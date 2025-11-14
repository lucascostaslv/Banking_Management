DELIMITER $$

-- Procedure genérica para transferir fundos e registrar a transação
-- (Sua procedure original estava boa, apenas ajustei os nomes das colunas de transação)
CREATE PROCEDURE transfer_funds(
    IN p_fromId VARCHAR(12),
    IN p_toId   VARCHAR(12),
    IN p_amount DECIMAL(13,2),
    IN p_type VARCHAR(63),
    INOUT p_transaction_id VARCHAR(17), -- Alterado para INOUT
    OUT p_status VARCHAR(50)
)
proc_label: BEGIN
    DECLARE v_balance DECIMAL(13,2);
    
    IF (SELECT COUNT(*) FROM tb_account WHERE id = p_fromId) = 0 THEN
        SET p_status = 'ACCOUNT_FROM_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    IF (SELECT COUNT(*) FROM tb_account WHERE id = p_toId) = 0 THEN
        SET p_status = 'ACCOUNT_TO_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    SELECT balance INTO v_balance
    FROM tb_account
    WHERE id = p_fromId
    FOR UPDATE;
    
    IF v_balance < p_amount THEN
        SET p_status = 'INSUFFICIENT_FUNDS';
        LEAVE proc_label;
    END IF;
    
    UPDATE tb_account SET balance = balance - p_amount WHERE id = p_fromId;
    UPDATE tb_account SET balance = balance + p_amount WHERE id = p_toId;
    
    SET p_transaction_id = CONCAT(DATE_FORMAT(NOW(), '%d%Y%m%H%i%s'), LPAD(FLOOR(MICROSECOND(NOW(3))/1000), 3, '0'));

    -- Registra transação (PIX e Transferência têm origem e destino)
    -- O 'tipo' boleto será tratado pela procedure pay_ticket
    IF p_type = 'PIX' THEN
		INSERT INTO tb_transaction(id, type, transaction_value, origin_account_id, target_account_id, payment_date)
		VALUES (p_transaction_id, p_type, p_amount, p_fromId, p_toId, NOW());
    END IF;
    
    SET p_status = 'SUCCESS';
END proc_label$$

-- Procedure para pagamento via PIX (CORRIGIDA)
CREATE PROCEDURE pix_payment(
    IN p_fromId VARCHAR(12),
    IN p_pixKey_org VARCHAR(127),
    IN p_pixKey_trg VARCHAR(127),
    IN p_amount DECIMAL(13,2),
    OUT p_result VARCHAR(50) -- CORRIGIDO: Retorna ID ou Erro
)
proc_label: BEGIN
    DECLARE v_toId VARCHAR(12);
    DECLARE v_transaction_id VARCHAR(17);
    DECLARE v_status_transfer VARCHAR(50);
    
    -- Busca conta pela chave PIX (incluindo CPF)
    SELECT pk.id INTO v_toId
    FROM tb_pix_key pk
    LEFT JOIN tb_account a ON pk.id = a.id
    LEFT JOIN tb_client c ON a.client_id = c.id
    LEFT JOIN tb_user u ON c.id = u.id
    WHERE p_pixKey_trg IN (pk.email, pk.phone_number, pk.rand_key, u.cpf)
    LIMIT 1;    
    
    IF v_toId IS NULL THEN
        SET p_result = 'PIX_KEY_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    -- Chama transferência
    CALL transfer_funds(p_fromId, v_toId, p_amount, 'PIX', v_transaction_id, v_status_transfer);
    
    IF v_status_transfer = 'SUCCESS' THEN
        INSERT INTO tb_pix(id, key_org, key_trg)
        VALUES (v_transaction_id, p_pixKey_org, p_pixKey_trg);
        
        SET p_result = v_transaction_id; -- Retorna o ID da Transação
    ELSE
        SET p_result = v_status_transfer; -- Retorna o erro (ex: 'INSUFFICIENT_FUNDS')
    END IF;
END proc_label$$

-- Procedure para pagamento de boleto (ticket) (CORRIGIDA)
CREATE PROCEDURE pay_ticket(
    IN p_payingAccountId VARCHAR(12), -- ID da conta que está pagando
    IN p_ticketId VARCHAR(17),        -- ID do boleto (que é o ID da transação)
    OUT p_status VARCHAR(50)
)
proc_label: BEGIN
    DECLARE v_receivingAccountId VARCHAR(12); -- Conta que vai receber
    DECLARE v_amount DECIMAL(13, 2);
    DECLARE v_balance DECIMAL(13,2);
    DECLARE v_isPaid INT;
    
    -- Busca o boleto e verifica se ele já foi pago (target_account_id IS NOT NULL)
    SELECT origin_account_id, transaction_value, (target_account_id IS NOT NULL)
    INTO v_receivingAccountId, v_amount, v_isPaid
    FROM tb_transaction
    WHERE id = p_ticketId AND type = 'ticket';
    
    IF v_receivingAccountId IS NULL THEN
        SET p_status = 'TICKET_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    IF v_isPaid = 1 THEN
        SET p_status = 'TICKET_ALREADY_PAID';
        LEAVE proc_label;
    END IF;
    
    -- Verifica saldo da conta pagadora
    SELECT balance INTO v_balance
    FROM tb_account
    WHERE id = p_payingAccountId
    FOR UPDATE;
    
    IF v_balance < v_amount THEN
        SET p_status = 'INSUFFICIENT_FUNDS';
        LEAVE proc_label;
    END IF;
    
    -- Efetua a transação
    UPDATE tb_account SET balance = balance - v_amount WHERE id = p_payingAccountId;
    UPDATE tb_account SET balance = balance + v_amount WHERE id = v_receivingAccountId;
    
    -- Atualiza o boleto (transação) marcando quem pagou e a data
    UPDATE tb_transaction
    SET 
        target_account_id = p_payingAccountId,
        payment_date = NOW()
    WHERE id = p_ticketId;
    
    SET p_status = 'SUCCESS';
END proc_label$$

DELIMITER ;
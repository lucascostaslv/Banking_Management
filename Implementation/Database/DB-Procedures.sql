#duas procedures sobre transacao, uma pix e outra boletos

DELIMITER $$
-- Procedure genérica para transferir fundos e registrar a transação
CREATE PROCEDURE transfer_funds(
    IN p_fromId VARCHAR(10),
    IN p_toId   VARCHAR(10),
    IN p_amount DECIMAL(13,2),
    IN p_type VARCHAR(63),
    INOUT p_transaction_id VARCHAR(17), -- Alterado para INOUT
    OUT p_status VARCHAR(50)
)
proc_label: BEGIN
    DECLARE v_balance DECIMAL(13,2);
    -- Conta origem existe?
    IF (SELECT COUNT(*) FROM tb_account WHERE id = p_fromId) = 0 THEN
        SET p_status = 'ACCOUNT_FROM_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    -- Conta destino existe?
    IF (SELECT COUNT(*) FROM tb_account WHERE id = p_toId) = 0 THEN
        SET p_status = 'ACCOUNT_TO_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    -- Verifica saldo e trava linha
    SELECT balance INTO v_balance
    FROM tb_account
    WHERE id = p_fromId
    FOR UPDATE;
    
    IF v_balance < p_amount THEN
        SET p_status = 'INSUFFICIENT_FUNDS';
        LEAVE proc_label;
    END IF;
    
    -- Débita / credita
    UPDATE tb_account SET balance = balance - p_amount WHERE id = p_fromId;
    UPDATE tb_account SET balance = balance + p_amount WHERE id = p_toId;
    
    -- Gera um ID para a transação baseado no timestamp
    SET p_transaction_id = CONCAT(DATE_FORMAT(NOW(), '%d%Y%m%H%i%s'), LPAD(FLOOR(MICROSECOND(NOW(3))/1000), 3, '0'));

    -- Registra transação
    INSERT INTO tb_transaction(id, type, transaction_value, origin_account_id, target_account_id)
    VALUES (p_transaction_id, p_type, p_amount, p_fromId, p_toId);
    
    SET p_status = 'SUCCESS';
END proc_label$$

-- Procedure para pagamento via PIX
CREATE PROCEDURE pix_payment(
    IN p_fromId VARCHAR(10),
    IN p_pixKey_org VARCHAR(127),
    IN p_pixKey_trg VARCHAR(127),
    IN p_amount DECIMAL(13,2),
    OUT p_status VARCHAR(50)
)
proc_label: BEGIN
    DECLARE v_toId VARCHAR(10);
    DECLARE v_transaction_id VARCHAR(17);
    
    -- Busca conta pela chave PIX
    SELECT pk.id INTO v_toId
    FROM tb_pixKey pk
    JOIN tb_account a ON pk.id = a.id
    JOIN tb_client c ON a.client_id = c.id
    JOIN tb_user u ON c.id = u.id
    WHERE p_pixKey_trg IN (pk.email, pk.phone_number, pk.rand_key, u.cpf)
    LIMIT 1;    
    
    IF v_toId IS NULL THEN
        SET p_status = 'PIX_KEY_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    -- Chama transferência, definindo o tipo como 'PIX'
    CALL transfer_funds(p_fromId, v_toId, p_amount, 'PIX', v_transaction_id, p_status);
    
    -- Se a transferência foi bem-sucedida, registra os detalhes na tabela tb_pix
    IF p_status = 'SUCCESS' THEN
        INSERT INTO tb_pix(id, key_org, key_trg)
        VALUES (
            v_transaction_id,
            p_pixKey_org,
            p_pixKey_trg
        );
    END IF;
END proc_label$$

-- Procedure para pagamento de boleto (ticket)
CREATE PROCEDURE pay_ticket(
    IN p_fromId VARCHAR(10),
    IN p_bars_code VARCHAR(255),
    OUT p_status VARCHAR(50)
)
proc_label: BEGIN
    DECLARE v_toId VARCHAR(10);
    DECLARE v_amount DECIMAL(13, 2);
    DECLARE v_ticket_id VARCHAR(17);
    DECLARE v_due_date DATE;
    DECLARE v_transaction_id VARCHAR(17); -- Para a chamada de transfer_funds
    
    -- Busca o boleto (ticket) e os detalhes da transação associada
    SELECT t.id, t.due_date, tr.target_account_id, tr.transaction_value
    INTO v_ticket_id, v_due_date, v_toId, v_amount
    FROM tb_ticket t JOIN tb_transaction tr ON t.id = tr.id
    WHERE t.bars_code = p_bars_code AND tr.origin_account_id IS NULL; -- Garante que o boleto ainda não foi pago
    
    IF v_ticket_id IS NULL THEN
        SET p_status = 'TICKET_NOT_FOUND_OR_PAID';
        LEAVE proc_label;
    END IF;
    
    IF v_due_date < CURDATE() THEN
        SET p_status = 'TICKET_EXPIRED';
        LEAVE proc_label;
    END IF;
    
    -- Deleta a transação de cobrança original, pois transfer_funds criará a transação de pagamento correta.
    DELETE FROM tb_ticket WHERE id = v_ticket_id;
    DELETE FROM tb_transaction WHERE id = v_ticket_id;
    
    -- Chama a procedure de transferência para efetuar o pagamento
    CALL transfer_funds(p_fromId, v_toId, v_amount, 'TICKET', v_transaction_id, p_status);
    
    -- O status final é o status retornado por transfer_funds.
    -- SET p_status = 'SUCCESS'; -- Não é mais necessário
END proc_label$$

DELIMITER ;
#duas procedures sobre transacao, uma pix e outra boletos

DELIMITER $$

CREATE PROCEDURE transfer_funds(
    IN p_fromId VARCHAR(10),
    IN p_toId   VARCHAR(10),
    IN p_amount DECIMAL(13,2),
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
    
    -- Registra transação
    INSERT INTO tb_transaction(from_acc, to_acc, value, date)
    VALUES (p_fromId, p_toId, p_amount, NOW());
    
    SET p_status = 'SUCCESS';
END proc_label$$

CREATE PROCEDURE pix_payment(
    IN p_fromId VARCHAR(10),
    IN p_pixKey VARCHAR(127),
    IN p_amount DECIMAL(13,2),
    OUT p_status VARCHAR(50)
)
proc_label: BEGIN
    DECLARE v_toId VARCHAR(10);
    
    -- Busca conta pela chave PIX
    SELECT id INTO v_toId
    FROM tb_pixKey
    WHERE email = p_pixKey
       OR phone_number = p_pixKey
       OR rand_key = p_pixKey
    LIMIT 1;
    
    IF v_toId IS NULL THEN
        SET p_status = 'PIX_KEY_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    -- Chama transferência
    CALL transfer_funds(p_fromId, v_toId, p_amount, p_status);
END proc_label$$

CREATE PROCEDURE pay_boleto(
    IN p_fromId VARCHAR(10),
    IN p_boletoId VARCHAR(20),
    OUT p_status VARCHAR(50)
)
proc_label: BEGIN
    DECLARE v_toId VARCHAR(10);
    DECLARE v_amount DECIMAL(13,2);
    DECLARE v_state VARCHAR(10);
    
    -- Busca boleto
    SELECT account_id, value, state
    INTO v_toId, v_amount, v_state
    FROM tb_boleto
    WHERE id = p_boletoId;
    
    IF v_toId IS NULL THEN
        SET p_status = 'BOLETO_NOT_FOUND';
        LEAVE proc_label;
    END IF;
    
    IF v_state <> 'OPEN' THEN
        SET p_status = 'INVALID_BOLETO';
        LEAVE proc_label;
    END IF;
    
    -- Realiza pagamento via transferência
    CALL transfer_funds(p_fromId, v_toId, v_amount, p_status);
    
    IF p_status = 'SUCCESS' THEN
        UPDATE tb_boleto
        SET state = 'PAID', pay_date = NOW()
        WHERE id = p_boletoId;
    END IF;
END proc_label$$

DELIMITER ;
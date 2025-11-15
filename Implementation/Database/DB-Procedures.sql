DELIMITER $$

DROP PROCEDURE IF EXISTS executePixPayment;
$$
DROP PROCEDURE IF EXISTS executeTicketPayment;
$$

-- Procedure para pagamento via PIX (CORRIGIDA)
CREATE PROCEDURE executePixPayment(
    IN p_origin_account_id VARCHAR(12),
    IN p_pix_key VARCHAR(127),
    IN p_value DECIMAL(13, 2),
    IN p_transaction_id VARCHAR(17),
    OUT p_status VARCHAR(50)
)
BEGIN
    DECLARE v_origin_balance DECIMAL(13, 2);
    DECLARE v_destination_account_id VARCHAR(12);
    DECLARE v_destination_balance DECIMAL(13, 2);
    DECLARE v_origin_pix_key VARCHAR(127);

    -- Encontra a conta de destino usando a chave PIX
    SELECT a.id INTO v_destination_account_id
    FROM tb_account a
    JOIN tb_pix_key pk ON a.id = pk.id
    WHERE pk.email = p_pix_key OR pk.phone_number = p_pix_key OR pk.rand_key = p_pix_key
    LIMIT 1;

    IF v_destination_account_id IS NULL THEN
        SET p_status = 'PIX_KEY_NOT_FOUND';
    ELSE
        START TRANSACTION;

        SELECT balance INTO v_origin_balance 
        FROM tb_account 
        WHERE id = p_origin_account_id 
        FOR UPDATE;

        IF v_origin_balance < p_value THEN
            SET p_status = 'INSUFFICIENT_FUNDS';
            ROLLBACK;
        ELSE
            SELECT balance INTO v_destination_balance 
            FROM tb_account 
            WHERE id = v_destination_account_id 
            FOR UPDATE;

            SELECT COALESCE(email, phone_number, rand_key) INTO v_origin_pix_key
            FROM tb_pix_key
            WHERE id = p_origin_account_id
            LIMIT 1;

            UPDATE tb_account 
            SET balance = v_origin_balance - p_value 
            WHERE id = p_origin_account_id;
            
            UPDATE tb_account 
            SET balance = v_destination_balance + p_value 
            WHERE id = v_destination_account_id;

            INSERT INTO tb_transaction (id, origin_account_id, target_account_id, transaction_value, payment_date, type, status)
            VALUES (p_transaction_id, p_origin_account_id, v_destination_account_id, p_value, NOW(3), 'PIX', 'SUCCESS');

            INSERT INTO tb_pix (id, key_org, key_trg)
            VALUES (p_transaction_id, COALESCE(v_origin_pix_key, 'N/A'), p_pix_key);

            SET p_status = 'SUCCESS';
            COMMIT;
        END IF;
    END IF;
END$$

-- Procedure para pagamento de boleto (CORRIGIDA)
CREATE PROCEDURE executeTicketPayment(
    IN p_paying_account_id VARCHAR(12),
    IN p_ticket_id VARCHAR(17),
    OUT p_status VARCHAR(50)
)
BEGIN
    DECLARE v_ticket_value DECIMAL(13, 2);
    DECLARE v_payer_balance DECIMAL(13, 2);
    DECLARE v_receiving_account_id VARCHAR(12);
    DECLARE v_ticket_status VARCHAR(50);
    DECLARE v_ticket_exists INT;

    START TRANSACTION;

    -- Verifica se o boleto existe
    SELECT COUNT(*) INTO v_ticket_exists
    FROM tb_ticket
    WHERE id = p_ticket_id;

    IF v_ticket_exists = 0 THEN
        SET p_status = 'TICKET_NOT_FOUND';
        ROLLBACK;
    ELSE
        -- Busca informações do boleto
        SELECT t.transaction_value, t.origin_account_id, t.status
        INTO v_ticket_value, v_receiving_account_id, v_ticket_status
        FROM tb_transaction t
        WHERE t.id = p_ticket_id AND t.type = 'TICKET'
        FOR UPDATE;

        -- Verifica se já foi pago
        IF v_ticket_status = 'PAID' THEN
            SET p_status = 'ALREADY_PAID';
            ROLLBACK;
        ELSEIF v_ticket_status = 'EXPIRED' THEN
            SET p_status = 'EXPIRED';
            ROLLBACK;
        ELSE
            -- Verifica saldo
            SELECT balance INTO v_payer_balance 
            FROM tb_account 
            WHERE id = p_paying_account_id 
            FOR UPDATE;

            IF v_payer_balance < v_ticket_value THEN
                SET p_status = 'INSUFFICIENT_FUNDS';
                ROLLBACK;
            ELSE
                -- Efetua o pagamento
                UPDATE tb_account 
                SET balance = v_payer_balance - v_ticket_value 
                WHERE id = p_paying_account_id;
                
                UPDATE tb_account 
                SET balance = balance + v_ticket_value 
                WHERE id = v_receiving_account_id;

                -- Atualiza o boleto
                UPDATE tb_transaction 
                SET target_account_id = p_paying_account_id,
                    payment_date = NOW(3),
                    status = 'PAID'
                WHERE id = p_ticket_id;

                SET p_status = 'SUCCESS';
                COMMIT;
            END IF;
        END IF;
    END IF;
END$$

DELIMITER ;
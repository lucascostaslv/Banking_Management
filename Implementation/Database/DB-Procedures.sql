DELIMITER $$

DROP PROCEDURE IF EXISTS executePixPayment;
$$
DROP PROCEDURE IF EXISTS executeTicketPayment;
$$

-- Procedure para pagamento via PIX (CORRIGIDA)
CREATE PROCEDURE executePixPayment(
    IN p_origin_account_id VARCHAR(12),  -- ID da conta de origem (corrigido tamanho)
    IN p_pix_key VARCHAR(127),           -- Chave PIX de destino
    IN p_value DECIMAL(13, 2),           -- Valor da transação (corrigido tamanho)
    IN p_transaction_id VARCHAR(17),     -- ID da transação
    OUT p_status VARCHAR(50)             -- Parâmetro de SAÍDA para o status
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

    -- Se não encontrar a chave, define o status e encerra
    IF v_destination_account_id IS NULL THEN
        SET p_status = 'PIX_KEY_NOT_FOUND';
    ELSE
        -- Inicia uma transação para garantir a atomicidade
        START TRANSACTION;

        -- Obtém o saldo da conta de origem com bloqueio
        SELECT balance INTO v_origin_balance 
        FROM tb_account 
        WHERE id = p_origin_account_id 
        FOR UPDATE;

        -- Verifica se há saldo suficiente
        IF v_origin_balance < p_value THEN
            SET p_status = 'INSUFFICIENT_FUNDS';
            ROLLBACK;
        ELSE
            -- Obtém o saldo da conta de destino com bloqueio
            SELECT balance INTO v_destination_balance 
            FROM tb_account 
            WHERE id = v_destination_account_id 
            FOR UPDATE;

            -- Busca a chave PIX da conta de origem para registrar
            SELECT COALESCE(email, phone_number, rand_key) INTO v_origin_pix_key
            FROM tb_pix_key
            WHERE id = p_origin_account_id
            LIMIT 1;

            -- Realiza as transferências
            UPDATE tb_account 
            SET balance = v_origin_balance - p_value 
            WHERE id = p_origin_account_id;
            
            UPDATE tb_account 
            SET balance = v_destination_balance + p_value 
            WHERE id = v_destination_account_id;

            -- Insere o registro da transação (origem = quem paga, target = quem recebe)
            INSERT INTO tb_transaction (id, origin_account_id, target_account_id, transaction_value, payment_date, type)
            VALUES (p_transaction_id, p_origin_account_id, v_destination_account_id, p_value, NOW(3), 'PIX');

            -- CORREÇÃO: Insere o registro na tabela tb_pix
            INSERT INTO tb_pix (id, key_org, key_trg)
            VALUES (p_transaction_id, COALESCE(v_origin_pix_key, 'N/A'), p_pix_key);

            SET p_status = 'SUCCESS';
            COMMIT;
        END IF;
    END IF;
END$$

-- Procedure para pagamento de boleto (CORRIGIDA)
CREATE PROCEDURE executeTicketPayment(
    IN p_paying_account_id VARCHAR(12),  -- ID da conta que está pagando (corrigido tamanho)
    IN p_ticket_id VARCHAR(17),          -- ID do boleto
    OUT p_status VARCHAR(50)             -- Parâmetro de SAÍDA para o status
)
BEGIN
    DECLARE v_ticket_value DECIMAL(13, 2);
    DECLARE v_payer_balance DECIMAL(13, 2);
    DECLARE v_receiving_account_id VARCHAR(12);
    DECLARE v_payment_date TIMESTAMP;
    DECLARE v_ticket_exists INT;

    -- Inicia uma transação
    START TRANSACTION;

    -- CORREÇÃO: Verifica se o boleto existe na tb_ticket
    SELECT COUNT(*) INTO v_ticket_exists
    FROM tb_ticket
    WHERE id = p_ticket_id;

    IF v_ticket_exists = 0 THEN
        SET p_status = 'TICKET_NOT_FOUND';
        ROLLBACK;
    ELSE
        -- Busca o boleto e verifica se já foi pago
        SELECT t.transaction_value, t.origin_account_id, t.payment_date
        INTO v_ticket_value, v_receiving_account_id, v_payment_date
        FROM tb_transaction t
        WHERE t.id = p_ticket_id AND t.type = 'TICKET'
        FOR UPDATE;

        -- CORREÇÃO: Verifica se já foi pago checando se target_account_id está preenchido
        IF v_payment_date IS NOT NULL AND EXISTS (
            SELECT 1 FROM tb_transaction 
            WHERE id = p_ticket_id AND target_account_id IS NOT NULL
        ) THEN
            SET p_status = 'ALREADY_PAID';
            ROLLBACK;
        ELSE
            -- Verifica o saldo da conta pagadora
            SELECT balance INTO v_payer_balance 
            FROM tb_account 
            WHERE id = p_paying_account_id 
            FOR UPDATE;

            IF v_payer_balance < v_ticket_value THEN
                SET p_status = 'INSUFFICIENT_FUNDS';
                ROLLBACK;
            ELSE
                -- Efetua a transação
                UPDATE tb_account 
                SET balance = v_payer_balance - v_ticket_value 
                WHERE id = p_paying_account_id;
                
                UPDATE tb_account 
                SET balance = balance + v_ticket_value 
                WHERE id = v_receiving_account_id;

                -- CORREÇÃO: Atualiza o boleto marcando quem pagou e a data
                UPDATE tb_transaction 
                SET target_account_id = p_paying_account_id,
                    payment_date = NOW(3)
                WHERE id = p_ticket_id;

                SET p_status = 'SUCCESS';
                COMMIT;
            END IF;
        END IF;
    END IF;
END$$

DELIMITER ;
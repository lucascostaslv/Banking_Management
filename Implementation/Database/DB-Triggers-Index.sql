#Uma das triggers tem que ser sobre colocar tipo a tb_usergroup
#Uma trigger para quando desativar a conta, ele deletar as chaves pix

DELIMITER $
CREATE TRIGGER insert_group_client
AFTER INSERT ON payment_bank.tb_client
FOR EACH ROW
BEGIN
    INSERT INTO tb_userGroup(id, type) VALUES(NEW.id, 2);
END$

CREATE TRIGGER insert_group_manager
AFTER INSERT ON payment_bank.tb_manager
FOR EACH ROW
BEGIN
    INSERT INTO tb_userGroup(id, type) VALUES(NEW.id, 1);
END$

CREATE TRIGGER remove_pixkeys_account
AFTER UPDATE ON payment_bank.tb_client
FOR EACH ROW
BEGIN
	IF NEW.act = 0 THEN
		-- Ao desativar um cliente (act=0), deleta as chaves PIX associadas às suas contas.
		DELETE FROM tb_pix_Key 
        WHERE id IN (SELECT id FROM tb_account WHERE client_id = NEW.id);
    END IF;
END$
DELIMITER ;

-- Adicionando índice para melhorar performance
CREATE INDEX idx_transaction_type ON tb_transaction(type);
CREATE INDEX idx_transaction_status ON tb_transaction(status);
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
		UPDATE tb_pixKey 
		SET email = NULL, phone_number = NULL, randKey = NULL
        WHERE id = NEW.id;
    END IF;
END$
DELIMITER ;
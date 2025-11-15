CREATE USER 'system_access'@'localhost' IDENTIFIED BY '12345';

CREATE USER 'employee'@'localhost' IDENTIFIED BY 'admin0';

CREATE ROLE IF NOT EXISTS 'crud_user';
-- Adicionada a permissão EXECUTE para permitir a chamada de Stored Procedures
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON payment_bank.* TO 'crud_user';

CREATE ROLE IF NOT EXISTS 'admin';
GRANT SELECT, INSERT, UPDATE, CREATE, ALTER, INDEX, CREATE VIEW, SHOW VIEW
ON payment_bank.* TO 'admin';

GRANT 'crud_user' TO 'system_access'@'localhost';
GRANT 'admin' TO 'employee'@'localhost';

SET DEFAULT ROLE 'crud_user' TO 'system_access'@'localhost';
SET DEFAULT ROLE 'admin' TO 'employee'@'localhost';
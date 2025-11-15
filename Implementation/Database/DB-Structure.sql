CREATE DATABASE IF NOT EXISTS payment_bank;
USE payment_bank;


CREATE TABLE tb_user (
	id VARCHAR(9) PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
	first_name VARCHAR(63) NOT NULL,
    last_name VARCHAR(127) NOT NULL,
    birth_day DATE NOT NULL
);

CREATE TABLE tb_userGroup (
	id VARCHAR(9) PRIMARY KEY,
	type INT NOT NULL, 

	CONSTRAINT FK_user_in_userGroup
	FOREIGN KEY (id) REFERENCES tb_user (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_client (
	id VARCHAR(9) PRIMARY KEY,
	act INT NOT NULL DEFAULT 1,
    state VARCHAR(2),
    
    CONSTRAINT FK_user_client
    FOREIGN KEY (id) REFERENCES tb_user (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_manager (
	id VARCHAR(9) PRIMARY KEY,
	role VARCHAR(255),
    
    CONSTRAINT FK_user_manager
    FOREIGN KEY (id) REFERENCES tb_user (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_account (
	id VARCHAR(12) PRIMARY KEY, -- CORRIGIDO: DE 10 PARA 12
    account_number INT NOT NULL,
    balance DECIMAL(13,2) NOT NULL DEFAULT 0.0,
	type VARCHAR(63) NOT NULL,
    open_date TIMESTAMP NOT NULL,
    client_id VARCHAR(9),

    CONSTRAINT FK_account_client
    FOREIGN KEY (client_id) REFERENCES tb_client (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_pix_key ( -- CORRIGIDO: de tb_pixKey para tb_pix_key
	id VARCHAR(12) PRIMARY KEY, -- CORRIGIDO: DE 10 PARA 12
    email VARCHAR(127) UNIQUE,
    phone_number VARCHAR(14),
    rand_key VARCHAR(63),
    
    CONSTRAINT FK_pix_key_account -- CORRIGIDO
    FOREIGN KEY (id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_current (
	id VARCHAR(12) PRIMARY KEY, -- CORRIGIDO: DE 10 PARA 12
    monthly_tax DECIMAL(4,2) NOT NULL DEFAULT 0.52,
    
    CONSTRAINT FK_current_account 
    FOREIGN KEY (id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_savings (
	id VARCHAR(12) PRIMARY KEY, -- CORRIGIDO: DE 10 PARA 12
    return_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_amount DECIMAL(9,2) NOT NULL,
    
    CONSTRAINT FK_savings_account 
    FOREIGN KEY (id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_transaction (
	id VARCHAR(17) PRIMARY KEY,
    type VARCHAR(63) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_date TIMESTAMP NULL DEFAULT NULL,
    transaction_value DECIMAL(13,2) NOT NULL,
    
    -- LÓGICA CORRIGIDA:
    origin_account_id VARCHAR(12) NOT NULL, -- Quem recebe (no boleto) ou quem paga (no pix)
    target_account_id VARCHAR(12) NULL,          -- Quem paga (no boleto) ou quem recebe (no pix)
    
    CONSTRAINT FK_transaction_OGaccount 
    FOREIGN KEY (origin_account_id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT,
    
    CONSTRAINT FK_transaction_TGaccount 
    FOREIGN KEY (target_account_id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_pix (
	id VARCHAR(17) PRIMARY KEY,
    key_org VARCHAR(127) NOT NULL,
    key_trg VARCHAR(127) NOT NULL,
    
    CONSTRAINT FK_pix_transaction
    FOREIGN KEY (id) REFERENCES tb_transaction(id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_ticket (
	id VARCHAR(17) PRIMARY KEY,
	bars_code VARCHAR(255) NOT NULL,
    due_date DATE NOT NULL,
    
    CONSTRAINT FK_ticket_transaction
    FOREIGN KEY (id) REFERENCES tb_transaction(id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);
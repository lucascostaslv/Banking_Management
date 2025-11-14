CREATE DATABASE IF NOT EXISTS payment_bank;
USE payment_bank;

CREATE TABLE tb_user ( #ok
	id VARCHAR(9) PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
	first_name VARCHAR(63) NOT NULL,
    last_name VARCHAR(127) NOT NULL,
    birth_day DATE NOT NULL
);

# 1 para maganer 2 para client
CREATE TABLE tb_userGroup ( #nao criado obvio
	id VARCHAR(9) PRIMARY KEY,
	type INT NOT NULL, 

	CONSTRAINT FK_user_in_userGroup
	FOREIGN KEY (id) REFERENCES tb_user (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_client ( #ok
	id VARCHAR(9) PRIMARY KEY,
	act INT NOT NULL DEFAULT 1,
    state VARCHAR(2),
    
    CONSTRAINT FK_user_client
    FOREIGN KEY (id) REFERENCES tb_user (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_manager ( #ok
	id VARCHAR(9) PRIMARY KEY,
	role VARCHAR(255),
    
    CONSTRAINT FK_user_manager
    FOREIGN KEY (id) REFERENCES tb_user (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_account ( #Faltou pixkeys
	id VARCHAR(10) PRIMARY KEY,
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

CREATE TABLE tb_pixKey ( #ok
	id VARCHAR(10) PRIMARY KEY,
    email VARCHAR(127) UNIQUE,
    phone_number VARCHAR(14), -- +5500900000000
    rand_key VARCHAR(63),
    
    CONSTRAINT FK_pixKey_account 
    FOREIGN KEY (id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_current ( #ok
	id VARCHAR(10) PRIMARY KEY,
    monthly_tax DECIMAL(4,2) NOT NULL DEFAULT 0.52,
    
    CONSTRAINT FK_current_account 
    FOREIGN KEY (id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_savings ( #ok
	id VARCHAR(10) PRIMARY KEY,
    return_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_amount DECIMAL(9,2) NOT NULL,
    
    CONSTRAINT FK_savings_account 
    FOREIGN KEY (id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_transaction ( #ok
	id VARCHAR(17) PRIMARY KEY, -- "ddyyyyMMHHmmssSSS"
    type VARCHAR(63) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_value DECIMAL(13,2) NOT NULL,
    
    origin_account_id VARCHAR(10),
    target_account_id VARCHAR(10) NOT NULL,
    
    CONSTRAINT FK_transaction_OGaccount 
    FOREIGN KEY (origin_account_id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT,
    
    CONSTRAINT FK_transaction_TGaccount 
    FOREIGN KEY (target_account_id) REFERENCES tb_account (id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_pix ( #ok
	id VARCHAR(17) PRIMARY KEY,
    key_org VARCHAR(127) NOT NULL,
    key_trg VARCHAR(127) NOT NULL,
    
    CONSTRAINT FK_pix_transaction
    FOREIGN KEY (id) REFERENCES tb_transaction(id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

CREATE TABLE tb_ticket ( #ok
	id VARCHAR(17) PRIMARY KEY,
	bars_code VARCHAR(255) NOT NULL,
    due_date DATE NOT NULL,
    
    CONSTRAINT FK_ticket_transaction
    FOREIGN KEY (id) REFERENCES tb_transaction(id)
		ON UPDATE CASCADE  
        ON DELETE RESTRICT
);

use payment_bank;

CREATE VIEW vw_client_account_summary AS
SELECT
    c.id AS client_id,
    u.first_name,
    u.last_name,
    c.act AS active_status,
    a.id AS account_id,
    a.account_number,
    a.balance
FROM tb_client c
JOIN tb_user u ON c.id = u.id
JOIN tb_account a ON c.id = a.client_id
WHERE c.act = 1;

CREATE VIEW vw_transaction_report AS
SELECT
    t.id AS transaction_id,
    t.payment_date,
    t.transaction_value,
    t.type,
    t.status,
    t.origin_account_id,
    t.target_account_id,
    COALESCE(px.key_trg, 'N/A') AS target_pix_key,
    COALESCE(tk.bars_code, 'N/A') AS ticket_code   
FROM tb_transaction t
LEFT JOIN tb_pix px ON t.id = px.id
LEFT JOIN tb_ticket tk ON t.id = tk.id;
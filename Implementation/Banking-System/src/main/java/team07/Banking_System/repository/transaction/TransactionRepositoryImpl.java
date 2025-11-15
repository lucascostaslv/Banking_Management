package team07.Banking_System.repository.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Map;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String executePixPayment(String originAccountId, String pixKey, BigDecimal value, String transactionId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("payment_bank") // Adiciona o schema
                .withProcedureName("executePixPayment")
                .declareParameters(
                        new SqlParameter("p_origin_account_id", Types.VARCHAR),
                        new SqlParameter("p_pix_key", Types.VARCHAR),
                        new SqlParameter("p_value", Types.DECIMAL),
                        new SqlParameter("p_transaction_id", Types.VARCHAR),
                        new SqlOutParameter("p_status", Types.VARCHAR) // Corrigido para parâmetro de SAÍDA
                )
                .withoutProcedureColumnMetaDataAccess(); // Evita a busca por metadados
        Map<String, Object> out = jdbcCall.execute(originAccountId, pixKey, value, transactionId);
        return (String) out.get("p_status");
    }

    @Override
    public String executeTicketPayment(String payingAccountId, String ticketId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("payment_bank") // Adiciona o schema
                .withProcedureName("executeTicketPayment") // Mantém o nome da procedure
                .declareParameters(
                        new SqlParameter("p_payingAccountId", Types.VARCHAR),
                        new SqlParameter("p_ticketId", Types.VARCHAR),
                        new SqlOutParameter("p_status", Types.VARCHAR) // Corrigido para parâmetro de SAÍDA
                )
                .withoutProcedureColumnMetaDataAccess(); // Evita a busca por metadados
        Map<String, Object> out = jdbcCall.execute(payingAccountId, ticketId);
        return (String) out.get("p_status");
    }
}
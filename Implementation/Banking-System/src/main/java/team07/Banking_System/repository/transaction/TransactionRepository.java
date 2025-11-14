package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.transaction.Transaction;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Chama a stored procedure executePixPayment para realizar um pagamento via PIX.
     * @return Uma string indicando o status da operação (ex: 'SUCCESS', 'INSUFFICIENT_FUNDS').
     */
    @Procedure(procedureName = "executePixPayment")
    String executePixPayment(
        @Param("p_origin_account_id") String originAccountId,
        @Param("p_pix_key") String pixKey,
        @Param("p_value") BigDecimal value,
        @Param("p_transaction_id") String transactionId
    );

    /**
     * Chama a stored procedure executeTicketPayment para realizar o pagamento de um boleto.
     * @return O status da operação (ex: 'SUCCESS', 'TICKET_NOT_FOUND').
     */
    @Procedure(procedureName = "executeTicketPayment")
    String executeTicketPayment(
            @Param("p_payingAccountId") String payingAccountId,
            @Param("p_ticketId") String ticketId
    );
    // --- FIM DA CORREÇÃO ---
}
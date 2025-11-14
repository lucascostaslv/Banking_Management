package team07.Banking_System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.transaction.Transaction;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Chama a stored procedure pix_payment para realizar um pagamento via PIX.
     * @return O status da operação (ex: 'SUCCESS', 'PIX_KEY_NOT_FOUND').
     */
    @Procedure(name = "pix_payment")
    String executePixPayment(
            @Param("p_fromId") String fromId,
            @Param("p_pixKey_org") String pixKeyOrg,
            @Param("p_pixKey_trg") String pixKeyTrg,
            @Param("p_amount") BigDecimal amount
    );

    /**
     * Chama a stored procedure pay_ticket para realizar o pagamento de um boleto.
     * @return O status da operação (ex: 'SUCCESS', 'TICKET_NOT_FOUND_OR_PAID').
     */
    @Procedure(name = "pay_ticket")
    String executeTicketPayment(
            @Param("p_fromId") String fromId,
            @Param("p_bars_code") String barsCode
    );

}

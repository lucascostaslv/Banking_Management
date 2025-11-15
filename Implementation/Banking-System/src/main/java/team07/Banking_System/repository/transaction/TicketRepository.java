package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team07.Banking_System.model.transaction.Ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findByOriginAccountId(String accountId);

    /**
     * Interface de projeção para buscar todos os dados necessários para a geração do boleto em PDF.
     * O Spring Data JPA implementará essa interface automaticamente com os resultados da query.
     */
    interface TicketDownloadProjection {
        String getRecipientAccountId();
        Integer getRecipientAccountNumber();
        String getRecipientFirstName();
        String getRecipientLastName();
        String getBarcode();
        BigDecimal getAmount();
        LocalDate getDueDate();
    }

    @Query(value = """
        SELECT
            t.origin_account_id as recipientAccountId,
            acc.account_number as recipientAccountNumber,
            u.first_name as recipientFirstName,
            u.last_name as recipientLastName,
            tk.bars_code as barcode,
            t.transaction_value as amount,
            tk.due_date as dueDate
        FROM tb_transaction t
        JOIN tb_ticket tk ON t.id = tk.id
        JOIN tb_account acc ON t.origin_account_id = acc.id
        JOIN tb_client c ON acc.client_id = c.id
        JOIN tb_user u ON c.id = u.id
        WHERE t.id = :ticketId
    """, nativeQuery = true)
    Optional<TicketDownloadProjection> findTicketDataForDownload(@Param("ticketId") String ticketId);
}
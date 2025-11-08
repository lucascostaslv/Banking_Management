package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import team07.Banking_System.model.transaction.Ticket;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findByOriginAccountId(String accountId);
}
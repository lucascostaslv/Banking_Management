package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.transaction.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String>{}

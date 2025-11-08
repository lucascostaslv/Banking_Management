package team07.Banking_System.services.transaction;

import team07.Banking_System.repository.transaction.TicketRepository;
import team07.Banking_System.model.transaction.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> listAllByAccountId(String accountId){
        return ticketRepository.findByOriginAccountId(accountId);
    }

    public Optional<Ticket> findTicket(String id){
        return ticketRepository.findById(id);
    }

    @Transactional
    public Ticket createTicket(Ticket ticket){
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(String id){
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isEmpty()) {
            throw new NoSuchElementException("Ticket com ID " + id + " não encontrado para exclusão.");
        }
        
        Ticket ticket = ticketOptional.get();

        if (ticket.getTargetAccount() != null) {
            throw new IllegalArgumentException("Não é possível deletar o Ticket: A conta de destino (Acc_trg) já está definida.");
        }

        // 3. Executa o DELETE
        ticketRepository.deleteById(id);
    }
}

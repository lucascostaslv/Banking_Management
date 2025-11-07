package team07.Banking_System.controller.transaction;

import team07.Banking_System.model.transaction.Ticket;
import team07.Banking_System.services.transaction.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{id}")
    public Optional<Ticket> findTicket(@PathVariable String id) {
        return ticketService.findTicket(id);
    }

    @GetMapping("/list/{acc_org}")
    public List<Ticket> listAll(@PathVariable Ticket acc_org) {
        return ticketService.listAll(acc_org);
    }

    @PostMapping
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable String id) {
        ticketService.deleteTicket(id);
    }
}

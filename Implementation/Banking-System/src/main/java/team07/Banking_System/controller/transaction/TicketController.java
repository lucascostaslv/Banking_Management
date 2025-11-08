package team07.Banking_System.controller.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team07.Banking_System.model.transaction.Ticket;
import team07.Banking_System.model.transaction.TicketDTO;
import team07.Banking_System.services.transaction.TicketService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> create(@RequestBody TicketDTO ticketDTO) {
        Ticket createdTicket = ticketService.createTicket(ticketDTO);
        return ResponseEntity.ok(createdTicket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> findById(@PathVariable String id) {
        return ticketService.findTicket(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/list/by-account/{accountId}")
    public ResponseEntity<List<Ticket>> listByAccount(@PathVariable String accountId) {
        List<Ticket> tickets = ticketService.listAllByAccountId(accountId);
        return ResponseEntity.ok(tickets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Ticket> pay(
            @PathVariable String id,
            @RequestBody Map<String, String> payload
    ) {
        String payingAccountId = payload.get("payingAccountId");
        if (payingAccountId == null) {
            return ResponseEntity.badRequest().build();
        }
        Ticket paidTicket = ticketService.payTicket(id, payingAccountId);
        return ResponseEntity.ok(paidTicket);
    }
}
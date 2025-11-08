package team07.Banking_System.services.transaction;

import team07.Banking_System.model.account.Account;
import team07.Banking_System.model.account.Current;
import team07.Banking_System.model.account.Savings;
import team07.Banking_System.repository.account.AccountRepository;
import team07.Banking_System.repository.transaction.TicketRepository;
import team07.Banking_System.model.transaction.Ticket;
import team07.Banking_System.model.transaction.TicketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team07.Banking_System.services.account.CurrentService;
import team07.Banking_System.services.account.SavingsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final AccountRepository accountRepository;
    private final CurrentService currentService;
    private final SavingsService savingsService;

    @Autowired
    public TicketService(TicketRepository ticketRepository, AccountRepository accountRepository, CurrentService currentService, SavingsService savingsService){
        this.ticketRepository = ticketRepository;
        this.accountRepository = accountRepository;
        this.currentService = currentService;
        this.savingsService = savingsService;
    }

    public List<Ticket> listAllByAccountId(String accountId){
        return ticketRepository.findByOriginAccountId(accountId);
    }

    public Optional<Ticket> findTicket(String id){
        return ticketRepository.findById(id);
    }

    @Transactional
    public Ticket createTicket(TicketDTO ticketDTO){
        // Validar dados
        if (ticketDTO.getOriginAccount() == null || ticketDTO.getOriginAccount().getId() == null) {
            throw new IllegalArgumentException("Conta de origem é obrigatória para criar o boleto.");
        }
        if (ticketDTO.getValue() == null || ticketDTO.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do boleto deve ser maior que zero.");
        }
        if (ticketDTO.getDueDate() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória.");
        }

        // Garante que a conta de origem existe
        Account originAccount = accountRepository.findById(ticketDTO.getOriginAccount().getId())
                .orElseThrow(() -> new NoSuchElementException("Conta de origem não encontrada para o boleto."));

        // Criar objeto Ticket
        Ticket ticket = new Ticket();
        ticket.generateAndSetId();
        ticket.setOriginAccount(originAccount);
        ticket.setValue(ticketDTO.getValue());
        ticket.setDue_date(ticketDTO.getDueDate());
        ticket.setType("ticket");
        
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket payTicket(String ticketId, String targetAccountId) {
        // 1. Validar e buscar entidades
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Boleto com ID " + ticketId + " não encontrado."));

        // Recarregar a conta de origem para garantir que não seja um proxy
        Account originAccount = accountRepository.findById(ticket.getOriginAccount().getId())
                .orElseThrow(() -> new NoSuchElementException("Conta de origem do boleto não encontrada."));
        Account targetAccount = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new NoSuchElementException("Conta de destino (pagamento) não encontrada."));

        // 2. Validar estado do boleto e saldo
        if (ticket.getTargetAccount() != null) {
            throw new IllegalStateException("Este boleto já foi pago.");
        }
        if (targetAccount.getBalance().compareTo(ticket.getValue()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta pagadora para quitar o boleto.");
        }

        // 3. Efetuar a transferência
        targetAccount.setBalance(targetAccount.getBalance().subtract(ticket.getValue()));
        originAccount.setBalance(originAccount.getBalance().add(ticket.getValue()));

        // 4. Atualizar o boleto
        ticket.setTargetAccount(targetAccount);
        ticket.setPayment_date(LocalDateTime.now());
        
        updateAccount(originAccount);
        updateAccount(targetAccount);

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

    private void updateAccount(Account account) {
        if (account instanceof Current) {
            currentService.updateAccount((Current) account);
        } else if (account instanceof Savings) {
            savingsService.updateAccount((Savings) account);
        } else {
            // Fallback ou erro, caso existam outros tipos de conta não tratados
            throw new IllegalStateException("Tipo de conta não suportado para atualização: " + account.getClass().getName());
        }
    }
}

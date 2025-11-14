package team07.Banking_System.services.transaction;

import team07.Banking_System.model.account.Account;
import team07.Banking_System.repository.account.AccountRepository;
import team07.Banking_System.repository.transaction.TicketRepository;
import team07.Banking_System.repository.transaction.TransactionRepository; // 1. IMPORTAR
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
import java.util.logging.Logger;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository; // 2. INJETAR

    private static final Logger logger = Logger.getLogger(TicketService.class.getName());

    @Autowired
    public TicketService(TicketRepository ticketRepository, AccountRepository accountRepository,
                         TransactionRepository transactionRepository, // 3. ADICIONAR
                         CurrentService currentService, SavingsService savingsService){
        this.ticketRepository = ticketRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Ticket> listAllByAccountId(String accountId){
        // Lista boletos ONDE A CONTA É A BENEFICIÁRIA (origin_account_id)
        return ticketRepository.findByOriginAccountId(accountId);
    }

    public Optional<Ticket> findTicket(String id){
        return ticketRepository.findById(id);
    }

    @Transactional
    public Ticket createTicket(TicketDTO ticketDTO){
        // Validar dados
        if (ticketDTO.getOriginAccount() == null || ticketDTO.getOriginAccount().getId() == null) {
            throw new IllegalArgumentException("Conta de origem (beneficiária) é obrigatória para criar o boleto.");
        }
        if (ticketDTO.getValue() == null || ticketDTO.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do boleto deve ser maior que zero.");
        }
        if (ticketDTO.getDueDate() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória.");
        }

        // Garante que a conta de origem existe
        Account originAccount = accountRepository.findById(ticketDTO.getOriginAccount().getId())
                .orElseThrow(() -> new NoSuchElementException("Conta de origem (beneficiária) não encontrada para o boleto."));

        // Criar objeto Ticket
        Ticket ticket = new Ticket();
        ticket.generateAndSetId();
        ticket.setOriginAccount(originAccount); // Conta que recebe
        // targetAccount (quem paga) fica NULO
        ticket.setValue(ticketDTO.getValue());
        ticket.setDue_date(ticketDTO.getDueDate());
        ticket.setType("ticket");
        
        // --- CORREÇÃO ---
        // Gerar um código de barras (usando o ID da transação)
        // O banco exige que 'bars_code' não seja nulo
        ticket.setBars_code(ticket.getId()); 
        
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket payTicket(String ticketId, String payingAccountId) {
        
        // 1. Validar IDs
        if (ticketId == null || payingAccountId == null) {
            throw new IllegalArgumentException("ID do Boleto e ID da Conta Pagadora são obrigatórios.");
        }

        // 2. CHAMAR A PROCEDURE
        logger.info("Chamando procedure pay_ticket...");
        String status = transactionRepository.executeTicketPayment(
            payingAccountId,
            ticketId
        );
        logger.info("Procedure executada. Status: " + status);
        
        // 3. Tratar o resultado
        switch (status) {
            case "SUCCESS":
                // Busca o boleto atualizado para retornar ao usuário
                return ticketRepository.findById(ticketId)
                        .orElseThrow(() -> new IllegalStateException("Boleto pago com sucesso, mas não encontrado para retorno."));
            case "TICKET_NOT_FOUND":
                throw new NoSuchElementException("Boleto com ID " + ticketId + " não encontrado.");
            case "TICKET_ALREADY_PAID":
                throw new IllegalStateException("Este boleto já foi pago.");
            case "INSUFFICIENT_FUNDS":
                throw new IllegalArgumentException("Saldo insuficiente na conta pagadora.");
            default:
                throw new IllegalStateException("Erro desconhecido no pagamento do boleto: " + status);
        }
    }

    @Transactional
    public void deleteTicket(String id){
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket com ID " + id + " não encontrado para exclusão."));

        if (ticket.getTargetAccount() != null) {
            throw new IllegalArgumentException("Não é possível deletar um boleto que já foi pago.");
        }

        ticketRepository.deleteById(id);
    }
}
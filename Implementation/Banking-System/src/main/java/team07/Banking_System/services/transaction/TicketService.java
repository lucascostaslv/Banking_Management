package team07.Banking_System.services.transaction;

import team07.Banking_System.model.account.Account;
import team07.Banking_System.repository.account.AccountRepository;
import team07.Banking_System.repository.transaction.TicketRepository;
import team07.Banking_System.repository.transaction.TransactionRepository;
import team07.Banking_System.model.transaction.Ticket;
import team07.Banking_System.model.transaction.TicketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import team07.Banking_System.services.account.CurrentService;
import team07.Banking_System.services.account.SavingsService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final HttpClient httpClient;

    @Value("${ticket.generator.api.url}")
    private String ticketApiBaseUrl;

    // Formatter para garantir que a data seja enviada no formato YYYY-MM-DD
    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Logger logger = Logger.getLogger(TicketService.class.getName());

    @Autowired
    public TicketService(TicketRepository ticketRepository, 
                         AccountRepository accountRepository,
                         TransactionRepository transactionRepository,
                         CurrentService currentService, 
                         SavingsService savingsService) {
        this.ticketRepository = ticketRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public List<Ticket> listAllByAccountId(String accountId) {
        // Lista boletos ONDE A CONTA É A BENEFICIÁRIA (origin_account_id)
        return ticketRepository.findByOriginAccountId(accountId);
    }

    public Optional<Ticket> findTicket(String id) {
        return ticketRepository.findById(id);
    }

    @Transactional
    public Ticket createTicket(TicketDTO ticketDTO) {
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

        // Criar objeto Ticket usando o construtor correto
        Ticket ticket = new Ticket(
            originAccount,           // Conta que vai receber (beneficiária)
            ticketDTO.getValue(),    // Valor do boleto
            generateBarsCode(),      // Gera código de barras
            ticketDTO.getDueDate()   // Data de vencimento
        );
        
        // Gerar ID manualmente
        String ticketId = "TK-" + System.currentTimeMillis();
        ticket.setId(ticketId);
        
        // Salvar no banco
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket payTicket(String ticketId, String payingAccountId) {
        // 1. Validar IDs
        if (ticketId == null || payingAccountId == null) {
            throw new IllegalArgumentException("ID do Boleto e ID da Conta Pagadora são obrigatórios.");
        }

        // 2. CHAMAR A PROCEDURE
        logger.info("Chamando procedure executeTicketPayment...");
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
            case "ALREADY_PAID":
                throw new IllegalStateException("Este boleto já foi pago.");
            case "EXPIRED":
                throw new IllegalStateException("Este boleto está vencido.");
            case "INSUFFICIENT_FUNDS":
                throw new IllegalArgumentException("Saldo insuficiente na conta pagadora.");
            default:
                throw new IllegalStateException("Erro desconhecido no pagamento do boleto: " + status);
        }
    }

    @Transactional
    public void deleteTicket(String id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket com ID " + id + " não encontrado para exclusão."));

        // MUDOU: agora usa getTargetAccount() da classe Transaction
        if (ticket.getTargetAccount() != null) {
            throw new IllegalArgumentException("Não é possível deletar um boleto que já foi pago.");
        }

        ticketRepository.deleteById(id);
    }

    // Método auxiliar para gerar código de barras
    private String generateBarsCode() {
        // Gera um código de barras simples (você pode melhorar isso)
        return String.format("%020d", System.currentTimeMillis() % 100000000000000000L);
    }

    public byte[] downloadTicketPdf(String ticketId) {
        // 1. Busca os dados do boleto no banco de dados principal usando a projeção
        TicketRepository.TicketDownloadProjection ticketData = ticketRepository.findTicketDataForDownload(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Dados para geração do boleto não encontrados para o ID: " + ticketId));

        // 2. Constrói a URL para a API Python de forma segura
        URI uri = UriComponentsBuilder.fromHttpUrl(ticketApiBaseUrl)
                .path("/download-ticket/{id_boleto}")
                .queryParam("id_conta", ticketData.getRecipientAccountId())
                .queryParam("n_conta", ticketData.getRecipientAccountNumber())
                .queryParam("first_name", ticketData.getRecipientFirstName())
                .queryParam("last_name", ticketData.getRecipientLastName())
                .queryParam("barcode", ticketData.getBarcode())
                .queryParam("amount", ticketData.getAmount())
                .queryParam("due_date", ticketData.getDueDate().format(API_DATE_FORMATTER))
                .buildAndExpand(ticketId) // Expande o {id_boleto}
                .toUri(); // Constrói o URI, realizando a codificação necessária

        logger.info("Requisitando PDF do boleto na URL: " + uri.toString());

        try {
            // 3. Faz a requisição GET para a API Python
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                logger.severe("Falha ao gerar o boleto na API externa. Status: " + response.statusCode() + " | Body: " + new String(response.body()));
                throw new RuntimeException("Falha ao gerar o boleto na API externa. Status: " + response.statusCode());
            }

            // 4. Retorna o corpo da resposta (o arquivo PDF em bytes)
            return response.body();
        } catch (Exception e) {
            logger.severe("Erro de comunicação com a API de geração de boletos: " + e.getMessage());
            throw new RuntimeException("Erro de comunicação com a API de geração de boletos.", e);
        }
    }
}
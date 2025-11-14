package team07.Banking_System.services.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team07.Banking_System.model.account.Account;
import team07.Banking_System.repository.account.AccountRepository;
import team07.Banking_System.model.transaction.Pix;
import team07.Banking_System.model.transaction.PixDTO;
import team07.Banking_System.repository.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PixService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private static final Logger logger = Logger.getLogger(PixService.class.getName());

    @Autowired
    public PixService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Pix createPixTransaction(PixDTO pixDTO) {
        // 1. Validar dados de entrada
        if (pixDTO.getOriginAccount() == null || pixDTO.getOriginAccount().getId() == null) {
            throw new IllegalArgumentException("Conta de origem é obrigatória.");
        }
        if (pixDTO.getPixKey() == null || pixDTO.getPixKey().isBlank()) {
            throw new IllegalArgumentException("Chave PIX de destino é obrigatória.");
        }
        if (pixDTO.getValue() == null) {
            throw new IllegalArgumentException("O valor do PIX é obrigatório.");
        }

        // 2. Garantir que a conta de origem existe
        Account originAccount = accountRepository.findById(pixDTO.getOriginAccount().getId())
                .orElseThrow(() -> new NoSuchElementException("Conta de origem não encontrada."));

        // 3. Criar a entidade PIX inicial
        Pix pix = new Pix(originAccount, pixDTO.getValue(), pixDTO.getPixKey());
        pix.generateAndSetId(); // Gera o ID da transação

        // 4. Chamar a procedure
        logger.info("Chamando procedure pix_payment...");
        String status = transactionRepository.executePixPayment(
                pix.getOriginAccount().getId(),
                pix.getPixKey(),
                pix.getValue(),
                pix.getId()
        );
        logger.info("Procedure executada. Status: " + status);

        // 5. Tratar o resultado
        return switch (status) {
            case "SUCCESS" -> (Pix) transactionRepository.findById(pix.getId())
                    .orElseThrow(() -> new IllegalStateException("PIX realizado, mas não encontrado para retorno."));
            case "INSUFFICIENT_FUNDS" -> throw new IllegalArgumentException("Saldo insuficiente na conta de origem.");
            case "TARGET_ACCOUNT_NOT_FOUND" -> throw new NoSuchElementException("Chave PIX de destino não encontrada.");
            default -> throw new IllegalStateException("Erro desconhecido na transação PIX: " + status);
        };
    }

    public Optional<Pix> findPixById(String id) {
        return transactionRepository.findById(id)
                .filter(transaction -> transaction instanceof Pix).map(transaction -> (Pix) transaction);
    }
}
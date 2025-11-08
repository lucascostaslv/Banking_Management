package team07.Banking_System.services.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team07.Banking_System.model.account.Account;
import team07.Banking_System.model.account.Current;
import team07.Banking_System.model.account.Savings;
import team07.Banking_System.repository.account.AccountRepository;
import team07.Banking_System.model.transaction.Pix;
import team07.Banking_System.model.transaction.PixDTO;
import team07.Banking_System.repository.transaction.PixRepository;
import team07.Banking_System.services.account.CurrentService;
import team07.Banking_System.services.account.SavingsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PixService {

    private final PixRepository pixRepository;
    private final AccountRepository accountRepository;
    private final CurrentService currentService;
    private final SavingsService savingsService;

    @Autowired
    public PixService(PixRepository pixRepository, AccountRepository accountRepository, CurrentService currentService, SavingsService savingsService) {
        this.pixRepository = pixRepository;
        this.accountRepository = accountRepository;
        this.currentService = currentService;
        this.savingsService = savingsService;
    }

    @Transactional
    public Pix createPixTransaction(PixDTO pixDTO) {
        // 1. Validar e buscar contas
        if (pixDTO.getOriginAccount() == null || pixDTO.getOriginAccount().getId() == null) {
            throw new IllegalArgumentException("Conta de origem é obrigatória.");
        }
        if (pixDTO.getTargetAccount() == null || pixDTO.getTargetAccount().getId() == null) {
            throw new IllegalArgumentException("Conta de destino é obrigatória.");
        }
        if (pixDTO.getValue() == null || pixDTO.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser maior que zero.");
        }

        Account originAccount = accountRepository.findById(pixDTO.getOriginAccount().getId())
                .orElseThrow(() -> new NoSuchElementException("Conta de origem não encontrada."));

        Account targetAccount = accountRepository.findById(pixDTO.getTargetAccount().getId())
                .orElseThrow(() -> new NoSuchElementException("Conta de destino não encontrada."));

        // 2. Validar saldo
        if (originAccount.getBalance().compareTo(pixDTO.getValue()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transação.");
        }

        // 3. Criar objeto Pix
        Pix pix = new Pix();
        pix.generateAndSetId();
        pix.setOriginAccount(originAccount);
        pix.setTargetAccount(targetAccount);
        pix.setValue(pixDTO.getValue());
        pix.setPayment_date(LocalDateTime.now());
        pix.setType("pix");
        
        // 4. Processar pixKey - usar como key_trg (chave de destino)
        if (pixDTO.getPixKey() != null && !pixDTO.getPixKey().isEmpty()) {
            pix.setKey_trg(pixDTO.getPixKey());
            // A chave de origem pode ser o CPF do cliente da conta de origem
            if (originAccount.getClient() != null && originAccount.getClient().getCpf() != null) {
                pix.setKey_org(originAccount.getClient().getCpf());
            }
        }

        // 5. Efetuar a transferência
        BigDecimal originNewBalance = originAccount.getBalance().subtract(pixDTO.getValue());
        originAccount.setBalance(originNewBalance);

        BigDecimal targetNewBalance = targetAccount.getBalance().add(pixDTO.getValue());
        targetAccount.setBalance(targetNewBalance);

        // 6. Salvar tudo
        updateAccount(originAccount);
        updateAccount(targetAccount);
        return pixRepository.save(pix);
    }

    public Optional<Pix> findPixById(String id) {
        return pixRepository.findById(id);
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
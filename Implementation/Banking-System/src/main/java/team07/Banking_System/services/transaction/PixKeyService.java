package team07.Banking_System.services.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team07.Banking_System.model.account.Account;
import team07.Banking_System.model.account.PixKeys;
import team07.Banking_System.repository.account.AccountRepository;
import team07.Banking_System.repository.transaction.PixKeyRepository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class PixKeyService {

    private final PixKeyRepository pixKeyRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public PixKeyService(PixKeyRepository pixKeyRepository, AccountRepository accountRepository) {
        this.pixKeyRepository = pixKeyRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Busca a entidade de chaves para uma conta. A criação será tratada no método transacional.
     */
    private Optional<PixKeys> getKeysForAccount(String accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("O ID da conta é obrigatório.");
        }
        // Apenas busca, não cria.
        return pixKeyRepository.findById(accountId);
    }

    @Transactional
    public PixKeys registerEmailKey(String accountId, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Formato de email inválido.");
        }
        
        if (pixKeyRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este email já está cadastrado como chave PIX em outra conta.");
        }

        // Lógica "Find or Create" dentro da transação
        PixKeys keys = getKeysForAccount(accountId).orElseGet(() -> {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NoSuchElementException("Conta com ID " + accountId + " não encontrada."));
            PixKeys newPixKeys = new PixKeys();
            // Sincroniza a relação bidirecional
            account.setPixKey(newPixKeys);
            return newPixKeys;
        });
        
        if (keys.getEmail() != null) {
            throw new IllegalStateException("Esta conta já possui uma chave de email cadastrada.");
        }

        keys.setEmail(email);
        return pixKeyRepository.save(keys); // Agora isso é um UPDATE seguro
    }

    @Transactional
    public PixKeys registerPhoneKey(String accountId, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 10) { 
            throw new IllegalArgumentException("Formato de telefone inválido.");
        }

        if (pixKeyRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Este telefone já está cadastrado como chave PIX em outra conta.");
        }

        // Lógica "Find or Create" dentro da transação
        PixKeys keys = getKeysForAccount(accountId).orElseGet(() -> {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NoSuchElementException("Conta com ID " + accountId + " não encontrada."));
            PixKeys newPixKeys = new PixKeys();
            // Sincroniza a relação bidirecional
            account.setPixKey(newPixKeys);
            return newPixKeys;
        });

        if (keys.getPhoneNumber() != null) {
            throw new IllegalStateException("Esta conta já possui uma chave de telefone cadastrada.");
        }

        keys.setPhoneNumber(phoneNumber);
        return pixKeyRepository.save(keys); // UPDATE seguro
    }

    @Transactional
    public PixKeys registerRandomKey(String accountId) {
        // Lógica "Find or Create" dentro da transação
        PixKeys keys = getKeysForAccount(accountId).orElseGet(() -> {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NoSuchElementException("Conta com ID " + accountId + " não encontrada."));
            PixKeys newPixKeys = new PixKeys();
            // Sincroniza a relação bidirecional
            account.setPixKey(newPixKeys);
            return newPixKeys;
        });

        if (keys.getRandomKey() != null) {
            throw new IllegalStateException("Esta conta já possui uma chave aleatória cadastrada.");
        }

        String randomKey = UUID.randomUUID().toString();
        
        while (pixKeyRepository.existsByRandomKey(randomKey)) {
             randomKey = UUID.randomUUID().toString();
        }

        keys.setRandomKey(randomKey);
        return pixKeyRepository.save(keys); // UPDATE seguro
    }
}
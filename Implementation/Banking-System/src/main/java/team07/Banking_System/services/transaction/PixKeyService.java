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
import java.util.function.Consumer;
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

    // Método privado e genérico para registrar qualquer tipo de chave.
    // Ele centraliza a lógica de buscar a conta e a entidade PixKeys.
    private PixKeys registerKey(String accountId, Consumer<PixKeys> keySetter) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Conta com ID " + accountId + " não encontrada."));

        // Lógica "Find or Create" centralizada
        PixKeys keys = Optional.ofNullable(account.getPixKey()).orElseGet(() -> {
            PixKeys newKeys = new PixKeys(account);
            account.setPixKey(newKeys); // Garante a associação bidirecional
            return newKeys;
        });

        keySetter.accept(keys); // Aplica a lógica específica de atribuição da chave
        return pixKeyRepository.save(keys);
    }

    @Transactional
    public PixKeys registerEmailKey(String accountId, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Formato de email inválido.");
        }
        
        if (pixKeyRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este email já está cadastrado como chave PIX em outra conta.");
        }

        return registerKey(accountId, keys -> {
            if (keys.getEmail() != null) {
                throw new IllegalStateException("Esta conta já possui uma chave de email cadastrada.");
            }
            keys.setEmail(email);
        });
    }

    @Transactional
    public PixKeys registerPhoneKey(String accountId, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 10) { 
            throw new IllegalArgumentException("Formato de telefone inválido.");
        }

        if (pixKeyRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Este telefone já está cadastrado como chave PIX em outra conta.");
        }

        return registerKey(accountId, keys -> {
            if (keys.getPhoneNumber() != null) {
                throw new IllegalStateException("Esta conta já possui uma chave de telefone cadastrada.");
            }
            keys.setPhoneNumber(phoneNumber);
        });
    }

    @Transactional
    public PixKeys registerRandomKey(String accountId) {
        return registerKey(accountId, keys -> {
            if (keys.getRandomKey() != null) {
                throw new IllegalStateException("Esta conta já possui uma chave aleatória cadastrada.");
            }
            String randomKey;
            do {
                randomKey = UUID.randomUUID().toString();
            } while (pixKeyRepository.existsByRandomKey(randomKey));

            keys.setRandomKey(randomKey);
        });
    }
}
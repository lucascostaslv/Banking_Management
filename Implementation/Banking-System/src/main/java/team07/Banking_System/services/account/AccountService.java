package team07.Banking_System.services.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import team07.Banking_System.model.account.Account;
import team07.Banking_System.model.user.Client;
import team07.Banking_System.repository.user.ClientRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class AccountService<T extends Account, R extends JpaRepository<T, String>> {

    protected final R accountRepository;
    protected final ClientRepository clientRepository;

    protected AccountService(R accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    public Optional<T> findAccount(String id) {
        return accountRepository.findById(id);
    }

    public List<T> listAll() {
        return accountRepository.findAll();
    }

    @Transactional
    public T createAccount(T account) {
        if (account.getClient() == null || account.getClient().getId() == null) {
            throw new IllegalArgumentException("O ID do cliente é obrigatório para criar uma conta.");
        }
        Client client = clientRepository.findById(account.getClient().getId())
                .orElseThrow(() -> new NoSuchElementException("Cliente com ID " + account.getClient().getId() + " não encontrado."));
        account.setClient(client);
        account.generateAndSetId();
        account.generateAndSetAccountNumber();
        return accountRepository.save(account);
    }

    @Transactional
    public T updateAccount(T account) {
        return accountRepository.save(account);
    }
}
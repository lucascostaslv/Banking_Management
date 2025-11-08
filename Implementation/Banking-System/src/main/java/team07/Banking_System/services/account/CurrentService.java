package team07.Banking_System.services.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team07.Banking_System.model.account.Current;
import team07.Banking_System.model.user.Client;
import team07.Banking_System.repository.account.CurrentRepository;
import team07.Banking_System.repository.user.ClientRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CurrentService {

    private final CurrentRepository currentRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public CurrentService(CurrentRepository currentRepository, ClientRepository clientRepository) {
        this.currentRepository = currentRepository;
        this.clientRepository = clientRepository;
    }

    public Optional<Current> findAccount(String id) {
        return currentRepository.findById(id);
    }

    public List<Current> listAll() {
        return currentRepository.findAll();
    }

    @Transactional
    public Current createCurrent(Current account) {
        if (account.getClient() == null || account.getClient().getId() == null) {
            throw new IllegalArgumentException("O ID do cliente é obrigatório para criar uma conta.");
        }

        // Busca o cliente completo para garantir que todos os dados estão disponíveis
        Client client = clientRepository.findById(account.getClient().getId())
                .orElseThrow(() -> new NoSuchElementException("Cliente com ID " + account.getClient().getId() + " não encontrado."));

        // Associa o cliente completo e gera o ID e número da conta
        account.setClient(client);
        account.generateAndSetId();
        account.generateAndSetAccountNumber();

        return currentRepository.save(account);
    }

    @Transactional
    public Current updateCurrent(Current account) {
        // A lógica de atualização pode ser mais robusta, mas por enquanto salva a entidade.
        return currentRepository.save(account);
    }
}
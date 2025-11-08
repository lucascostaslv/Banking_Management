package team07.Banking_System.services.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team07.Banking_System.model.account.Savings;
import team07.Banking_System.model.user.Client;
import team07.Banking_System.repository.account.SavingsRepository;
import team07.Banking_System.repository.user.ClientRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SavingsService {
    private final SavingsRepository savingsRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public SavingsService(SavingsRepository savingsRepository, ClientRepository clientRepository) {
        this.savingsRepository = savingsRepository;
        this.clientRepository = clientRepository;
    }

    public Optional<Savings> findAccount(String id) {
        return savingsRepository.findById(id);
    }

    public List<Savings> listAll() {
        return savingsRepository.findAll();
    }

    @Transactional
    public Savings createSavings(Savings savings) {
        if (savings.getClient() == null || savings.getClient().getId() == null) {
            throw new IllegalArgumentException("O ID do cliente é obrigatório para criar uma conta.");
        }

        // Busca o cliente completo para garantir que todos os dados estão disponíveis
        Client client = clientRepository.findById(savings.getClient().getId())
                .orElseThrow(() -> new NoSuchElementException("Cliente com ID " + savings.getClient().getId() + " não encontrado."));

        // Associa o cliente completo e gera o ID e número da conta
        savings.setClient(client);
        savings.generateAndSetId();
        savings.generateAndSetAccountNumber();

        return savingsRepository.save(savings);
    }

    public Savings updateSavings(Savings savings) {
        return savingsRepository.save(savings);
    }
}

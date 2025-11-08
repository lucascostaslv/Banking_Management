package team07.Banking_System.services.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team07.Banking_System.model.account.Savings;
import team07.Banking_System.repository.account.SavingsRepository;
import team07.Banking_System.repository.user.ClientRepository;

@Service
public class SavingsService extends AccountService<Savings, SavingsRepository> {

    @Autowired
    public SavingsService(SavingsRepository savingsRepository, ClientRepository clientRepository) {
        super(savingsRepository, clientRepository);
    }

    public Savings createSavings(Savings savings) {
        return super.createAccount(savings);
    }

    public Savings updateSavings(Savings savings) {
        return super.updateAccount(savings);
    }
}

package team07.Banking_System.services.account;

import team07.Banking_System.repository.account.SavingsRepository;
import team07.Banking_System.model.account.Savings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsService {
    private final SavingsRepository savingsRepository;

    @Autowired
    public SavingsService(SavingsRepository savingsRepository){
        this.savingsRepository = savingsRepository;
    }
}

package team07.Banking_System.services.account;

import team07.Banking_System.repository.account.SavingsRepository;
import team07.Banking_System.model.account.Savings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class SavingsService {
    private final SavingsRepository savingsRepository;

    @Autowired
    public SavingsService(SavingsRepository savingsRepository){
        this.savingsRepository = savingsRepository;
    }

    public Optional<Savings> findAccount(String id){
        return savingsRepository.findById(id);
    }


    public void uniqueIdGen(Savings savings){
        String generatedId;

        do{
            generatedId = savings.getId();

        } while(savingsRepository.existsById(generatedId));

        savings.setId(generatedId);
    }

    @Transactional
    public Savings createSavings(Savings savings){
        uniqueIdGen(savings);

        return savingsRepository.save(savings);
    }

    @Transactional
    public Savings updateCurrent(Savings savings){
        return savingsRepository.save(savings);
    }
}

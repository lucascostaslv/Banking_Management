package team07.Banking_System.services.account;

import team07.Banking_System.repository.account.CurrentRepository;
import team07.Banking_System.model.account.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CurrentService {
    private final CurrentRepository currentRepository;

    @Autowired
    public CurrentService(CurrentRepository currentRepository){
        this.currentRepository = currentRepository;
    }

    public Optional<Current> findAccount(String id){
        return currentRepository.findById(id);
    }


    public void uniqueIdGen(Current current){
        String generatedId;

        do{
            generatedId = current.getId();

        } while(currentRepository.existsById(generatedId));

        current.setId(generatedId);
    }

    @Transactional
    public Current createCurrent(Current current){
        uniqueIdGen(current);

        return currentRepository.save(current);
    }

    @Transactional
    public Current updateCurrent(Current current){
        return currentRepository.save(current);
    }
}

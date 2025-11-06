package team07.Banking_System.services.account;

import team07.Banking_System.repository.account.CurrentRepository;
import team07.Banking_System.model.account.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CurrentService {
    private final CurrentRepository currentRepository;

    @Autowired
    public CurrentService(CurrentRepository currentRepository){
        this.currentRepository = currentRepository;
    }
}

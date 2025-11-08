package team07.Banking_System.services.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team07.Banking_System.model.account.Current;
import team07.Banking_System.repository.account.CurrentRepository;
import team07.Banking_System.repository.user.ClientRepository;

@Service
public class CurrentService extends AccountService<Current, CurrentRepository> {

    @Autowired
    public CurrentService(CurrentRepository currentRepository, ClientRepository clientRepository) {
        super(currentRepository, clientRepository);
    }

    public Current createCurrent(Current account) {
        return super.createAccount(account);
    }

    public Current updateCurrent(Current account) {
        return super.updateAccount(account);
    }
}
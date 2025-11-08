package team07.Banking_System.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.account.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {}
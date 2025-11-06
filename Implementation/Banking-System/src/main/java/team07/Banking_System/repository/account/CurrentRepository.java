package team07.Banking_System.repository.account;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import team07.Banking_System.model.account.Current;

@Repository
public interface CurrentRepository extends JpaRepository <Current, String>{}

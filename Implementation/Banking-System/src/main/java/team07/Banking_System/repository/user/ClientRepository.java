package team07.Banking_System.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.user.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String>{}

package team07.Banking_System.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.user.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, String>{}
package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.transaction.Pix;
import java.util.List;


@Repository
public interface PixRepository extends JpaRepository<Pix, String>{

    List<Pix> findByAcc_orgId(String accountId);
}

package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import team07.Banking_System.model.transaction.Pix;
import java.util.List;

public interface PixRepository extends JpaRepository<Pix, String> {
    List<Pix> findByOriginAccountId(String accountId);
}
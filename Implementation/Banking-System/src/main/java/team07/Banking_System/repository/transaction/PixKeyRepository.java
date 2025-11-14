package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.account.PixKeys;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKeys, String> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByRandomKey(String randomKey); // Corrigido de existsByRandKey
}
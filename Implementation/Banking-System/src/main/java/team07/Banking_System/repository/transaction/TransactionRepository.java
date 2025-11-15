package team07.Banking_System.repository.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team07.Banking_System.model.transaction.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>, TransactionRepositoryCustom {
    // Métodos JPA padrão (findById, save, etc.) permanecem aqui.
    // As chamadas de procedure agora são gerenciadas pela implementação customizada.
}
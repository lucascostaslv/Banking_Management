package team07.Banking_System.repository.transaction;

import java.math.BigDecimal;

public interface TransactionRepositoryCustom {
    String executePixPayment(String originAccountId, String pixKey, BigDecimal value, String transactionId);
    String executeTicketPayment(String payingAccountId, String ticketId);
}
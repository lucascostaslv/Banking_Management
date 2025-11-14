package team07.Banking_System.model.transaction;

import jakarta.persistence.*;
import team07.Banking_System.model.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "tb_transaction")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transaction {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id")
    private Account originAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    private BigDecimal value;

    @Column(name = "transaction_date", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime transactionDate;

    private String type;

    public Transaction() {
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(Account originAccount, BigDecimal value, String type) {
        this.originAccount = originAccount;
        this.value = value;
        this.type = type;
        this.transactionDate = LocalDateTime.now();
    }

    public void generateAndSetId() {
        Random rand = new Random();
        int r_aux = rand.nextInt(1000000);
        String r_num = String.format("%06d", r_aux);
        this.id = "TR-" + r_num;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Account originAccount) {
        this.originAccount = originAccount;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

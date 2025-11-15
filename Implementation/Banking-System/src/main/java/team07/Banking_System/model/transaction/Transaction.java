package team07.Banking_System.model.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import team07.Banking_System.model.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class Transaction {

    @Id
    @Column(name = "id", length = 17)
    private String id;

    @Column(name = "type", length = 63, nullable = false)
    private String type;

    @Column(name = "payment_date", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;

    @Column(name = "transaction_value", precision = 13, scale = 2, nullable = false)
    private BigDecimal transactionValue;

    @Column(name = "status", length = 20, nullable = true)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Account originAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Account targetAccount;

    // Construtores
    public Transaction() {
        // payment_date começa NULL para boletos não pagos
        this.status = "PENDING";
    }

    public Transaction(Account originAccount, BigDecimal transactionValue, String type) {
        this.originAccount = originAccount;
        this.transactionValue = transactionValue;
        this.type = type;
        this.status = "PENDING";
        // payment_date fica NULL até ser pago
    }

    public Transaction(Account originAccount, Account targetAccount, BigDecimal transactionValue, String type) {
        this.originAccount = originAccount;
        this.targetAccount = targetAccount;
        this.transactionValue = transactionValue;
        this.type = type;
        this.status = "PENDING";
        this.paymentDate = LocalDateTime.now(); // PIX é pago na hora
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(BigDecimal transactionValue) {
        this.transactionValue = transactionValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", transactionValue=" + transactionValue +
                ", status='" + status + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
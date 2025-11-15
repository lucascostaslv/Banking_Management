package team07.Banking_System.model.transaction;

import jakarta.persistence.*;
import team07.Banking_System.model.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tb_transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class Transaction {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id")
    private Account originAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    // CORREÇÃO: Mapeado para a coluna correta 'transaction_value'
    @Column(name = "transaction_value")
    private BigDecimal value;

    // CORREÇÃO FINAL: Mapeado para a coluna correta 'payment_date'
    @Column(name = "payment_date", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime paymentDate;

    private String type;

    private String status;

    public Transaction() {
        // A data é definida pela procedure ou no momento da criação
    }

    public Transaction(Account originAccount, BigDecimal value, String type) {
        this.originAccount = originAccount;
        this.value = value;
        this.type = type;
        this.status = "PENDING"; // Transações começam como pendentes
        // A data do pagamento (paymentDate) será nula até a transação ser efetivada
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

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package team07.Banking_System.model.transaction;

import jakarta.persistence.*;
import team07.Banking_System.model.account.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_transaction")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transaction {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "client", "keys"})
    private Account originAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "client", "keys"})
    private Account targetAccount;
    private String type;
    private LocalDateTime payment_date;

    @Column(name = "transaction_value")
    private BigDecimal value;

    public Transaction(Account acc_trg, BigDecimal value){
        this.id = generateId();
        this.targetAccount = acc_trg;
        this.value = value;
    }

    public Transaction(){}

    protected String generateId(){
    LocalDateTime now = LocalDateTime.now();

    String pattern = "ddyyyyMMHHmmssSSS"; 

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    
    return now.format(formatter);
}

    public void generateAndSetId() {
        this.id = generateId();
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(LocalDateTime payment_date) {
        this.payment_date = payment_date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}

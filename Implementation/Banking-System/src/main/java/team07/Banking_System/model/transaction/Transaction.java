package team07.Banking_System.model.transaction;

import jakarta.persistence.*;
import team07.Banking_System.model.account.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

@Entity
public abstract class Transaction {
    @Id
    private String id;

    private Account acc_org;
    private Account acc_trg;
    private String type;
    private LocalDateTime payment_date;
    private BigDecimal value;

    public Transaction(Account acc_trg, BigDecimal value){
        this.id = generateId();
        this.acc_trg = acc_trg;
        this.value = value;
    }

    public Transaction(){}

    protected String generateId(){
    LocalDateTime now = LocalDateTime.now();

    String pattern = "ddyyyyMMHHmmssSSS"; 

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    
    return now.format(formatter);
}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getAcc_org() {
        return acc_org;
    }

    public void setAcc_org(Account acc_org) {
        this.acc_org = acc_org;
    }

    public Account getAcc_trg() {
        return acc_trg;
    }

    public void setAcc_trg(Account acc_trg) {
        this.acc_trg = acc_trg;
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

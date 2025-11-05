package team07.Banking_System.model.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import team07.Banking_System.model.user.Client;;


public abstract class Account {
    @Id
    private String id;

    private Client c;
    private int n_acc;
    private BigDecimal balance;
    private String type;
    private String pix_key;
    
    @Column(columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime open_date;

    public Account(Client c, String type){
        this.id = GenerateId();
        this.n_acc = GenerateNAcc();
        this.c = c;
        this.type = type;
    }

    public Account(){}

    protected abstract String GenerateId();
    protected abstract int GenerateNAcc();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getC() {
        return c;
    }

    public void setC(Client c) {
        this.c = c;
    }

    public int getN_acc() {
        return n_acc;
    }

    public void setN_acc(int n_acc) {
        this.n_acc = n_acc;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getOpen_date() {
        return open_date;
    }

    public void setOpen_date(LocalDateTime open_date) {
        this.open_date = open_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPix_key() {
        return pix_key;
    }

    public void setPix_key(String pix_key) {
        this.pix_key = pix_key;
    };
}

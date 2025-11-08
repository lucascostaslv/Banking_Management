package team07.Banking_System.model.account;

import team07.Banking_System.model.user.Client;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
public class Savings extends Account{
    private LocalDateTime return_date;

    @Column(name = "return_amount")
    private BigDecimal return_amount;

    public Savings(Client c, String type){
        super(c, type);
    }

    public Savings(){};

    public LocalDateTime getReturn_date() {
        return return_date;
    }

    public void setReturn_date(LocalDateTime return_date) {
        this.return_date = return_date;
    }

    public BigDecimal getReturn_amount() {
        return return_amount;
    }

    public void setReturn_amount(BigDecimal return_amount) {
        this.return_amount = return_amount;
    }
}  

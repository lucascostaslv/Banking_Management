package team07.Banking_System.model.account;

import team07.Banking_System.model.user.Client;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
public class Savings extends Account{
    private LocalDateTime return_date;

    @Column(columnDefinition = "TIMESTAMP(3)")
    private BigDecimal return_value;

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

    public BigDecimal getReturn_value() {
        return return_value;
    }

    public void setReturn_value(BigDecimal return_value) {
        this.return_value = return_value;
    }
}  

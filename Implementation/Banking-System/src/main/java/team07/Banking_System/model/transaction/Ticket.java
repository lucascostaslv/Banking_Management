package team07.Banking_System.model.transaction;

import jakarta.persistence.*;
import java.time.LocalDate;
import team07.Banking_System.model.account.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_ticket")
@PrimaryKeyJoinColumn(name = "id")
public class Ticket extends Transaction{
    private String bars_code;
    private LocalDate due_date;
    
    public Ticket(Account acc_trg, BigDecimal value, LocalDate due_date){
        super(acc_trg, value, "ticket");
        this.due_date = due_date;
    }

    public Ticket(){}

    public String getBars_code() {
        return bars_code;
    }

    public void setBars_code(String bars_code) {
        this.bars_code = bars_code;
    }

    public LocalDate getDue_date() {
        return due_date;
    }

    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    };
}

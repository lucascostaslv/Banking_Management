package team07.Banking_System.model.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import team07.Banking_System.model.account.Account;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_ticket")
@PrimaryKeyJoinColumn(name = "id")
public class Ticket extends Transaction {

    @Column(name = "bars_code", length = 255, nullable = false)
    private String barsCode;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    public Ticket() {
        super();
        super.setType("TICKET");
        super.setStatus("PENDING");
    }

    public Ticket(Account originAccount, BigDecimal transactionValue, String barsCode, LocalDate dueDate) {
        super(originAccount, transactionValue, "TICKET");
        this.barsCode = barsCode;
        this.dueDate = dueDate;
        super.setStatus("PENDING");
        // payment_date fica NULL até o boleto ser pago
    }

    // Getters e Setters
    public String getBarsCode() {
        return barsCode;
    }

    public void setBarsCode(String barsCode) {
        this.barsCode = barsCode;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + getId() + '\'' +
                ", barsCode='" + barsCode + '\'' +
                ", dueDate=" + dueDate +
                ", value=" + getTransactionValue() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
package team07.Banking_System.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TicketDTO {
    private AccountReference originAccount;
    private BigDecimal value;
    private LocalDate dueDate;

    public TicketDTO() {}

    public AccountReference getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(AccountReference originAccount) {
        this.originAccount = originAccount;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public static class AccountReference {
        private String id;

        public AccountReference() {}

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}


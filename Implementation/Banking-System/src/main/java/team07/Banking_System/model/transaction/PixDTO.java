package team07.Banking_System.model.transaction;

import java.math.BigDecimal;

public class PixDTO {
    private AccountReference originAccount;
    private AccountReference targetAccount;
    private BigDecimal value;
    private String pixKey;

    public PixDTO() {}

    public AccountReference getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(AccountReference originAccount) {
        this.originAccount = originAccount;
    }

    public AccountReference getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(AccountReference targetAccount) {
        this.targetAccount = targetAccount;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
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


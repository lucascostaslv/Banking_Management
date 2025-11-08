package team07.Banking_System.model.account;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PixKeyId implements Serializable {

    private String accountId;
    private PixKeys.KeyType type;

    public PixKeyId() {}

    public PixKeyId(String accountId, PixKeys.KeyType type) {
        this.accountId = accountId;
        this.type = type;
    }

    // Getters, Setters, equals, and hashCode

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public PixKeys.KeyType getType() {
        return type;
    }

    public void setType(PixKeys.KeyType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixKeyId pixKeyId = (PixKeyId) o;
        return Objects.equals(accountId, pixKeyId.accountId) && type == pixKeyId.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, type);
    }
}
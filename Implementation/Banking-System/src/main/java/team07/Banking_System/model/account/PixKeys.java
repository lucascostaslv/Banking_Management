package team07.Banking_System.model.account;

import jakarta.persistence.*;

@Entity
@IdClass(PixKeyId.class)
public class PixKeys{
    @Id
    @Column(name = "account_id")
    private String accountId;

    @Id
    @Enumerated(EnumType.STRING)
    private KeyType type;

    private String email;
    private String phone_number;
    private String rand_key;

    public enum KeyType {
        EMAIL, PHONE, RANDOM
    }

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private Account account;

    public PixKeys(Account account, String key, KeyType type) {
        this.account = account;
        this.accountId = account.getId();
        this.type = type;

        switch (type) {
            case EMAIL -> this.email = key;
            case PHONE -> this.phone_number = key;
            case RANDOM -> this.rand_key = key;
        }
    }

    public PixKeys() {}

    // Getters and Setters

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getRand_key() {
        return rand_key;
    }

    public KeyType getType() {
        return type;
    }

    public void setType(KeyType type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
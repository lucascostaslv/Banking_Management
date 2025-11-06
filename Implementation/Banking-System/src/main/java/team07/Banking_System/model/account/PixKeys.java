package team07.Banking_System.model.account;

import jakarta.persistence.*;

@Entity
public class PixKeys{
    @Id
    private String id;

    private String email;
    private String phone_number;
    private String rand_key;
    private KeyType type;

    public enum KeyType {
        EMAIL, PHONE, RANDOM
    }

    @ManyToOne
    @JoinColumn(name = "id")
    private Account account;

    public PixKeys(Account account, String key, KeyType type) {
        this.id = account.getId();
        this.type = type;

        switch (type) {
            case EMAIL -> this.email = key;
            case PHONE -> this.phone_number = key;
            case RANDOM -> this.rand_key = key;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getRand_key() {
        return rand_key;
    }

    public void setRand_key(String rand_key) {
        this.rand_key = rand_key;
    }

    public KeyType getType() {
        return type;
    }

    public void setType(KeyType type) {
        this.type = type;
    }

    
}
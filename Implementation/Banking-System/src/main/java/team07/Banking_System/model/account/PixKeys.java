package team07.Banking_System.model.account;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_pixKey")
public class PixKeys {

    @Id
    @Column(name = "id")
    private String id; // mesmo ID da conta

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private String randKey;

    @OneToOne
    @MapsId // usa o mesmo ID da conta
    @JoinColumn(name = "id")
    private Account account;

    public PixKeys() {}

    public PixKeys(Account account) {
        this.account = account;
        this.id = account.getId();
    }

    // Getters e Setters
    public String getId() { return id; }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getRandomKey() { return randKey; }

    public Account getAccount() { return account; }

    public void setAccount(Account account) { this.account = account; }
}

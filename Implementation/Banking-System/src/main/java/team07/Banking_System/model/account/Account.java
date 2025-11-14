package team07.Banking_System.model.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonBackReference;
import team07.Banking_System.model.user.Client;

@Entity
@Table(name = "tb_account")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference
    private Client client;
    private int accountNumber;
    private BigDecimal balance = BigDecimal.ZERO;
    private String type;
    
    @Column(columnDefinition = "TIMESTAMP(3)", updatable = false)
    private LocalDateTime openDate;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL) 

    private PixKeys pixKey;

    public Account(Client c, String type){
        if (c == null) {
            throw new IllegalArgumentException("Client cannot be null when creating an account.");
        }
        this.client = c;
        this.type = type;
        this.openDate = LocalDateTime.now();
        generateAndSetId();
        generateAndSetAccountNumber();
    }

    public Account(){}

    protected String generateId(Client client){
        int year = LocalDate.now().getYear();
        year = year%100;

        String code = client.getState().getCode();

        Random rand = new Random();
        int r_aux = rand.nextInt(10000);
        String r_num = String.format("%04d", r_aux);

        return "ACC-" + code + year + r_num;
    }

    public void generateAndSetId() {
        this.id = generateId(this.client);
    }

    private int generateNAcc(Client client){
        String code = client.getState().getCode();

        Random rand = new Random();
        String r_num = String.format("%04d", rand.nextInt(10000));

        String s_aux = r_num + code;

        return Integer.parseInt(s_aux);
    }

    public void generateAndSetAccountNumber() {
        this.accountNumber = generateNAcc(this.client);
    }

    // Getters and Setters with conventional Java naming
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public PixKeys getPixKey() {
        return pixKey;
    }

    public void setPixKey(PixKeys pixKey) {
        this.pixKey = pixKey;
        pixKey.setAccount(this); // Sincroniza o outro lado da relação
    }

    public enum States{
        AC("01"),
        AL("02"),
        AP("03"),
        AM("04"),
        BA("05"),
        CE("06"),
        DF("07"),
        ES("08"),
        GO("09"),
        MA("10"),
        MT("11"),
        MS("12"),
        MG("13"),
        PA("14"),
        PB("15"),
        PR("16"),
        PE("17"),
        PI("18"),
        RJ("19"),
        RN("20"),
        RS("21"),
        RO("22"),
        RR("23"),
        SC("24"),
        SP("25"),
        SE("26"),
        TO("27");

        private final String code;

        States (String code){
            this.code = code;
        }

        public String getCode(){
            return code;
        }
    }
}

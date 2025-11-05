package team07.Banking_System.model.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import team07.Banking_System.model.user.Client;;


public abstract class Account {
    @Id
    private String id;

    private Client c;
    private int n_acc;
    private BigDecimal balance;
    private String type;
    
    @Column(columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime open_date;

    public Account(Client c, String type){
        this.id = GenerateId();
        this.n_acc = GenerateNAcc();
        this.c = c;
        this.type = type;
    }

    public Account(){}

    protected String GenerateId(){
        int year = LocalDate.now().getYear();
        year = year%100;

        Client aux = this.getC();
        String code = aux.getState().getCode();

        Random rand = new Random();
        int r_aux = rand.nextInt(10000);
        String r_num = String.format("%04d", r_aux);

        return "C-" + code + year + r_num;
    }

    protected int GenerateNAcc(){
        Client aux = this.getC();
        String code = aux.getState().getCode();

        Random rand = new Random();
        int r_aux = rand.nextInt(10000);
        String r_num = String.format("%04d", r_aux);

        String s_aux = r_num + code;

        return Integer.parseInt(s_aux);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getC() {
        return c;
    }

    public void setC(Client c) {
        this.c = c;
    }

    public int getN_acc() {
        return n_acc;
    }

    public void setN_acc(int n_acc) {
        this.n_acc = n_acc;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getOpen_date() {
        return open_date;
    }

    public void setOpen_date(LocalDateTime open_date) {
        this.open_date = open_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

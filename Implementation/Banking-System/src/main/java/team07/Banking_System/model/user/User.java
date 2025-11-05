package team07.Banking_System.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public abstract class User{
    @Id
    private String id;

    private String first_name;
    private String last_name;
    private String cpf;
    private  LocalDate birth_day;
    private int type;

    public User(String first_name, String last_name, String cpf, LocalDate birth_day, int type){
        this.id = GenerateId();
        this.first_name = first_name;
        this.last_name = last_name;
        this.cpf = cpf;
        this.birth_day = birth_day;
        this.type = type;
    }

    public User(){}

    protected abstract String GenerateId();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(LocalDate birth_day) {
        this.birth_day = birth_day;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }    
}

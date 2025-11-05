package team07.Banking_System.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public abstract class User{
    @Id
    private String id;

    private String name;
    private String cpf;
    private  LocalDate birth_day;
    private int type;

    public User(String name, String cpf, LocalDate birth_day, int type){
        this.id = GenerateId();
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

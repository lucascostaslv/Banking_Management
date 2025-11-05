package team07.Banking_System.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Manager extends User{
    private String role;

    public Manager(String name, String cpf, LocalDate birth_day, int type, String role){
        super(name, cpf, birth_day, 1);
        this.role = role;
    }  

    public Manager(){}

    protected String GenerateId(){

    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}

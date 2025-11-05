package team07.Banking_System.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Client extends User{
    private int act;

    public Client(String name, String cpf, LocalDate birth_day, int type, int act){
        super(name, cpf, birth_day, 2);
        this.act = act;
    }

    public Client(){}

    @Override
    protected String GenerateId(){

    }

    public void setAct(int act){
        this.act = act;
    }

    public int getAct() {
        return act;
    }
}

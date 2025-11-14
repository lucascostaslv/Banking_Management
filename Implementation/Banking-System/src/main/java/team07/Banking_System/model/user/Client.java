package team07.Banking_System.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;
import team07.Banking_System.model.account.Account.States; // Importe o enum
import java.util.Random;

@Entity
@Table(name = "tb_client")
@PrimaryKeyJoinColumn(name = "id")
public class Client extends User{
    private int act;

    // --- CORREÇÃO ---
    @Enumerated(EnumType.STRING) // Salva o NOME (ex: "SP", "RJ")
    @Column(name = "state")
    private States state; 
    // --- FIM DA CORREÇÃO ---

    public Client(String first_name, String last_name, String cpf, LocalDate birth_day, int type, int act){
        super(first_name, last_name, cpf, birth_day, 2);
        this.act = act;
    }

    public Client(){}

    @Override
    public String generateId(){
        String month = String.format("%02d" ,LocalDate.now().getMonthValue());

        Random rand = new Random();
        int r_int = rand.nextInt(1000);
        String r_num = String.format("%03d", r_int);

        return "CLI-" + month + r_num;
    }

    public void setAct(int act){
        this.act = act;
    }

    public int getAct() {
        return act;
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }
}
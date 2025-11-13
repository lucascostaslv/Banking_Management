package team07.Banking_System.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Random;

@Entity
@Table(name = "tb_manager")
@PrimaryKeyJoinColumn(name = "id")
public class Manager extends User{
    private String role;

    public Manager(String first_name, String last_name, String cpf, LocalDate birth_day, int type, String role){
        super(first_name, last_name, cpf, birth_day, 1);
        this.role = role;
    }  

    public Manager(){}

    @Override
    public String generateId() {
        String name = getFirst_name();
        String l_name = getLast_name();

        Random rand = new Random();
        int r_int = rand.nextInt(1000);
        String r_num = String.format("%03d", r_int);

        return "MID-" + name.charAt(0) + l_name.charAt(0) + r_num;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}

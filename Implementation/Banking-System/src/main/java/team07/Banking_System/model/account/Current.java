package team07.Banking_System.model.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import team07.Banking_System.model.user.Client;

@Entity
@Table(name = "tb_current")
@PrimaryKeyJoinColumn(name = "id")
public class Current extends Account{
    
    @Column(name = "monthly_tax")
    private BigDecimal monthly_tax;

    public Current(Client c, String type){
        super(c, type);
    }

    // --- CORREÇÃO ---
    public Current(){
        super.setType("current");
        // Define o valor padrão que está no seu SQL para evitar erro NOT NULL
        this.monthly_tax = new BigDecimal("0.52"); 
    }
    // --- FIM DA CORREÇÃO ---

    public BigDecimal getMonthly_tax() {
        return monthly_tax;
    }

    public void setMonthly_tax(BigDecimal monthly_tax) {
        this.monthly_tax = monthly_tax;
    }
}
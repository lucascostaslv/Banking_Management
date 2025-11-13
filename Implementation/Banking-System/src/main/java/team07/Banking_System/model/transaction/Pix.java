package team07.Banking_System.model.transaction;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_pix")
@PrimaryKeyJoinColumn(name = "id")
public class Pix extends Transaction{
    private String key_org;
    private String key_trg;

    public Pix(String key_org, String key_trg, BigDecimal value){
        this.setValue(value);
        this.key_org = key_org;
        this.key_trg = key_trg;
    }

    public Pix(){}

    public String getKey_org() {
        return key_org;
    }

    public void setKey_org(String key_org) {
        this.key_org = key_org;
    }

    public String getKey_trg() {
        return key_trg;
    }

    public void setKey_trg(String key_trg) {
        this.key_trg = key_trg;
    }
}

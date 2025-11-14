package team07.Banking_System.model.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import team07.Banking_System.model.account.Account;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_pix")
@PrimaryKeyJoinColumn(name = "id")
public class Pix extends Transaction {

    @Column(name = "pix_key")
    private String pixKey;

    public Pix() {
        super.setType("pix");
    }

    public Pix(Account originAccount, BigDecimal value, String pixKey) {
        super(originAccount, value, "pix");
        this.pixKey = pixKey;
    }

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }
}

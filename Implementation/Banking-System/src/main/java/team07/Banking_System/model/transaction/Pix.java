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

    @Column(name = "key_org")
    private String keyOrg;
    
    @Column(name = "key_trg")
    private String keyTrg;  

    public Pix() {
        super.setType("PIX");
    }

    public Pix(Account originAccount, BigDecimal value, String type,String keyOrg, String keyTrg) {
        super(originAccount, value, type);
        this.keyOrg = keyOrg;
        this.keyTrg = keyTrg;
    }

    // Construtor alternativo mantendo compatibilidade (usa pixKey como keyTrg)
    public Pix(Account originAccount, BigDecimal value, String pixKey) {
        super(originAccount, value, "PIX");
        this.keyTrg = pixKey;
        // keyOrg pode ser preenchido depois via setter ou pela procedure
    }

    public String getKeyOrg() {
        return keyOrg;
    }

    public void setKeyOrg(String keyOrg) {
        this.keyOrg = keyOrg;
    }

    public String getKeyTrg() {
        return keyTrg;
    }

    public void setKeyTrg(String keyTrg) {
        this.keyTrg = keyTrg;
    }

    // Métodos de compatibilidade com código antigo (se necessário)
    @Deprecated
    public String getPixKey() {
        return keyTrg; // Assume que pixKey era a chave de destino
    }

    @Deprecated
    public void setPixKey(String pixKey) {
        this.keyTrg = pixKey;
    }
}
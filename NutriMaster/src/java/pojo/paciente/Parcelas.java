/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo.paciente;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author renisteffenon
 */

@Entity
public class Parcelas {
    
    private Long id;
    private float valorAberto;
    private int bonusGold;
    private double taxaJuros;
    private Date vencimento;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getValorAberto() {
        return valorAberto;
    }

    public void setValorAberto(float valorAberto) {
        this.valorAberto = valorAberto;
    }

    public int getBonusGold() {
        return bonusGold;
    }

    public void setBonusGold(int bonusGold) {
        this.bonusGold = bonusGold;
    }

    public double getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(double taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }
    
    
}

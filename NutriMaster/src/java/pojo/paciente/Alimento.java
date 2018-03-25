package pojo.paciente;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author gustavolazarottoschroeder
 */

@Entity
public class Alimento implements Serializable {

    private Long id;
    private String alimento;
    private Double qtdeCalorias;
    private Double vlrEnergetico;
    private Double gordurasTotais;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlimento() {
        return alimento;
    }

    public void setAlimento(String alimento) {
        this.alimento = alimento;
    }

    public Double getQtdeCalorias() {
        return qtdeCalorias;
    }

    public void setQtdeCalorias(Double qtdeCalorias) {
        this.qtdeCalorias = qtdeCalorias;
    }

    public Double getVlrEnergetico() {
        return vlrEnergetico;
    }

    public void setVlrEnergetico(Double vlrEnergetico) {
        this.vlrEnergetico = vlrEnergetico;
    }

    public Double getGordurasTotais() {
        return gordurasTotais;
    }

    public void setGordurasTotais(Double gordurasTotais) {
        this.gordurasTotais = gordurasTotais;
    }
}
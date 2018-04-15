package pojo.paciente;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 *
 * @author augustopasini
 */
@Entity
public class Exercicio implements Serializable {

    private String exercicio;
    private Double calorias;
    
    @Id
    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public Double getCalorias() {
        return calorias;
    }

    public void setCalorias(Double calorias) {
        this.calorias = calorias;
    }
    
    
}

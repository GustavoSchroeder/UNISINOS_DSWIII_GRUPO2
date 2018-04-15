package pojo.paciente;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pojo.usuario.Usuario;

/**
 *
 * @author augustopasini
 */

@Entity
public class RotinaExercicio implements Serializable{
    
    private Long id;
    private String exercicio;
    private Double duracaoExercicio;
    private Usuario nutricionistaResponsavel;
    private List<CalendarioExercicio> calendarioExercicio;
    private Integer qtdeSemanal;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    @OneToOne
    public Usuario getNutricionistaResponsavel() {
        return nutricionistaResponsavel;
    }

    public void setNutricionistaResponsavel(Usuario nutricionistaResponsavel) {
        this.nutricionistaResponsavel = nutricionistaResponsavel;
    }

        @OneToMany (fetch = FetchType.EAGER)
            @Fetch(FetchMode.SUBSELECT) 

    public List<CalendarioExercicio> getCalendarioExercicio() {
        return calendarioExercicio;
    }

    public void setCalendarioExercicio(List<CalendarioExercicio> calendarioExercicio) {
        this.calendarioExercicio = calendarioExercicio;
    }

    public Integer getQtdeSemanal() {
        return qtdeSemanal;
    }

    public void setQtdeSemanal(Integer qtdeSemanal) {
        this.qtdeSemanal = qtdeSemanal;
    }

    public Double getDuracaoExercicio() {
        return duracaoExercicio;
    }

    public void setDuracaoExercicio(Double duracaoExercicio) {
        this.duracaoExercicio = duracaoExercicio;
    }
    
}

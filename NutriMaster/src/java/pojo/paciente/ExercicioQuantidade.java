package pojo.paciente;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author augustopasini
 */

@Entity
public class ExercicioQuantidade implements Serializable {
    private Long id;
    private Exercicio exercicio;
    private Integer quantidade;
    private Date dataCadastro;
    private Double duracaoExercicio;
    private Double caloriasSemanais;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

        @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Double getDuracaoExercicio() {
        return duracaoExercicio;
    }

    public void setDuracaoExercicio(Double duracaoExercicio) {
        this.duracaoExercicio = duracaoExercicio;
    }

    public Double getCaloriasSemanais() {
        return caloriasSemanais;
    }

    public void setCaloriasSemanais(Double caloriasSemanais) {
        this.caloriasSemanais = caloriasSemanais;
    }
        
}

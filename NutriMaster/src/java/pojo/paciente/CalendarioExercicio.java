package pojo.paciente;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pojo.usuario.Usuario;

/**
 *
 * @author augustopasini
 */
@Entity
public class CalendarioExercicio implements Serializable {

    private Long id;
    private List<ExercicioQuantidade> exercicio;
    private Usuario usuario;
    private Date dataValido;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    public List<ExercicioQuantidade> getExercicio() {
        return exercicio;
    }

    public void setExercicio (List<ExercicioQuantidade> exercicio) {
        this.exercicio = exercicio;
    }

    @OneToOne
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataValido() {
        return dataValido;
    }

    public void setDataValido(Date dataValido) {
        this.dataValido = dataValido;
    }

}

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
 * @author gustavolazarottoschroeder
 */
@Entity
public class CalendarioAlimentacao implements Serializable {

    private Long id;
    private String diaSemana;
    private List<AlimentoQuantidade> alimento;
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

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    public List<AlimentoQuantidade> getAlimento() {
        return alimento;
    }

    public void setAlimento(List<AlimentoQuantidade> alimento) {
        this.alimento = alimento;
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

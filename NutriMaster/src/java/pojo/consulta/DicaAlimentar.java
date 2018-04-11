package pojo.consulta;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import pojo.usuario.Usuario;

/**
 *
 * @author gustavolazarottoschroeder
 */
@Entity
public class DicaAlimentar implements Serializable {

    private Long id;
    private String titulo;
    private String dica;
    private Date dataCadastro;
    private Boolean lida;
    private Usuario nutricionista;
    private Usuario paciente;
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 500)
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDica() {
        return dica;
    }

    public void setDica(String dica) {
        this.dica = dica;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    @OneToOne
    public Usuario getNutricionista() {
        return nutricionista;
    }

    public void setNutricionista(Usuario nutricionista) {
        this.nutricionista = nutricionista;
    }

    @OneToOne
    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }
}

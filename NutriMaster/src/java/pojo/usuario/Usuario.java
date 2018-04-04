package pojo.usuario;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author gustavolazarottoschroeder
 */

@Entity
public class Usuario implements Serializable {

    private Long id;
    private String nome;
    private String usuario;
    private String senha;
    private Endereco endereco;
    private List<InfoPaciente> infoPaciente;
    private Boolean administrador;
    private Boolean liberado;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @NotNull (message = "Qual seu nome?")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @NotNull (message = "Você não definiu seu usuário")
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @NotNull (message = "Ooops, e sua senha?")
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @OneToOne
    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    @OneToMany (fetch = FetchType.EAGER)
    public List<InfoPaciente> getInfoPaciente() {
        return infoPaciente;
    }

    public void setInfoPaciente(List<InfoPaciente> infoPaciente) {
        this.infoPaciente = infoPaciente;
    }

    public Boolean getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Boolean administrador) {
        this.administrador = administrador;
    }

    public Boolean getLiberado() {
        return liberado;
    }

    public void setLiberado(Boolean liberado) {
        this.liberado = liberado;
    }
}

package pojo.paciente;

import pojo.alimentacao.CalendarioAlimentacao;
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
public class Dieta implements Serializable{
    private Long id;
    private String dieta;
    private Date dataDieta;
    private Usuario nutricionistaResponsavel;
    private Boolean alimentacaoRestrita;
    private List<CalendarioAlimentacao> calendariAlimentacao;
    private Date vencimentoDieta;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDieta() {
        return dieta;
    }

    public void setDieta(String dieta) {
        this.dieta = dieta;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getDataDieta() {
        return dataDieta;
    }

    public void setDataDieta(Date dataDieta) {
        this.dataDieta = dataDieta;
    }

    @OneToOne
    public Usuario getNutricionistaResponsavel() {
        return nutricionistaResponsavel;
    }

    public void setNutricionistaResponsavel(Usuario nutricionistaResponsavel) {
        this.nutricionistaResponsavel = nutricionistaResponsavel;
    }

    public Boolean getAlimentacaoRestrita() {
        return alimentacaoRestrita;
    }

    public void setAlimentacaoRestrita(Boolean alimentacaoRestrita) {
        this.alimentacaoRestrita = alimentacaoRestrita;
    }

        @OneToMany (fetch = FetchType.EAGER)
            @Fetch(FetchMode.SUBSELECT) 

    public List<CalendarioAlimentacao> getCalendariAlimentacao() {
        return calendariAlimentacao;
    }

    public void setCalendariAlimentacao(List<CalendarioAlimentacao> calendariAlimentacao) {
        this.calendariAlimentacao = calendariAlimentacao;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getVencimentoDieta() {
        return vencimentoDieta;
    }

    public void setVencimentoDieta(Date vencimentoDieta) {
        this.vencimentoDieta = vencimentoDieta;
    }
   
}

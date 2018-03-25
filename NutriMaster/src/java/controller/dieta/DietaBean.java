package controller.dieta;

import controller.userControl.UsuarioBean;
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import pojo.paciente.Dieta;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author gustavolazarottoschroeder
 */

@ManagedBean
@ViewScoped
public class DietaBean implements Serializable{
    @ManagedProperty("calendarioAlimentacaoBean")
    private CalendarioAlimentacaoBean calendarioAlimentacaoBean;
    @ManagedProperty("usuarioBean")
    private UsuarioBean usuarioBean;
    private Dieta dieta;
    private Usuario paciente;

    public DietaBean() {
        this.dieta = new Dieta();
        this.paciente = new Usuario();
    }
    
    public void cadastrarDieta(){
        this.dieta.setCalendariAlimentacao(this.calendarioAlimentacaoBean.retornaCalendarios(this.paciente));
        this.dieta.setNutricionistaResponsavel(this.usuarioBean.getUsuario());
        this.dieta.setDataDieta(new Date());
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.dieta);
        em.getTransaction().commit();
        em.close();
        this.dieta = new Dieta();
    }

    public CalendarioAlimentacaoBean getCalendarioAlimentacaoBean() {
        return calendarioAlimentacaoBean;
    }

    public void setCalendarioAlimentacaoBean(CalendarioAlimentacaoBean calendarioAlimentacaoBean) {
        this.calendarioAlimentacaoBean = calendarioAlimentacaoBean;
    }

    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    public Dieta getDieta() {
        return dieta;
    }

    public void setDieta(Dieta dieta) {
        this.dieta = dieta;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }
}

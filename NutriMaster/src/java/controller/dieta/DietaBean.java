package controller.dieta;

import controller.userControl.UsuarioBean;
import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
public class DietaBean implements Serializable {

    @ManagedProperty("#{calendarioAlimentacaoBean}")
    private CalendarioAlimentacaoBean calendarioAlimentacaoBean;
    @ManagedProperty("#{usuarioBean}")
    private UsuarioBean usuarioBean;
    private Dieta dieta;
    private Usuario paciente;
    private String pacienteStr;

    public DietaBean() {
        this.dieta = new Dieta();
        this.paciente = new Usuario();
        this.pacienteStr = "";
    }

    public void atribuirPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        this.paciente = em.find(Usuario.class, this.pacienteStr.substring(0, this.pacienteStr.indexOf(';')));
        em.close();
    }

    public void cadastrarDieta() {
        Usuario u = this.usuarioBean.retornaUsuarioById(this.pacienteStr);
        this.calendarioAlimentacaoBean.cadastrarCalendarioAlimentacao(u);
        this.dieta.setCalendariAlimentacao(this.calendarioAlimentacaoBean.retornaCalendarios(u));
        this.dieta.setNutricionistaResponsavel(this.usuarioBean.getUsuario());
        this.dieta.setDataDieta(new Date());
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.dieta);
        em.getTransaction().commit();
        em.close();
        this.dieta = new Dieta();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Sucesso!", "Dieta cadastrada!"));
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

    public CalendarioAlimentacaoBean getCalendarioAlimentacaoBean() {
        return calendarioAlimentacaoBean;
    }

    public void setCalendarioAlimentacaoBean(CalendarioAlimentacaoBean calendarioAlimentacaoBean) {
        this.calendarioAlimentacaoBean = calendarioAlimentacaoBean;
    }

    public String getPacienteStr() {
        return pacienteStr;
    }

    public void setPacienteStr(String pacienteStr) {
        this.pacienteStr = pacienteStr;
    }

}

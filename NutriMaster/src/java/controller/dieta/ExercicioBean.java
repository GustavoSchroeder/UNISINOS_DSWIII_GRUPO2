package controller.dieta;

import controller.userControl.UsuarioBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import pojo.paciente.RotinaExercicio;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author augustopasini
 */
@ManagedBean
@ViewScoped
public class ExercicioBean implements Serializable {

    @ManagedProperty("#{calendarioExercicioBean}")
    private CalendarioExercicioBean calendarioExercicioBean;    
    @ManagedProperty("#{usuarioBean}")
    private UsuarioBean usuarioBean;
    private RotinaExercicio exercicio;
    private Usuario paciente;
    private String pacienteStr;
    private List<RotinaExercicio> listaExercicios;

    public ExercicioBean() {
        this.exercicio = new RotinaExercicio();
        this.paciente = new Usuario();
        this.pacienteStr = "";
        this.listaExercicios = new ArrayList<>();
    }

    public void adicionarExercicioLista() {
        Usuario u = this.usuarioBean.retornaUsuarioById(this.pacienteStr);
        this.calendarioExercicioBean.cadastrarCalendarioExercicio(u);
        this.exercicio.setCalendarioExercicio(this.calendarioExercicioBean.retornaCalendarios(u));
        this.exercicio.setNutricionistaResponsavel(this.usuarioBean.getUsuario());
        this.exercicio.setDuracaoExercicio(0.0);
        this.exercicio.setQtdeSemanal(1);
        this.listaExercicios.add(this.exercicio);
        this.exercicio = new RotinaExercicio();
    }

    public void atribuirPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        this.paciente = em.find(Usuario.class, this.pacienteStr.substring(0, this.pacienteStr.indexOf(';')));
        em.close();
    }

    public void cadastrarExercicios() {

        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        for (RotinaExercicio exerc : this.listaExercicios) {
            em.merge(exerc);
        }
        em.getTransaction().commit();
        em.close();

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Sucesso!", "Exercício cadastrado!"));
    }  
    
    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    public RotinaExercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio (RotinaExercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }

    public String getPacienteStr() {
        return pacienteStr;
    }

    public void setPacienteStr(String pacienteStr) {
        this.pacienteStr = pacienteStr;
    }

    public List<RotinaExercicio> getListaExercicios() {
        return listaExercicios;
    }

    public void setListaExercicios (List<RotinaExercicio> listaExercicios) {
        this.listaExercicios = listaExercicios;
    }

    public CalendarioExercicioBean getCalendarioExercicioBean() {
        return calendarioExercicioBean;
    }

    public void setCalendarioExercicioBean(CalendarioExercicioBean calendarioExercicioBean) {
        this.calendarioExercicioBean = calendarioExercicioBean;
    }

}

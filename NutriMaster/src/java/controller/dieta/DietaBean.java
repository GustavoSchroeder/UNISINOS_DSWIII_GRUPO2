package controller.dieta;

import controller.userControl.UsuarioBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import pojo.paciente.CalendarioAlimentacao;
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
    private List<Dieta> listaDietas;

    public DietaBean() {
        this.dieta = new Dieta();
        this.paciente = new Usuario();
        this.pacienteStr = "";
        this.listaDietas = new ArrayList<>();
    }

    public void adicionarDietaLista() {
        Usuario u = this.usuarioBean.retornaUsuarioById(this.pacienteStr);
        this.calendarioAlimentacaoBean.cadastrarCalendarioAlimentacao(u);
        this.dieta.setCalendariAlimentacao(this.calendarioAlimentacaoBean.retornaCalendarios(u));
        this.dieta.setNutricionistaResponsavel(this.usuarioBean.getUsuario());
        this.dieta.setDataDieta(new Date());
        this.listaDietas.add(this.dieta);
        this.dieta = new Dieta();
    }
    
    public void buscarDieta(String dia){
        for (CalendarioAlimentacao calendarioAlimentacao : this.dieta.getCalendariAlimentacao()) {
            if(calendarioAlimentacao.getDiaSemana().equalsIgnoreCase(dia)){
                this.calendarioAlimentacaoBean.setCalendariosCadastrados(calendarioAlimentacao);
                return;
            }
        }
    }

    public void atribuirPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        this.paciente = em.find(Usuario.class, this.pacienteStr.substring(0, this.pacienteStr.indexOf(';')));
        em.close();
    }

    public void cadastrarDietas() {
        if (!isAllDiasCadastrados()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oooops!", "Você não cadastrou dieta para todos os dias!"));
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        for (Dieta diet : this.listaDietas) {
            em.merge(diet);
        }
        em.getTransaction().commit();
        em.close();

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Sucesso!", "Dieta cadastrada!"));
    }

    public Boolean isDiaCadastrado(String dia) {
        for (Dieta listaDieta : this.listaDietas) {
            for (CalendarioAlimentacao calendarioAlimentacao : listaDieta.getCalendariAlimentacao()) {
                if (calendarioAlimentacao.getDiaSemana().equalsIgnoreCase(dia)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isAllDiasCadastrados() {
        Integer cont = 0;
        for (String dia : this.calendarioAlimentacaoBean.retornaDiasSemana()) {
            if (isDiaCadastrado(dia)) {
                cont++;
            }
        }
        return cont == 7;
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

    public List<Dieta> getListaDietas() {
        return listaDietas;
    }

    public void setListaDietas(List<Dieta> listaDietas) {
        this.listaDietas = listaDietas;
    }

}

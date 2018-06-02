package controller.dieta;

import controller.userControl.UsuarioBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import pojo.paciente.Exercicio;
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

    @ManagedProperty("#{usuarioBean}")
    private UsuarioBean usuarioBean;
    private RotinaExercicio exercicio;
    private Usuario paciente;
    private String pacienteStr;
    private List<RotinaExercicio> listaExercicios;
    String nomeExercicio;

    public ExercicioBean() {
        this.exercicio = new RotinaExercicio();
        this.paciente = new Usuario();
        this.pacienteStr = "";
        this.listaExercicios = new ArrayList<>();
        this.exercicio.setExercicio(new Exercicio());
    }

    @PostConstruct
    public void init() {
        retornaMeusExercicios();
    }

    public void adicionarExercicioLista() {
        Usuario u = this.usuarioBean.retornaUsuarioById(this.pacienteStr);
        this.exercicio.setNutricionistaResponsavel(this.usuarioBean.getUsuario());
        this.exercicio.setUsuario(u);
        this.exercicio.setExercicio(retornaExercicioByNome(nomeExercicio));
        this.listaExercicios.add(this.exercicio);
        this.exercicio = new RotinaExercicio();
    }

    public Exercicio retornaExercicioByNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Exercicio.class, nome);
        } catch (NullPointerException e) {
            return null;
        } finally {
            em.close();
        }

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

    public void retornaMeusExerciciosByPaciente() {
        this.listaExercicios.clear();
        Set<RotinaExercicio> exercicioSet = new HashSet<>();

        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM RotinaExercicio i");
        List<RotinaExercicio> auxList = query.getResultList();

        for (RotinaExercicio d : auxList) {
            Long id = retornarIdUsuario(this.usuarioBean.getUsuarioSelect());
            if (null != id && null != d.getUsuario()) {
                if (Objects.equals(d.getUsuario().getId(), id)) {
                    exercicioSet.add(d);
                }
            }
        }

        for (RotinaExercicio exer : exercicioSet) {
            this.listaExercicios.add(exer);
        }

        em.close();
    }

    public Long retornarIdUsuario(String usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Long.parseLong(usuario.substring(0, usuario.indexOf(';')));
        } catch (IndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public String retornaPaciente(RotinaExercicio exercicio) {
        try {
            for (RotinaExercicio exerc : listaExercicios) {
                return exerc.getUsuario().getNome();
            }
        } catch (NullPointerException e) {
        }
        return "-";
    }

    public String abrirRelatorioMeusExercicios() {
        if (this.usuarioBean.getUsuario().getAdministrador()) {
            this.usuarioBean.setDisplay(Boolean.TRUE);
        }
        return "/paciente/relatorioMeusExercicios.xhtml?faces-redirect=true";
    }

    public void retornaMeusExercicios() {
        this.listaExercicios.clear();
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM RotinaExercicio i");
        List<RotinaExercicio> auxList = query.getResultList();

        for (RotinaExercicio auxExercicio : auxList) {
            if (auxExercicio.getUsuario() != null) {
                if (Objects.equals(auxExercicio.getUsuario().getId(), this.usuarioBean.getUsuario().getId())) {
                    this.listaExercicios.add(auxExercicio);
                }
            }
        }

        em.close();
    }

    public List<String> retornaListNomeExercicios() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i.exercicio FROM Exercicio i ORDER BY i.exercicio");
        try {
            return query.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public void cadastrarExercicio() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.exercicio.getExercicio());
        em.getTransaction().commit();
        em.close();
        RequestContext.getCurrentInstance().execute("PF('dlg2').hide();");
        RequestContext.getCurrentInstance().update("formExercicio");
        this.exercicio.setExercicio(new Exercicio());
    }

    public void retirarExercicioLista(RotinaExercicio exerc) {
        this.listaExercicios.remove(exerc);
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

    public void setExercicio(RotinaExercicio exercicio) {
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

    public void setListaExercicios(List<RotinaExercicio> listaExercicios) {
        this.listaExercicios = listaExercicios;
    }

    public String getNomeExercicio() {
        return nomeExercicio;
    }

    public void setNomeExercicio(String nomeExercicio) {
        this.nomeExercicio = nomeExercicio;
    }

}

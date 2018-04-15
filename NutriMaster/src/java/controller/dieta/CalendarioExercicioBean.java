package controller.dieta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import pojo.paciente.Exercicio;
import pojo.paciente.ExercicioQuantidade;
import pojo.paciente.CalendarioExercicio;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author augustopasini
 */
@ManagedBean
@ViewScoped
public class CalendarioExercicioBean implements Serializable {
    
    private List<ExercicioQuantidade> exercicios;
    private Integer qtdeExercicio;
    private String exercicio;
    private CalendarioExercicio exerciciosCadastrados;
    private Exercicio objExercicio;
    private Double duracaoExercicio;
    
    public CalendarioExercicioBean() {
        zerarVariaveis();
    }
    
    private void zerarVariaveis() {
        this.exercicios = new ArrayList<>();
        this.exercicio = "";
        this.exerciciosCadastrados = new CalendarioExercicio();
        this.objExercicio = new Exercicio();
        this.duracaoExercicio = 0.1;
    }
    
    public Exercicio retornaExercicioById(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Exercicio.class, exercicio);
        } catch (NullPointerException e) {
            return null;
        } finally {
            em.close();
        }
        
    }
    
    public void retirarExercicioLista(ExercicioQuantidade aq) {
        this.exercicios.remove(aq);
    }
    
    public void adicionarExercicioLista() {
        Exercicio e = retornaExercicioById(this.exercicio);
        ExercicioQuantidade aq = new ExercicioQuantidade();
        aq.setExercicio(e);
        aq.setDataCadastro(new Date());
        aq.setQuantidade(this.qtdeExercicio);
        aq.setDuracaoExercicio(this.duracaoExercicio);
        aq.setCaloriasSemanais(this.qtdeExercicio * this.duracaoExercicio * e.getCalorias());
        this.exercicios.add(aq);
        this.exercicio = "";
        this.qtdeExercicio = 1;
        this.duracaoExercicio = 0.1;
    }
    
    private List<ExercicioQuantidade> mergeListExerciciosQtde() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        List<ExercicioQuantidade> auxList = new ArrayList<>();
        
        for (ExercicioQuantidade a : this.exercicios) {
            a.setId(null);
            auxList.add(a);
        }
        
        for (ExercicioQuantidade a : auxList) {
            em.persist(a);
        }
        
        em.getTransaction().commit();
        em.close();
        return auxList;
    }
    
    public void cadastrarExercicio() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.objExercicio);
        em.getTransaction().commit();
        em.close();
        RequestContext.getCurrentInstance().execute("PF('dlg2').hide();");
        RequestContext.getCurrentInstance().update("formExercicio");
        this.objExercicio = new Exercicio();
    }
    
    public List<CalendarioExercicio> retornaCalendarios(Usuario u) {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM CalendarioExercicio i "
                + "WHERE i.usuario = :usuario AND i.dataValido = :data");
        query.setParameter("usuario", u);
        query.setParameter("data", retornaUltimaDataCadastrada(em, u));
        List<CalendarioExercicio> calendarios = query.getResultList();
        em.close();
        return calendarios;
    }    
    
    private Date retornaUltimaDataCadastrada(EntityManager em, Usuario u) {
        Query query = em.createQuery("SELECT i.dataValido FROM CalendarioExercicio i "
                + "WHERE i.usuario = :usuario ORDER BY i.dataValido DESC");
        query.setParameter("usuario", u);
        try {
            return (Date) query.getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            return new Date();
        }
    }
    
    public void cadastrarCalendarioExercicio(Usuario u) {
        CalendarioExercicio calendarioExercicio = new CalendarioExercicio();
        calendarioExercicio.setDataValido(new Date());
        calendarioExercicio.setUsuario(u);
        calendarioExercicio.setExercicio(mergeListExerciciosQtde());
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(calendarioExercicio);
        em.getTransaction().commit();
        em.close();
        this.exercicios = new ArrayList<>();
    }
    
    public void adicionarExercicio() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.objExercicio);
        em.getTransaction().commit();
        em.close();
        this.objExercicio = new Exercicio();
    }
    
    public Exercicio retornaExercicio(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Exercicio a = em.find(Exercicio.class, id);
        em.close();
        return a;
    }
    
    public List<Exercicio> retornaListExercicios() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Exercicio i ORDER BY i.exercicio");
        try {
            return query.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
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
        
    public List<ExercicioQuantidade> getExercicios() {
        return exercicios;
    }
    
    public void setExercicios(List<ExercicioQuantidade> exercicios) {
        this.exercicios = exercicios;
    }
    
    public Integer getQtdeExercicio() {
        return qtdeExercicio;
    }
    
    public void setQtdeExercicio(Integer qtdeExercicio) {
        this.qtdeExercicio = qtdeExercicio;
    }
    
    public String getExercicio() {
        return exercicio;
    }
    
    public void setExercicio (String exercicio) {
        this.exercicio = exercicio;
    }
    
    public CalendarioExercicio getExerciciosCadastrados() {
        return exerciciosCadastrados;
    }
    
    public void setExerciciosCadastrados(CalendarioExercicio exerciciosCadastrados) {
        this.exerciciosCadastrados = exerciciosCadastrados;
    }
    
    public Exercicio getObjExercicio() {
        return objExercicio;
    }
    
    public void setObjExercicio(Exercicio objExercicio) {
        this.objExercicio = objExercicio;
    }

    public Double getDuracaoExercicio() {
        return duracaoExercicio;
    }

    public void setDuracaoExercicio(Double duracaoExercicio) {
        this.duracaoExercicio = duracaoExercicio;
    }
    
}

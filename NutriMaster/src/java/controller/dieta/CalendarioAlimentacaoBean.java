package controller.dieta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import pojo.paciente.Alimento;
import pojo.paciente.CalendarioAlimentacao;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author gustavolazarottoschroeder
 */
@ManagedBean
@ViewScoped
public class CalendarioAlimentacaoBean implements Serializable {
    
    private List<Alimento> alimentos;
    private String diaSemana;
    private Alimento alimento;
    
    public CalendarioAlimentacaoBean() {
        zerarVariaveis();
    }
    
    private void zerarVariaveis() {
        this.alimentos = new ArrayList<>();
        this.diaSemana = retornaDiasSemana().get(0);
        this.alimento = new Alimento();
    }
    
    public void adicionarAlimentoLista() {
        this.alimentos.add(this.alimento);
        this.alimento = new Alimento();
    }
    
    public void cadastrarAlimento(){
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.alimento);
        em.getTransaction().commit();
        em.close();
                RequestContext.getCurrentInstance().execute("PF('dlg2').hide();");
        RequestContext.getCurrentInstance().update("formDieta");
        this.alimento = new Alimento();
    }
    
    public List<CalendarioAlimentacao> retornaCalendarios(Usuario u) {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM CalendarioAlimentacao i "
                + "WHERE i.usuario = :usuario AND i.dataValido = :data");
        query.setParameter("usuario", u);
        query.setParameter("data", retornaUltimaDataCadastrada(em, u));
        List<CalendarioAlimentacao> calendarios = query.getResultList();
        em.close();
        return calendarios;
    }
    
    public Map<String, List<Alimento>> retornaAlimentosPorDia(Usuario u) {
        List<CalendarioAlimentacao> calendarios = retornaCalendarios(u);
        Map<String, List<Alimento>> dictionarySemanaAlimentos = new HashMap<>();
        
        for (CalendarioAlimentacao calendario : calendarios) {
            dictionarySemanaAlimentos.put(calendario.getDiaSemana(), calendario.getAlimento());
        }
        return dictionarySemanaAlimentos;
    }
    
    private Date retornaUltimaDataCadastrada(EntityManager em, Usuario u) {
        Query query = em.createQuery("SELECT i FROM CalendarioAlimentacao i "
                + "WHERE i.usuario = :usuario ORDER BY i.dataValido DESC");
        query.setParameter("usuario", u);
        try {
            return (Date) query.getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            return new Date();
        } finally {
            
        }
    }
    
    public void cadastrarCalendarioAlimentacao(Usuario u) {
        CalendarioAlimentacao calendarioAlimentacao = new CalendarioAlimentacao();
        calendarioAlimentacao.setAlimento(this.alimentos);
        calendarioAlimentacao.setDiaSemana(this.diaSemana);
        calendarioAlimentacao.setDataValido(new Date());
        calendarioAlimentacao.setUsuario(u);
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(calendarioAlimentacao);
        em.getTransaction().commit();
        em.close();
        RequestContext.getCurrentInstance().execute("PF('dlg2').hide();");
        RequestContext.getCurrentInstance().update("formDieta");
    }
    
    public void adicionarAlimento() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.alimento);
        em.getTransaction().commit();
        em.close();
        this.alimento = new Alimento();
    }
    
    public Alimento retornaAlimento(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Alimento a = em.find(Alimento.class, id);
        em.close();
        return a;
    }
    
    public List<Alimento> retornaListAlimentos() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Alimento i ORDER BY i.alimento");
        try {
            return query.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    public final List<String> retornaDiasSemana() {
        List<String> dias = new ArrayList<>();
        dias.add("Segunda");
        dias.add("Terça");
        dias.add("Quarta");
        dias.add("Quinta");
        dias.add("Sexta");
        dias.add("Sábado");
        dias.add("Domingo");
        return dias;
    }
    
    public List<Alimento> getAlimentos() {
        return alimentos;
    }
    
    public void setAlimentos(List<Alimento> alimentos) {
        this.alimentos = alimentos;
    }
    
    public Alimento getAlimento() {
        return alimento;
    }
    
    public void setAlimento(Alimento alimento) {
        this.alimento = alimento;
    }
    
    public String getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }
}

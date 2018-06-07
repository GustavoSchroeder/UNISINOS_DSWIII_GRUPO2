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
import pojo.alimentacao.Alimento;
import pojo.alimentacao.AlimentoQuantidade;
import pojo.alimentacao.CalendarioAlimentacao;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author gustavolazarottoschroeder
 */
@ManagedBean
@ViewScoped
public class CalendarioAlimentacaoBean implements Serializable {

    private List<AlimentoQuantidade> alimentos;
    private List<String> diasSemana;
    private Integer qtdeAlimento;
    private String alimento;
    private CalendarioAlimentacao calendariosCadastrados;
    private Alimento objAlimento;

    public CalendarioAlimentacaoBean() {
        zerarVariaveis();
    }

    private void zerarVariaveis() {
        this.alimentos = new ArrayList<>();
        this.alimento = "";
        this.calendariosCadastrados = new CalendarioAlimentacao();
        this.objAlimento = new Alimento();
        this.diasSemana = new ArrayList<>();
    }

    public Alimento retornaAlimentoById(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Alimento.class, alimento);
        } catch (NullPointerException e) {
            return null;
        } finally {
            em.close();
        }

    }

    public void retirarAlimentoLista(AlimentoQuantidade aq) {
        this.alimentos.remove(aq);
    }

    public void adicionarAlimentoLista() {
        AlimentoQuantidade aq = new AlimentoQuantidade();
        aq.setAlimento(retornaAlimentoById(this.alimento));
        aq.setDataCadastro(new Date());
        aq.setQuantidade(this.qtdeAlimento);
        this.alimentos.add(aq);
        this.alimento = "";
        this.qtdeAlimento = 1;
    }

    private List<AlimentoQuantidade> mergeListAlimentosQtde() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        List<AlimentoQuantidade> auxList = new ArrayList<>();

        for (AlimentoQuantidade a : this.alimentos) {
            a.setId(null);
            auxList.add(a);
        }

        for (AlimentoQuantidade a : auxList) {
            em.persist(a);
        }

        em.getTransaction().commit();
        em.close();
        return auxList;
    }

    public void cadastrarAlimento() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.objAlimento);
        em.getTransaction().commit();
        em.close();
        RequestContext.getCurrentInstance().execute("PF('dlg2').hide();");
        RequestContext.getCurrentInstance().update("formDieta");
        this.objAlimento = new Alimento();
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

    public Map<String, List<AlimentoQuantidade>> retornaAlimentosPorDia(Usuario u) {
        List<CalendarioAlimentacao> calendarios = retornaCalendarios(u);
        Map<String, List<AlimentoQuantidade>> dictionarySemanaAlimentos = new HashMap<>();

        for (CalendarioAlimentacao calendario : calendarios) {
            dictionarySemanaAlimentos.put(calendario.getDiaSemana(), calendario.getAlimento());
        }
        return dictionarySemanaAlimentos;
    }

    private Date retornaUltimaDataCadastrada(EntityManager em, Usuario u) {
        Query query = em.createQuery("SELECT i.dataValido FROM CalendarioAlimentacao i "
                + "WHERE i.usuario = :usuario ORDER BY i.dataValido DESC");
        query.setParameter("usuario", u);
        try {
            return (Date) query.getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            return new Date();
        }
    }

    public Boolean isDiaCadastrado(String dia, List<String> diasCadastrados) {

        try {
            for (String diaCadastrado : diasCadastrados) {
                if (diaCadastrado.equalsIgnoreCase(dia)) {
                    return Boolean.TRUE;
                }
            }
        } catch (NullPointerException e) {
        }

        return Boolean.FALSE;
    }

    public void cadastrarCalendarioAlimentacao(Usuario u, List<String> diasCadastrados) {
        for (String dia : this.diasSemana) {
            CalendarioAlimentacao calendarioAlimentacao = new CalendarioAlimentacao();
            calendarioAlimentacao.setDiaSemana(dia);
            calendarioAlimentacao.setDataValido(new Date());
            calendarioAlimentacao.setUsuario(u);
            calendarioAlimentacao.setAlimento(mergeListAlimentosQtde());
            if (!isDiaCadastrado(dia, diasCadastrados)) {
                EntityManager em = JPAUtil.getEntityManager();
                em.getTransaction().begin();
                em.merge(calendarioAlimentacao);
                em.getTransaction().commit();
                em.close();
            }
        }
        this.alimentos = new ArrayList<>();
    }

    public void adicionarAlimento() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.objAlimento);
        em.getTransaction().commit();
        em.close();
        this.objAlimento = new Alimento();
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

    public List<String> retornaListNomeAlimentos() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i.alimento FROM Alimento i ORDER BY i.alimento");
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
        dias.add("Segunda-Feira");
        dias.add("Terça-Feira");
        dias.add("Quarta-Feira");
        dias.add("Quinta-Feira");
        dias.add("Sexta-Feira");
        dias.add("Sábado");
        dias.add("Domingo");
        return dias;
    }

    public List<AlimentoQuantidade> getAlimentos() {
        return alimentos;
    }

    public void setAlimentos(List<AlimentoQuantidade> alimentos) {
        this.alimentos = alimentos;
    }

    public Integer getQtdeAlimento() {
        return qtdeAlimento;
    }

    public void setQtdeAlimento(Integer qtdeAlimento) {
        this.qtdeAlimento = qtdeAlimento;
    }

    public String getAlimento() {
        return alimento;
    }

    public void setAlimento(String alimento) {
        this.alimento = alimento;
    }

    public CalendarioAlimentacao getCalendariosCadastrados() {
        return calendariosCadastrados;
    }

    public void setCalendariosCadastrados(CalendarioAlimentacao calendariosCadastrados) {
        this.calendariosCadastrados = calendariosCadastrados;
    }

    public Alimento getObjAlimento() {
        return objAlimento;
    }

    public void setObjAlimento(Alimento objAlimento) {
        this.objAlimento = objAlimento;
    }

    public List<String> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(List<String> diasSemana) {
        this.diasSemana = diasSemana;
    }
}

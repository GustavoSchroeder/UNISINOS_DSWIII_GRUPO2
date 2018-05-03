package controller.dieta;

import controller.userControl.UsuarioBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import pojo.consulta.DicaAlimentar;
import util.JPAUtil;

/**
 *
 * @author gustavolazarottoschroeder
 */
@ManagedBean
@ViewScoped
public class DicasAlimentaresBean {
    
    private String titulo;
    private String texto;
    @ManagedProperty("#{usuarioBean}")
    private UsuarioBean usuarioBean;
    private String pacienteStr;
    private Integer tipo;
    private List<DicaAlimentar> listOfDicas;
    private Boolean pesquisarPorNome;
    private String buscarPorNomePaciente;
    private DicaAlimentar dicaAlimentarSelect;
    
    public DicasAlimentaresBean() {
        zerarVariaveis();
        this.listOfDicas = new ArrayList<>();
        /*
        0 - NÃO LIDOS
        1 - LIDOS
        2 - TODOS
         */
        this.tipo = 0;
        this.pesquisarPorNome = Boolean.FALSE;
        this.buscarPorNomePaciente = null;
    }
    
    private void zerarVariaveis() {
        this.titulo = "";
        this.texto = "";
        this.pacienteStr = "";
        this.dicaAlimentarSelect = new DicaAlimentar();
    }
    
    public Long qtdeNaoLidasPorPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT COUNT(i.id) FROM DicaAlimentar i WHERE i.paciente.id = :pacienteId");
        query.setParameter("pacienteId", this.usuarioBean.getUsuario());
        try {
            return (Long) query.getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            return 0L;
        } finally {
            em.close();
        }
    }
    
    private List<DicaAlimentar> retornaDicasNutricionista(EntityManager em) {
        Query query = em.createQuery("SELECT i FROM DicaAlimentar i "
                + "WHERE i.nutricionista.id = :nutricionistaId");
        query.setParameter("nutricionistaId", this.usuarioBean.getUsuario().getId());
        return query.getResultList();
    }
    
    private List<DicaAlimentar> retornaDicasPorPaciente(EntityManager em, String nomePaciente) {
        Query query = em.createQuery("SELECT i FROM DicaAlimentar i WHERE i.paciente.nome = :nomePaciente");
        query.setParameter("nomePaciente", this.usuarioBean.retornaNome(nomePaciente));
        return query.getResultList();
    }
    
    public List<DicaAlimentar> retornaDicasPaciente(EntityManager em) {
        Query query;
        if (this.tipo == 2) {
            query = em.createQuery("SELECT i FROM DicaAlimentar i WHERE i.paciente.id = :pacienteId");
            query.setParameter("pacienteId", this.usuarioBean.getUsuario().getId());
        } else {
            query = em.createQuery("SELECT i FROM DicaAlimentar i WHERE i.paciente.id = :pacienteId AND i.lida = :lida");
            query.setParameter("pacienteId", this.usuarioBean.getUsuario().getId());
            if (this.tipo == 0) {
                query.setParameter("lida", Boolean.FALSE);
            } else if (this.tipo == 1) {
                query.setParameter("lida", Boolean.TRUE);
            }
        }
        return query.getResultList();
    }
    
    public void retornaListaDicas() {
        EntityManager em = JPAUtil.getEntityManager();
        this.listOfDicas.clear();
        if (this.usuarioBean.getUsuario().getAdministrador()) {
            if (this.pesquisarPorNome) {
                this.listOfDicas.addAll(retornaDicasPorPaciente(em, this.buscarPorNomePaciente));
            } else {
                this.listOfDicas.addAll(retornaDicasNutricionista(em));
            }
        } else {
            this.listOfDicas.addAll(retornaDicasPaciente(em));
        }
        em.close();
    }
    
    public void cadastrarDica() {
        DicaAlimentar dica = new DicaAlimentar();
        dica.setDica(this.texto);
        dica.setTitulo(this.titulo);
        dica.setDataCadastro(new Date());
        dica.setLida(Boolean.FALSE);
        dica.setPaciente(this.usuarioBean.retornaUsuarioById(this.pacienteStr));
        dica.setNutricionista(this.usuarioBean.getUsuario());
        
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(dica);
        em.getTransaction().commit();
        em.close();
        zerarVariaveis();
    }
    
    public void editarDica() {
        this.dicaAlimentarSelect.setDataCadastro(new Date());
        this.dicaAlimentarSelect.setLida(Boolean.FALSE);
        this.dicaAlimentarSelect.setNutricionista(this.usuarioBean.getUsuario());
        this.dicaAlimentarSelect.setTitulo(this.titulo);
        this.dicaAlimentarSelect.setDica(this.texto);
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(this.dicaAlimentarSelect);
        em.getTransaction().commit();
        em.close();
        zerarVariaveis();
        retornaListaDicas();
        RequestContext.getCurrentInstance().execute("PF('editarDica').hide()");
    }
    
    public DicaAlimentar editarDica(DicaAlimentar dica) {
        dica.setLida(Boolean.TRUE);
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(dica);
        em.getTransaction().commit();
        em.close();
        return dica;
    }
    
    public void abrirDicaAlimentar(DicaAlimentar dica) {
        if (!this.usuarioBean.getUsuario().getAdministrador()) {
            dica = editarDica(dica);
        }
        this.dicaAlimentarSelect = dica;
        RequestContext.getCurrentInstance().execute("PF('fullDica').show()");
    }
    
    public void editarDicaAlimentar(DicaAlimentar dica) {
        this.dicaAlimentarSelect = dica;
        this.titulo = dica.getTitulo();
        this.texto = dica.getDica();
        RequestContext.getCurrentInstance().execute("PF('editarDica').show()");
    }
    
    public void fecharDicaAlimentar() {
        this.dicaAlimentarSelect = new DicaAlimentar();
        RequestContext.getCurrentInstance().execute("PF('fullDica').hide()");
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getTexto() {
        return texto;
    }
    
    public void setTexto(String texto) {
        this.texto = texto;
    }
    
    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }
    
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }
    
    public String getPacienteStr() {
        return pacienteStr;
    }
    
    public void setPacienteStr(String pacienteStr) {
        this.pacienteStr = pacienteStr;
    }
    
    public Integer getTipo() {
        return tipo;
    }
    
    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }
    
    public List<DicaAlimentar> getListOfDicas() {
        return listOfDicas;
    }
    
    public void setListOfDicas(List<DicaAlimentar> listOfDicas) {
        this.listOfDicas = listOfDicas;
    }
    
    public Boolean getPesquisarPorNome() {
        return pesquisarPorNome;
    }
    
    public void setPesquisarPorNome(Boolean pesquisarPorNome) {
        this.pesquisarPorNome = pesquisarPorNome;
    }
    
    public String getBuscarPorNomePaciente() {
        return buscarPorNomePaciente;
    }
    
    public void setBuscarPorNomePaciente(String buscarPorNomePaciente) {
        this.buscarPorNomePaciente = buscarPorNomePaciente;
    }
    
    public DicaAlimentar getDicaAlimentarSelect() {
        return dicaAlimentarSelect;
    }
    
    public void setDicaAlimentarSelect(DicaAlimentar dicaAlimentarSelect) {
        this.dicaAlimentarSelect = dicaAlimentarSelect;
    }
}

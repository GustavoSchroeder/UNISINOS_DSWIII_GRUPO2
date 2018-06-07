package controller.financeiro;

import controller.userControl.UsuarioBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import pojo.financeiro.Mensalidade;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author augustopasini
 */
@ManagedBean
@ViewScoped
public class MensalidadeBean implements Serializable {

    @ManagedProperty("#{usuarioBean}")
    private UsuarioBean usuarioBean;
    private Usuario paciente;
    private String pacienteStr;
    private Mensalidade mensalidade;
    private List<Mensalidade> mensalidades;

    public MensalidadeBean() {
        this.paciente = new Usuario();
        this.pacienteStr = "";
        this.mensalidades = new ArrayList<>();
    }

    public void retornaListaDicas() {
        EntityManager em = JPAUtil.getEntityManager();
        this.mensalidades.clear();
        this.mensalidades.addAll(retornaMensalidadesPaciente(em));
        em.close();
    }

    public List<Mensalidade> retornaMensalidadesPaciente(EntityManager em) {
        Query query;
        query = em.createQuery("SELECT i FROM Mensalidade i WHERE i.paciente.id = :pacienteId");
        query.setParameter("pacienteId", this.usuarioBean.getUsuario().getId());
        return query.getResultList();
    }

    public List<Mensalidade> retornaMensalidadesPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query;
        query = em.createQuery("SELECT i FROM Mensalidade i WHERE i.paciente.id = :pacienteId");
        query.setParameter("pacienteId", this.usuarioBean.getUsuario().getId());
        try {
            return query.getResultList();
        } catch (NullPointerException e) {

        } finally {
            em.close();
            return new ArrayList<>();
        }
    }

    public void pagarMensalidade(Long id) {
        this.mensalidade = this.buscaPorId(id);
        this.mensalidade.setPago(true);
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(mensalidade);
        em.getTransaction().commit();
        em.close();
    }

    public Mensalidade buscaPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Mensalidade mens;
        mens = em.find(Mensalidade.class, id);
        em.close();
        return mens;
    }

    public void atribuirPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        this.paciente = em.find(Usuario.class, this.pacienteStr.substring(0, this.pacienteStr.indexOf(';')));
        em.close();
    }

    public void adicionarMensalidade() {

    }

    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
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

    public Mensalidade getMensalidade() {
        return mensalidade;
    }

    public void setMensalidade(Mensalidade mensalidade) {
        this.mensalidade = mensalidade;
    }

    public List<Mensalidade> getMensalidades() {
        return mensalidades;
    }

    public void setMensalidades(List<Mensalidade> mensalidades) {
        this.mensalidades = mensalidades;
    }

}

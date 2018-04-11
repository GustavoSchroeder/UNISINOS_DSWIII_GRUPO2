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

    public DicasAlimentaresBean() {
        zerarVariaveis();
        this.listOfDicas = new ArrayList<>();
        /*
        0 - NÃO LIDOS
        1 - LIDOS
        2 - TODOS
         */
        this.tipo = 0;
    }

    private void zerarVariaveis() {
        this.titulo = "";
        this.texto = "";
        this.pacienteStr = "";
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
            this.listOfDicas.addAll(retornaDicasNutricionista(em));
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
}

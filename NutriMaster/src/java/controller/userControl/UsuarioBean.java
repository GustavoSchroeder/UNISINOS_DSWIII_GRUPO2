package controller.userControl;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import pojo.usuario.Endereco;
import pojo.usuario.InfoPaciente;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author gustavolazarottoschroeder
 */
@ManagedBean
@SessionScoped
public class UsuarioBean implements Serializable {

    private Usuario usuario;
    private Endereco endereco;
    private InfoPaciente infoPaciente;
    private String usuarioLogin;
    private String senhaLogIn;
    private String tipoUsuario;
    private List<Usuario> nutricionistaNaoLiberados;

    public UsuarioBean() {
        this.usuario = new Usuario();
        this.endereco = new Endereco();
        this.infoPaciente = new InfoPaciente();
        this.nutricionistaNaoLiberados = new ArrayList<>();
        this.usuarioLogin = "";
        this.senhaLogIn = "";
        this.tipoUsuario = "Paciente";
    }

    public String cadastrarUsuario() throws NoSuchAlgorithmException {
        EntityManager em = JPAUtil.getEntityManager();
        this.usuario.setSenha(retornaMD5(this.usuario.getSenha()));
        this.usuario.setEndereco(this.endereco);
        List<InfoPaciente> listOfInfo = new ArrayList<>();
        this.infoPaciente.setDataMarcacao(new Date());
        listOfInfo.add(this.infoPaciente);
        this.usuario.setInfoPaciente(listOfInfo);

        if (this.tipoUsuario.equalsIgnoreCase("Paciente")) {
            this.usuario.setAdministrador(Boolean.FALSE);
            this.usuario.setLiberado(Boolean.TRUE);
        } else {
            this.usuario.setAdministrador(Boolean.TRUE);
            this.usuario.setLiberado(Boolean.FALSE);
        }

        //persistencia
        em.getTransaction().begin();
        em.persist(this.endereco);
        em.persist(this.infoPaciente);
        em.merge(this.usuario);
        em.getTransaction().commit();

        em.close();
        this.endereco = new Endereco();
        this.infoPaciente = new InfoPaciente();
        this.usuario = new Usuario();
        this.tipoUsuario = "Paciente";
        return "index.xhtml";
    }

    public Long qtdeNutricionistasAprovar() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT COUNT(i.id) FROM Usuario i "
                + "WHERE i.liberado = :liberado AND i.administrador = :adm");
        query.setParameter("liberado", Boolean.FALSE);
        query.setParameter("adm", Boolean.TRUE);
       try{
           return (Long) query.getResultList().get(0);
       }catch(IndexOutOfBoundsException e){
           return 0L;
       }finally{
        em.close();
       }        
    }

    public void getNutricionistaNaoAprovados(Boolean show) {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Usuario i "
                + "WHERE i.liberado = :liberado AND i.administrador = :adm");
        query.setParameter("liberado", Boolean.FALSE);
        query.setParameter("adm", Boolean.TRUE);
        this.nutricionistaNaoLiberados = query.getResultList();
        em.close();
        if (show) {
            RequestContext.getCurrentInstance().execute("PF('dlg3').show()");
        }
    }

    public void usuarioIsNutricionista(Usuario u) {
        EntityManager em = JPAUtil.getEntityManager();
        u.setLiberado(Boolean.TRUE);
        em.getTransaction().begin();
        em.merge(u);
        em.getTransaction().commit();
        em.close();
        getNutricionistaNaoAprovados(Boolean.FALSE);
    }

    public void usuarioIsPaciente(Usuario u) {
        EntityManager em = JPAUtil.getEntityManager();
        u.setLiberado(Boolean.TRUE);
        u.setAdministrador(Boolean.FALSE);
        em.getTransaction().begin();
        em.merge(u);
        em.getTransaction().commit();
        em.close();
        getNutricionistaNaoAprovados(Boolean.FALSE);
    }

    public String novoUsuario() {
        this.usuario = new Usuario();
        return "novoUsuario.xhtml";
    }

    public String isUsuarioAllowedOnDieta() {
        if (this.usuario.getAdministrador()) {
            return "/paciente/dieta.xhtml";
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Oops!", "Você não pode criar sua própria dieta, contate seu nutricionista."));
        return null;
    }

    public String logOut() {
        return "/index.xhtml?faces-redirect=true";
    }

    public String realizarLogIn() throws NoSuchAlgorithmException {
        if (this.usuarioLogin.trim().equalsIgnoreCase("")
                || this.senhaLogIn.trim().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Usuário não encontrado :("));
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Usuario i "
                + "WHERE i.usuario = :usuario AND i.senha = :senha AND i.liberado = :liberado");
        query.setParameter("usuario", this.usuarioLogin);
        query.setParameter("senha", retornaMD5(this.senhaLogIn));
        query.setParameter("liberado", Boolean.TRUE);

        List<Usuario> usuarios = query.getResultList();
        try {
            if (null == usuarios || usuarios.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Oops!", "Usuário não encontrado :("));
                return null;
            } else {
                this.usuario = (Usuario) query.getResultList().get(0);
                return "telaInicial.xhtml";
            }
        } catch (NullPointerException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Usuário não encontrado :("));
            return null;
        } finally {
            this.senhaLogIn = "";
            this.usuarioLogin = "";
            em.close();
        }
    }

    public String retornaMD5(String conteudo) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(conteudo.getBytes());
        byte[] hashMd5 = md.digest();
        return (stringHexa(hashMd5));
    }

    public Usuario buscaPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Usuario usuario;
        usuario = em.find(Usuario.class, id);
        em.close();
        return usuario;
    }

    public List<UsuarioBean> buscaPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Usuario i WHERE i.nome = :nome ORDER BY i.nome ASC");
        try {
            return query.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public Usuario retornaUsuarioById(String usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, extractID(usuario));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    private Long extractID(String usuario) {
        return Long.parseLong(usuario.substring(0, usuario.indexOf(";")));
    }

    public String retornaNome(String nome) {
        return nome.substring(nome.indexOf(';') + 1);
    }

    public List<String> retornaUsuariosPacientesCod() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Usuario i WHERE i.administrador = :adm ORDER BY i.nome");
        query.setParameter("adm", Boolean.FALSE);
        List<String> usuarios = new ArrayList<>();
        List<Usuario> user = query.getResultList();
        for (Usuario object : user) {
            usuarios.add(object.getId() + ";" + object.getNome());
        }
        try {
            return usuarios;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Usuario> retornaUsuariosPacientes() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Usuario i "
                + "WHERE i.administrador = :adm ORDER BY i.nome");
        query.setParameter("adm", Boolean.FALSE);
        try {
            return query.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<String> retornaListaUF() {
        List<String> ufs = new ArrayList<>();
        ufs.add("AC");
        ufs.add("AL");
        ufs.add("AM");
        ufs.add("AP");
        ufs.add("BA");
        ufs.add("CE");
        ufs.add("DF");
        ufs.add("ES");
        ufs.add("GO");
        ufs.add("MA");
        ufs.add("MG");
        ufs.add("MS");
        ufs.add("MT");
        ufs.add("PA");
        ufs.add("PB");
        ufs.add("PE");
        ufs.add("PI");
        ufs.add("PR");
        ufs.add("RJ");
        ufs.add("RN");
        ufs.add("RO");
        ufs.add("RR");
        ufs.add("RS");
        ufs.add("SC");
        ufs.add("SE");
        ufs.add("SP");
        ufs.add("TO");
        return ufs;
    }

    private static String stringHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) {
                s.append('0');
            }
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public String getSenhaLogIn() {
        return senhaLogIn;
    }

    public void setSenhaLogIn(String senhaLogIn) {
        this.senhaLogIn = senhaLogIn;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public InfoPaciente getInfoPaciente() {
        return infoPaciente;
    }

    public void setInfoPaciente(InfoPaciente infoPaciente) {
        this.infoPaciente = infoPaciente;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public List<Usuario> getNutricionistaNaoLiberados() {
        return nutricionistaNaoLiberados;
    }

    public void setNutricionistaNaoLiberados(List<Usuario> nutricionistaNaoLiberados) {
        this.nutricionistaNaoLiberados = nutricionistaNaoLiberados;
    }

}

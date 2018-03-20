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

    public UsuarioBean() {
        this.usuario = new Usuario();
        this.endereco = new Endereco();
        this.infoPaciente = new InfoPaciente();
        this.usuarioLogin = "";
        this.senhaLogIn = "";
    }

    public String cadastrarUsuario() throws NoSuchAlgorithmException {
        EntityManager em = JPAUtil.getEntityManager();
        this.usuario.setSenha(retornaMD5(this.usuario.getSenha()));
        this.usuario.setEndereco(this.endereco);
        List<InfoPaciente> listOfInfo = new ArrayList<>();
        this.infoPaciente.setDataMarcacao(new Date());
        listOfInfo.add(this.infoPaciente);
        this.usuario.setInfoPaciente(listOfInfo);
        this.usuario.setAdministrador(Boolean.FALSE);

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
        return "index.xhtml";
    }
    
    public String novoUsuario(){
        this.usuario = new Usuario();
        return "novoUsuario.xhtml";
    }

    public String realizarLogIn() throws NoSuchAlgorithmException {
        if(this.usuarioLogin.trim().equalsIgnoreCase("") 
                || this.senhaLogIn.trim().equalsIgnoreCase("")){
             FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Usuário não encontrado :("));
            return null;
         }
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Usuario i WHERE i.usuario = :usuario AND i.senha = :senha");
        query.setParameter("usuario", this.usuarioLogin);
        query.setParameter("senha", retornaMD5(this.senhaLogIn));
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

    private Boolean verificaPendenciasCadastro() {
        Integer cont = 0;
        if (this.usuario.getNome().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Você não disse seu nome :("));
            cont++;
        }
        if (this.usuario.getUsuario().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual seu usuário?"));
            cont++;
        }
        if (this.usuario.getSenha().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual sua senha?"));
            cont++;
        }
//        if (null ==  this.endereco.getEndereco() || this.endereco.getEndereco().equalsIgnoreCase("")) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage("Oops!", "Qual seu endereço?"));
//            cont++;
//        }
//        if (this.endereco.getBairro().equalsIgnoreCase("")) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage("Oops!", "Qual seu bairro?"));
//            cont++;
//        }
//        if (this.endereco.getCep().equalsIgnoreCase("")) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage("Oops!", "Qual seu CEP?"));
//            cont++;
//        }
//        if (this.endereco.getCidade().equalsIgnoreCase("")) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage("Oops!", "Qual sua cidade?"));
//            cont++;
//        }
//        if (this.endereco.getEndereco().equalsIgnoreCase("")) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage("Oops!", "Qual seu endereço?"));
//            cont++;
//        }
//        if (null == this.endereco.getNro()) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage("Oops!", "Você esqueceu do número"));
//            cont++;
//        }
//        if (this.endereco.getUf().equalsIgnoreCase("")) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage("Oops!", "Qual seu estado?"));
//            cont++;
//        }

        return cont != 0;
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
}

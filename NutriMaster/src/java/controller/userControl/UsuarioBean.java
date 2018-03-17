package controller.userControl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import pojo.usuario.Endereco;
import pojo.usuario.InfoPaciente;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author gustavolazarottoschroeder
 */
@ManagedBean
@ViewScoped
public class UsuarioBean {

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
        if (verificaPendenciasCadastro()) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(this.senhaLogIn.getBytes());
        byte[] hashMd5 = md.digest();
        stringHexa(hashMd5);
        em.close();
        return null;
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
        if (this.endereco.getEndereco().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual seu endereço?"));
            cont++;
        }
        if (this.endereco.getBairro().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual seu bairro?"));
            cont++;
        }
        if (this.endereco.getCep().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual seu CEP?"));
            cont++;
        }
        if (this.endereco.getCidade().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual sua cidade?"));
            cont++;
        }
        if (this.endereco.getEndereco().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual seu endereço?"));
            cont++;
        }
        if (null == this.endereco.getNro()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Você esqueceu do número"));
            cont++;
        }
        if (this.endereco.getUf().equalsIgnoreCase("")) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oops!", "Qual seu estado?"));
            cont++;
        }

        return cont != 0;
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

}

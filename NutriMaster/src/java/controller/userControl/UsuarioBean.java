package controller.userControl;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
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
    private Boolean display;
    private String usuarioSelect;
    private Double peso;
    private Double altura;
    private Boolean changeOnPeso;
    private Boolean changeOnAltura;
    private BarChartModel barModel;
    private Integer tipoGrafico;

    public UsuarioBean() {
        this.usuario = new Usuario();
        this.endereco = new Endereco();
        this.infoPaciente = new InfoPaciente();
        this.nutricionistaNaoLiberados = new ArrayList<>();
        this.usuarioLogin = "";
        this.senhaLogIn = "";
        this.tipoUsuario = "Paciente";
        this.display = Boolean.FALSE;
        this.usuarioSelect = null;
        this.peso = 0.0;
        this.altura = 0.0;
        this.changeOnAltura = Boolean.FALSE;
        this.changeOnPeso = Boolean.FALSE;
        this.tipoGrafico = 0;
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

    public void initBarModel() {
        this.barModel = new BarChartModel();
        List<InfoPaciente> listInfoP = fetchLastTenUpdatesInfoPaciente();
        BarChartModel model = new BarChartModel();

        ChartSeries serie = new ChartSeries();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (InfoPaciente info : listInfoP) {
            if (this.tipoGrafico == 0) {
                serie.set(sdf.format(info.getDataMarcacao()), info.getPeso());
            } else {
                serie.set(sdf.format(info.getDataMarcacao()), info.getAltura());
            }
        }

        model.addSeries(serie);

        this.barModel = model;
        this.barModel.setAnimate(true);
        this.barModel.setBarWidth(45);
        this.barModel.setMouseoverHighlight(true);
        this.barModel.setShadow(true);
        this.barModel.setShowPointLabels(true);
        this.barModel.setTitle("Meu Desempenho");
    }

    public void atualizarMeusDados() {
        EntityManager em = JPAUtil.getEntityManager();
        InfoPaciente info = new InfoPaciente();
        info.setAltura(this.altura);
        info.setPeso(this.peso);
        info.setDataMarcacao(new Date());
        em.getTransaction().begin();
        em.persist(info);
        em.getTransaction().commit();
        if (null == this.usuario.getInfoPaciente() || this.usuario.getInfoPaciente().isEmpty()) {
            this.usuario.setInfoPaciente(new ArrayList<>());
        }
        this.usuario.getInfoPaciente().add(info);
        em.getTransaction().begin();
        em.merge(this.usuario);
        em.getTransaction().commit();
        this.usuario = em.find(Usuario.class, this.usuario.getId());
        em.close();
        this.peso = 0.0;
        this.altura = 0.0;
        initBarModel();
        RequestContext.getCurrentInstance().execute("PF('atualizarDados').hide()");
    }

    public Long qtdeNutricionistasAprovar() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT COUNT(i.id) FROM Usuario i "
                + "WHERE i.liberado = :liberado AND i.administrador = :adm");
        query.setParameter("liberado", Boolean.FALSE);
        query.setParameter("adm", Boolean.TRUE);
        try {
            return (Long) query.getResultList().get(0);
        } catch (IndexOutOfBoundsException e) {
            return 0L;
        } finally {
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

    private void fetchLastUpdateDados() {
        EntityManager em = JPAUtil.getEntityManager();
        InfoPaciente output = this.usuario.getInfoPaciente().get(0);
        for (int i = 1; i < this.usuario.getInfoPaciente().size(); i++) {
            if (output.getDataMarcacao().after(this.usuario.getInfoPaciente().get(i).getDataMarcacao())) {
                output = this.usuario.getInfoPaciente().get(i);
            }
        }
        em.close();
        this.altura = output.getAltura();
        this.peso = output.getPeso();
    }

    private List<InfoPaciente> fetchLastTenUpdatesInfoPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        List<InfoPaciente> outputList = new ArrayList<>();
        List<InfoPaciente> currentList = this.usuario.getInfoPaciente();
        Collections.sort(currentList, new CustomComparator());
        //Collections.reverse(currentList);

        for (int i = 0; i < 10; i++) {
            try {
                outputList.add(currentList.get(i));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        em.close();
        return outputList;
    }

    public void openAtualizarDados() {
        fetchLastUpdateDados();
        RequestContext.getCurrentInstance().execute("PF('atualizarDados').show()");
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

    public String isUsuarioAllowedOnExercicio() {
        if (this.usuario.getAdministrador()) {
            return "/paciente/exercicio.xhtml";
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Oops!", "Você não pode cadastrar seu próprio exercício, contate seu nutricionista."));
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
                initBarModel();
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
    
    public Usuario retornaUsuarioById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, id);
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

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public String getUsuarioSelect() {
        return usuarioSelect;
    }

    public void setUsuarioSelect(String usuarioSelect) {
        this.usuarioSelect = usuarioSelect;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Boolean getChangeOnPeso() {
        return changeOnPeso;
    }

    public void setChangeOnPeso(Boolean changeOnPeso) {
        this.changeOnPeso = changeOnPeso;
    }

    public Boolean getChangeOnAltura() {
        return changeOnAltura;
    }

    public void setChangeOnAltura(Boolean changeOnAltura) {
        this.changeOnAltura = changeOnAltura;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public Integer getTipoGrafico() {
        return tipoGrafico;
    }

    public void setTipoGrafico(Integer tipoGrafico) {
        this.tipoGrafico = tipoGrafico;
    }
}

class CustomComparator implements Comparator<InfoPaciente> {

    public int compare(InfoPaciente o1, InfoPaciente o2) {
        return o1.getDataMarcacao().compareTo(o2.getDataMarcacao());
    }
}

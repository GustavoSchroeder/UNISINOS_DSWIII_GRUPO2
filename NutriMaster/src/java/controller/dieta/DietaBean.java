package controller.dieta;

import controller.userControl.UsuarioBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import pojo.paciente.AlimentoQuantidade;
import pojo.paciente.CalendarioAlimentacao;
import pojo.paciente.Dieta;
import pojo.usuario.Usuario;
import util.JPAUtil;

/**
 *
 * @author gustavolazarottoschroeder
 */
@ManagedBean
@ViewScoped
public class DietaBean implements Serializable {

    @ManagedProperty("#{calendarioAlimentacaoBean}")
    private CalendarioAlimentacaoBean calendarioAlimentacaoBean;
    @ManagedProperty("#{usuarioBean}")
    private UsuarioBean usuarioBean;
    private Dieta dieta;
    private Usuario paciente;
    private String pacienteStr;
    private List<Dieta> listaDietas;
    private Dieta dietaSelect;
    private List<String> diasSemana;
    private Date vcto;

    public DietaBean() {
        this.dieta = null;
        this.paciente = new Usuario();
        this.pacienteStr = "";
        this.listaDietas = new ArrayList<>();
        this.dietaSelect = new Dieta();
        this.vcto = new Date();
    }

    @PostConstruct
    public void init() {
        if (this.usuarioBean.getDisplay()) {
            retornaMinhasDietas();
            this.usuarioBean.setDisplay(Boolean.FALSE);
        }
        this.diasSemana = this.calendarioAlimentacaoBean.retornaDiasSemana();
    }

    public void abrirDietaDetalhes(Dieta dieta) {
        this.dietaSelect = dieta;
        RequestContext.getCurrentInstance().execute("PF('dietaDetalhes').show();");
    }

    public List<AlimentoQuantidade> retornaAlimentos(String diaSemana) {
        List<AlimentoQuantidade> calendarioAux = new ArrayList<>();
        try {
            for (CalendarioAlimentacao calendario : this.dietaSelect.getCalendariAlimentacao()) {
                if (calendario.getDiaSemana().equalsIgnoreCase(diaSemana)) {
                    calendarioAux.addAll(calendario.getAlimento());
                }
            }
        } catch (NullPointerException e) {
        }

        return calendarioAux;
    }

    public List<AlimentoQuantidade> retornaAlimentosFO(String diaSemana) {
        try {
            for (CalendarioAlimentacao calendario : this.dietaSelect.getCalendariAlimentacao()) {
                if (calendario.getDiaSemana().equalsIgnoreCase(diaSemana)) {
                    return calendario.getAlimento();
                }
            }
        } catch (NullPointerException e) {
        }

        return new ArrayList<>();
    }

    public String retornaPaciente(Dieta dieta) {
        try {
            for (CalendarioAlimentacao calendarioAlimentacao : dieta.getCalendariAlimentacao()) {
                return calendarioAlimentacao.getUsuario().getNome();
            }
        } catch (NullPointerException e) {
        }
        return "-";
    }

    public String isUsuarioAllowedOnDieta() {
        if (this.usuarioBean.getUsuario().getAdministrador()) {
            return "/paciente/dieta.xhtml";
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Oops!",
                "Você não pode criar sua própria dieta, contate seu nutricionista."));
        return null;
    }

    public void adicionarDietaLista() {
        Usuario u = this.usuarioBean.retornaUsuarioById(this.pacienteStr);
        this.calendarioAlimentacaoBean.cadastrarCalendarioAlimentacao(u, diasCadastrados());

        if (null == this.dieta) {
            this.dieta = new Dieta();

            for (CalendarioAlimentacao cal : this.calendarioAlimentacaoBean.retornaCalendarios(u)) {
                if (adicionarDietaLista(cal)) {
                    if (null == this.dieta.getCalendariAlimentacao()) {
                        this.dieta.setCalendariAlimentacao(new ArrayList<>());
                    }
                    this.dieta.getCalendariAlimentacao().add(cal);
                }
            }

            //this.dieta.setCalendariAlimentacao(this.calendarioAlimentacaoBean.retornaCalendarios(u));
            this.dieta.setNutricionistaResponsavel(this.usuarioBean.getUsuario());
            this.dieta.setDataDieta(new Date());
            this.dieta = (this.dieta);
        } else {
            for (CalendarioAlimentacao cal : this.calendarioAlimentacaoBean.retornaCalendarios(u)) {
                if (adicionarDietaLista(cal)) {
                    if (null == this.dieta.getCalendariAlimentacao()) {
                        this.dieta.setCalendariAlimentacao(new ArrayList<>());
                    }
                    this.dieta.getCalendariAlimentacao().add(cal);
                }
            }
        }
    }

    private Boolean adicionarDietaLista(CalendarioAlimentacao cal) {
        return (!isDiaCadastrado(cal.getDiaSemana()));
    }

    public void buscarDieta(String dia) {
        for (CalendarioAlimentacao calendarioAlimentacao : this.dieta.getCalendariAlimentacao()) {
            if (calendarioAlimentacao.getDiaSemana().equalsIgnoreCase(dia)) {
                this.calendarioAlimentacaoBean.setCalendariosCadastrados(calendarioAlimentacao);
                return;
            }
        }
    }

    public void atribuirPaciente() {
        EntityManager em = JPAUtil.getEntityManager();
        this.paciente = em.find(Usuario.class, this.pacienteStr.substring(0, this.pacienteStr.indexOf(';')));
        em.close();
    }

    public Usuario retornarUsuario(String usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, usuario.substring(0, usuario.indexOf(';')));
        } catch (IndexOutOfBoundsException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Long retornarIdUsuario(String usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Long.parseLong(usuario.substring(0, usuario.indexOf(';')));
        } catch (IndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void cadastrarDietas() {
        if (!isAllDiasCadastrados()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Oooops!", "Você não cadastrou dieta para todos os dias!"));
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        em.merge(this.dieta);

        em.getTransaction().commit();
        em.close();
        this.dieta = null;
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Sucesso!", "Dieta cadastrada!"));
    }

    public Boolean isDiaCadastrado(String dia) {
        try {
            for (CalendarioAlimentacao calendarioAlimentacao : this.dieta.getCalendariAlimentacao()) {
                if (calendarioAlimentacao.getDiaSemana().equalsIgnoreCase(dia)) {
                    return Boolean.TRUE;
                }
            }
        } catch (NullPointerException e) {
        }

        return Boolean.FALSE;
    }

    public List<String> diasCadastrados() {
        List<String> dias = new ArrayList<>();
        try {
            for (CalendarioAlimentacao calendarioAlimentacao : this.dieta.getCalendariAlimentacao()) {
                dias.add(calendarioAlimentacao.getDiaSemana());
            }
        } catch (NullPointerException e) {
        }

        return dias;
    }

    public Boolean isAllDiasCadastrados() {
        Integer cont = 0;
        for (String dia : this.calendarioAlimentacaoBean.retornaDiasSemana()) {
            if (isDiaCadastrado(dia)) {
                cont++;
            }
        }
        return cont == 7;
    }

    public void retornaMinhasDietas() {
        this.listaDietas.clear();
        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Dieta i");
        List<Dieta> auxList = query.getResultList();

        for (Dieta auxDieta : auxList) {
            for (CalendarioAlimentacao calendarioAlimentacao : auxDieta.getCalendariAlimentacao()) {
                if (Objects.equals(calendarioAlimentacao.getUsuario().getId(), this.usuarioBean.getUsuario().getId())) {
                    this.listaDietas.add(auxDieta);
                    break;
                }
            }
        }

        em.close();
    }

    public String abrirRelatorioMinhasDietas() {
        if (this.usuarioBean.getUsuario().getAdministrador()) {
            this.usuarioBean.setDisplay(Boolean.TRUE);
        }
        return "/paciente/relatorioMinhasDietas.xhtml?faces-redirect=true";
    }

    public void retornaMinhasDietasByPaciente() {
        this.listaDietas.clear();
        Set<Dieta> dietaSet = new HashSet<>();

        EntityManager em = JPAUtil.getEntityManager();
        Query query = em.createQuery("SELECT i FROM Dieta i");
        List<Dieta> auxList = query.getResultList();

        for (Dieta d : auxList) {
            for (CalendarioAlimentacao calendarioAlimentacao : d.getCalendariAlimentacao()) {
                Long id = retornarIdUsuario(this.usuarioBean.getUsuarioSelect());
                if (null != id) {
                    if (Objects.equals(calendarioAlimentacao.getUsuario().getId(), id)) {
                        dietaSet.add(d);
                        break;
                    }
                }
            }
        }

        for (Dieta diet : dietaSet) {
            this.listaDietas.add(diet);
        }

        em.close();
    }

    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    public Dieta getDieta() {
        return dieta;
    }

    public void setDieta(Dieta dieta) {
        this.dieta = dieta;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }

    public CalendarioAlimentacaoBean getCalendarioAlimentacaoBean() {
        return calendarioAlimentacaoBean;
    }

    public void setCalendarioAlimentacaoBean(CalendarioAlimentacaoBean calendarioAlimentacaoBean) {
        this.calendarioAlimentacaoBean = calendarioAlimentacaoBean;
    }

    public String getPacienteStr() {
        return pacienteStr;
    }

    public void setPacienteStr(String pacienteStr) {
        this.pacienteStr = pacienteStr;
    }

    public List<Dieta> getListaDietas() {
        return listaDietas;
    }

    public void setListaDietas(List<Dieta> listaDietas) {
        this.listaDietas = listaDietas;
    }

    public Dieta getDietaSelect() {
        return dietaSelect;
    }

    public void setDietaSelect(Dieta dietaSelect) {
        this.dietaSelect = dietaSelect;
    }

    public List<String> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(List<String> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public Date getVcto() {
        return vcto;
    }

    public void setVcto(Date vcto) {
        this.vcto = vcto;
    }
}

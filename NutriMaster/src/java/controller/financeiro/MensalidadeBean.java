package controller.financeiro;

import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.Beneficiario;
import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.Datas;
import br.com.caelum.stella.boleto.Endereco;
import br.com.caelum.stella.boleto.Pagador;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.caelum.stella.boleto.bancos.BancoDoBrasil;
import controller.userControl.UsuarioBean;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
    private StreamedContent file;

    public MensalidadeBean() {
        this.paciente = new Usuario();
        this.pacienteStr = "";
        this.mensalidades = new ArrayList<>();
    }

    public String retornaSituacao(Mensalidade m) {
        if (m.isPago()) {
            return "Pago";
        } else {
            return "Em aberto";
        }
    }

    public void retornaListaMensalidades() {
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
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Mensalidade> retornaMensalidadesMes() {
        EntityManager em = JPAUtil.getEntityManager();
        Query query;
        query = em.createQuery("SELECT i FROM Mensalidade i WHERE FUNC('MONTH',i.lancamento) = :mesLancamento");
        query.setParameter("mesLancamento", Calendar.getInstance().get(Calendar.MONTH));
        try {
            return query.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }

    }

    public void lancaMensalidade() {
        // Se possui mensalidade no mês
        if (!retornaMensalidadesMes().isEmpty()) {
            return;
        }

        mensalidade = new Mensalidade();
        mensalidade.setPaciente(usuarioBean.getUsuario());
        mensalidade.setPago(false);
        mensalidade.setValor(200);
        mensalidade.setLancamento(Calendar.getInstance().getTime());
        Calendar vencimento = Calendar.getInstance();
        vencimento.add(Calendar.DAY_OF_MONTH, 15);
        mensalidade.setVencimento(vencimento.getTime());
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(mensalidade);
        em.getTransaction().commit();
        em.close();
    }

    public void pagarMensalidade(Long id) throws FileNotFoundException {
        this.mensalidade = this.buscaPorId(id);
        this.mensalidade.setPago(true);
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(mensalidade);
        em.getTransaction().commit();
        em.close();
        // Gera o boleto
        this.geraBoleto();
    }

    public void geraBoleto() throws FileNotFoundException {
        Calendar proc = Calendar.getInstance();
        proc.setTime(this.mensalidade.getLancamento());
        Calendar venc = Calendar.getInstance();
        venc.setTime(this.mensalidade.getVencimento());

        Datas datas = Datas.novasDatas()
                .comDocumento(proc.get(Calendar.DAY_OF_MONTH), proc.get(Calendar.MONTH), proc.get(Calendar.YEAR))
                .comProcessamento(proc.get(Calendar.DAY_OF_MONTH), proc.get(Calendar.MONTH), proc.get(Calendar.YEAR))
                .comVencimento(venc.get(Calendar.DAY_OF_MONTH), venc.get(Calendar.MONTH), venc.get(Calendar.YEAR));

        Endereco enderecoBeneficiario = Endereco.novoEndereco()
                .comLogradouro("Av. Unisinos, 950")
                .comBairro("Cristo Rei")
                .comCep("93020-190")
                .comCidade("São Leopoldo")
                .comUf("RS");

        //Quem emite o file
        Beneficiario beneficiario = Beneficiario.novoBeneficiario()
                .comNomeBeneficiario("NutriMaster")
                .comAgencia("1234").comDigitoAgencia("5")
                .comCodigoBeneficiario("123456")
                .comDigitoCodigoBeneficiario("7")
                .comNumeroConvenio("1207113")
                .comCarteira("18")
                .comEndereco(enderecoBeneficiario)
                .comNossoNumero("9000206");

        Endereco enderecoPagador = Endereco.novoEndereco()
                .comLogradouro(this.usuarioBean.getUsuario().getEndereco().getEndereco())
                .comBairro(this.usuarioBean.getUsuario().getEndereco().getBairro())
                .comCep(this.usuarioBean.getUsuario().getEndereco().getCep())
                .comCidade(this.usuarioBean.getUsuario().getEndereco().getCidade())
                .comUf(this.usuarioBean.getUsuario().getEndereco().getUf());

        //Quem paga o file
        Pagador pagador = Pagador.novoPagador()
                .comNome(this.usuarioBean.getUsuario().getNome())
                .comDocumento("111.222.333-12")
                .comEndereco(enderecoPagador);

        Banco banco = new BancoDoBrasil();

        Boleto boleto = Boleto.novoBoleto()
                .comBanco(banco)
                .comDatas(datas)
                .comBeneficiario(beneficiario)
                .comPagador(pagador)
                .comValorBoleto(this.mensalidade.getValor())
                .comNumeroDoDocumento("1234")
                .comInstrucoes("Realize o pagamento o mais rápido possível para evitar problemas com sua fatura!")
                .comLocaisDePagamento("Agência do Banco do Brasil");

        GeradorDeBoleto gerador = new GeradorDeBoleto(boleto);
        String caminhoWebInf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/");
        String caminhoBoleto = caminhoWebInf + "\\" + this.usuarioBean.getUsuario().getId() + "_" + proc.getTimeInMillis() + ".pdf";
        try {
            FileOutputStream out = new FileOutputStream(caminhoBoleto);
            gerador.geraPDF(out);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(MensalidadeBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        InputStream stream = new FileInputStream(caminhoBoleto);
        file = new DefaultStreamedContent(stream, "application/pdf", "boleto.pdf");
        RequestContext.getCurrentInstance().execute("PF('download').show()");
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

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

}

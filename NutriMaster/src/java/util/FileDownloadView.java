package util;

import controller.financeiro.MensalidadeBean;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
public class FileDownloadView {

    private StreamedContent file;

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public StreamedContent getFile() throws FileNotFoundException {

        String caminhoWebInf = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/");
        InputStream stream = new FileInputStream(caminhoWebInf + "boleto.pdf"); //Caminho onde está salvo o arquivo.
        file = new DefaultStreamedContent(stream, "application/pdf", "edital.pdf");

        return file;
    }
}

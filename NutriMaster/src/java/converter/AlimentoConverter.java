package converter;

import controller.dieta.CalendarioAlimentacaoBean;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author gustavolazarottoschroeder
 */
@FacesConverter("alimentoConverter")
public class AlimentoConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string != null && string.trim().length() > 0) {
            try {
                CalendarioAlimentacaoBean usuarioBean = (CalendarioAlimentacaoBean) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, "calendarioAlimentacaoBean");
                return usuarioBean.retornaAlimento(Long.parseLong(string));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Associando não existente."));
            }

        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return o + "";
    }
}

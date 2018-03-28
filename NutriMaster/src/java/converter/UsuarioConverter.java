/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import controller.userControl.UsuarioBean;
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
@FacesConverter("usuarioConverter")
public class UsuarioConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string != null && string.trim().length() > 0) {
            try {
                UsuarioBean usuarioBean = (UsuarioBean) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, "usuarioBean");
                return usuarioBean.buscaPorId(Long.parseLong(string));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Associando não existente."));
            }

        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
//        Usuario pesquisa;
//        pesquisa = (Usuario) o;
//        return pesquisa.getNome();
        return o + "";
    }
}

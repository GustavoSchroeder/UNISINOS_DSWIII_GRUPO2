/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.userControl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
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
    private Integer aux;
    
    public UsuarioBean(){
        this.usuario = new Usuario();
        this.aux = 0;
        EntityManager em = JPAUtil.getEntityManager();   
    }
    
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getAux() {
        return aux;
    }

    public void setAux(Integer aux) {
        this.aux = aux;
    }
    
}

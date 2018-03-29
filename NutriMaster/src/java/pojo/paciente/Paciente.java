/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo.paciente;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author renisteffenon
 */

@Entity
public class Paciente {
    
    private Long id;
    private String nome;
    private Date dataNascimento;
    private float peso;
    private String documento;
    private String preferenciaPagamento;
    private String endereco;
    private Dieta dieta;
    private Consulta consultas;
    // Atributo Perfil Social
    private List<Parcelas> parcelas;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getPreferenciaPagamento() {
        return preferenciaPagamento;
    }

    public void setPreferenciaPagamento(String preferenciaPagamento) {
        this.preferenciaPagamento = preferenciaPagamento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Dieta getDieta() {
        return dieta;
    }

    public void setDieta(Dieta dieta) {
        this.dieta = dieta;
    }

    public List<Parcelas> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<Parcelas> parcelas) {
        this.parcelas = parcelas;
    }

    public Consulta getConsultas() {
        return consultas;
    }

    public void setConsultas(Consulta consultas) {
        this.consultas = consultas;
    }
    
}

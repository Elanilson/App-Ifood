package com.elanilsondejesus.com.ifood.model;

import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Empresa  implements Serializable {
    private String idUsuario;
    private String urlimagem;
    private String nome;
    private String tempo;
    private String categoria;
    private Double taxaDeEntrega;

    public Empresa() {
    }

    public void salvar(){
        DatabaseReference firabaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaREf  = firabaseRef.child("empresas")
                .child(getIdUsuario());
        empresaREf.setValue(this);

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlimagem() {
        return urlimagem;
    }

    public void setUrlimagem(String urlimagem) {
        this.urlimagem = urlimagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getTaxaDeEntrega() {
        return taxaDeEntrega;
    }

    public void setTaxaDeEntrega(Double taxaDeEntrega) {
        this.taxaDeEntrega = taxaDeEntrega;
    }
}

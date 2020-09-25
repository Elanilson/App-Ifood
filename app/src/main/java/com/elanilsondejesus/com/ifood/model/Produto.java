package com.elanilsondejesus.com.ifood.model;

import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Produto {

    private String idUsuario;
    private String idUproduto;
    private String nome;
    private String descricao;
    private Double preco;

    public Produto() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos");
        setIdUproduto(produtoRef.push().getKey());
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child( getIdUsuario() )
                .child(getIdUproduto());
        produtoRef.setValue(this);

    }
    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child( getIdUsuario() )
                .child(getIdUproduto());
        produtoRef.removeValue();
    }

    public String getIdUproduto() {
        return idUproduto;
    }

    public void setIdUproduto(String idUproduto) {
        this.idUproduto = idUproduto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}

package com.elanilsondejesus.com.ifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elanilsondejesus.com.ifood.R;
import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.elanilsondejesus.com.ifood.helper.UsuarioFirebase;
import com.elanilsondejesus.com.ifood.model.Empresa;
import com.elanilsondejesus.com.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {
    private EditText edtiNomeProduto,edtiDescicao,edtiPreco;
    private FirebaseAuth autenticacao;
    private String usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioLogado = UsuarioFirebase.getIdUsuario();
configuracoesIniciais();
        //configurar toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void validarDadosProduto(View view){
        String nome = edtiNomeProduto.getText().toString();
        String descricao = edtiDescicao.getText().toString();
        String preco = edtiPreco.getText().toString();


        if (! nome.isEmpty()){
            if (! descricao.isEmpty()){
                if (! preco.isEmpty()){
                    Produto produto = new Produto();
                    produto.setIdUsuario(usuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();
                    finish();
                    exibirMEnsagem("produto salvo com sucesso");
                }else{
                    exibirMEnsagem("Digite um preço para o produto!");
                }
            }else{
                exibirMEnsagem("Digite uma descrição para o produto!");
            }
        }else{
            exibirMEnsagem("Digite um nome para o produto!");
        }
    }

    public void exibirMEnsagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();

    }

    private void configuracoesIniciais(){
        edtiNomeProduto = findViewById(R.id.editProdutoNome);
        edtiDescicao = findViewById(R.id.editrodutoDescricao);
        edtiPreco = findViewById(R.id.ediProdutoPreco);

    }
}
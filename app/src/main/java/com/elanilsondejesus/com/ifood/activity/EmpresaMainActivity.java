package com.elanilsondejesus.com.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.elanilsondejesus.com.ifood.R;
import com.elanilsondejesus.com.ifood.adapter.AdapterProduto;
import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.elanilsondejesus.com.ifood.helper.UsuarioFirebase;
import com.elanilsondejesus.com.ifood.listener.RecyclerItemClickListener;
import com.elanilsondejesus.com.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaMainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    private RecyclerView recyclerView;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firabaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_main);

        iniciarComponentes();
        //configurar toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood - empresa");
        setSupportActionBar(toolbar);

        autenticacao= ConfiguracaoFirebase.getFirebaseAutenticacao();
        firabaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

                // configurando recycleview
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos,this);
        recyclerView.setAdapter(adapterProduto);

        recuperarProdutos();

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(
                this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover();

                Toast.makeText(EmpresaMainActivity.this, "Produto excluido com sucesso", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }
        ));
}
    public void recuperarProdutos(){
        final DatabaseReference produtosRef = firabaseRef
                .child("produtos")
                .child(idUsuarioLogado);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds:  dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));

                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                    deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                    abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto:
                    abrirNovoProduto();
                break;
            case R.id.menuPedidos:
                abrirPedidos();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void abrirPedidos(){
        startActivity( new Intent(EmpresaMainActivity.this,PedidosActivity.class));
    }
    private void abrirConfiguracoes(){
        startActivity( new Intent(EmpresaMainActivity.this,ConfiguracoesEmpresaActivity.class));
    }
    private void abrirNovoProduto(){
        startActivity( new Intent(EmpresaMainActivity.this,NovoProdutoEmpresaActivity.class));
    }
    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
                finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void iniciarComponentes(){
        recyclerView = findViewById(R.id.recyclerviewProdutos);
    }
}
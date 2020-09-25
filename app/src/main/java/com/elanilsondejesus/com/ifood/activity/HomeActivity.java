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
import android.widget.Adapter;
import android.widget.AdapterView;

import com.elanilsondejesus.com.ifood.R;
import com.elanilsondejesus.com.ifood.adapter.AdapterEmpresa;
import com.elanilsondejesus.com.ifood.adapter.AdapterProduto;
import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.elanilsondejesus.com.ifood.listener.RecyclerItemClickListener;
import com.elanilsondejesus.com.ifood.model.Empresa;
import com.elanilsondejesus.com.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private MaterialSearchView  searchView;

    private RecyclerView recyclerView;
    private AdapterEmpresa adapterEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firabaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        autenticacao= ConfiguracaoFirebase.getFirebaseAutenticacao();
        firabaseRef = ConfiguracaoFirebase.getFirebase();
    incializarComponentes();
        //configurar toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood ");
        setSupportActionBar(toolbar);



        // configurando recycleview
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerView.setAdapter(adapterEmpresa);

        recuperarEmpresas();

        searchView.setHint("Pesquisar restaurantes");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas( newText);
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Empresa empresaSelecionada =  empresas.get(position);
                        Intent intent = new Intent(HomeActivity.this,CardapioActivity.class);
                            intent.putExtra("empresa",empresaSelecionada);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
                )
        );
    }
    public void pesquisarEmpresas(String texto){
        DatabaseReference empresasREf = firabaseRef.child("empresas");
        Query query = empresasREf.orderByChild("nome").startAt(texto).endAt(texto +"\uf8ff");
    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            empresas.clear();

            for (DataSnapshot ds:  dataSnapshot.getChildren()){
                empresas.add(ds.getValue(Empresa.class));

            }
            adapterEmpresa.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });

    }

    public void recuperarEmpresas(){
        DatabaseReference empresaRef = firabaseRef
                .child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresas.clear();

                for (DataSnapshot ds:  dataSnapshot.getChildren()){
                    empresas.add(ds.getValue(Empresa.class));

                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario,menu);

        // configurar o botton pesquisa
        MenuItem item  = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
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

        }

        return super.onOptionsItemSelected(item);
    }
    private void abrirConfiguracoes(){
        startActivity( new Intent(HomeActivity.this,ConfiguracoesUsuarioActivity.class));
    }
    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void incializarComponentes(){
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recyclerviewEmpresas);
    }

}
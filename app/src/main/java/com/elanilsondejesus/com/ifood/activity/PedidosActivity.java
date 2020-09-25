package com.elanilsondejesus.com.ifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.elanilsondejesus.com.ifood.R;
import com.elanilsondejesus.com.ifood.adapter.AdapterPedido;
import com.elanilsondejesus.com.ifood.adapter.AdapterProduto;
import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.elanilsondejesus.com.ifood.helper.UsuarioFirebase;
import com.elanilsondejesus.com.ifood.listener.RecyclerItemClickListener;
import com.elanilsondejesus.com.ifood.model.Pedido;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firabaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
    inicializarComponentes();

    firabaseRef = ConfiguracaoFirebase.getFirebase();
    idEmpresa = UsuarioFirebase.getIdUsuario();
        //configurar toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        // configurando recycleview
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerView.setAdapter(adapterPedido);
            recuperarPedidos();



            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                            Pedido pedido = pedidos.get(position);
                            pedido.setStatus("finalizado");
                            pedido.atualizarStatus();

                        }

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        }
                    }
                    )
            );

    }
    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable( false )
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firabaseRef
                .child("pedidos")
                .child(idEmpresa);

        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pedidos.clear();
                if( dataSnapshot.getValue() != null ){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void inicializarComponentes(){
        recyclerView = findViewById(R.id.recyclerviewPedidos);
    }
}
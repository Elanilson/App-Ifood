package com.elanilsondejesus.com.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.elanilsondejesus.com.ifood.R;
import com.elanilsondejesus.com.ifood.adapter.AdapterProduto;
import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.elanilsondejesus.com.ifood.helper.UsuarioFirebase;
import com.elanilsondejesus.com.ifood.listener.RecyclerItemClickListener;
import com.elanilsondejesus.com.ifood.model.Empresa;
import com.elanilsondejesus.com.ifood.model.ItemPedido;
import com.elanilsondejesus.com.ifood.model.Pedido;
import com.elanilsondejesus.com.ifood.model.Produto;
import com.elanilsondejesus.com.ifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView nomeEmpresaCardapio;
    private ImageView imagemEmpresaCardapio;
    private Empresa empresaSelecionada;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firabaseRef;
   private String idEmpresa;
   private AlertDialog dialog;
   private String idUsuarioLogado;
   private Usuario usuario;
   private Pedido pedidoRecuperado;
   private  int quantidadeItemCarrinho;
   private Double totalCarrinho;
   private TextView textViewCarinhoQuantidade, textViewCarrinhoTotal;
   private  int metodoDePagamento;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);
        firabaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

inicializarComponentes();
        //configurar toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //~recuperar empresa selecionada
        Bundle dados = getIntent().getExtras();
        if(dados != null){
            empresaSelecionada = (Empresa) dados.getSerializable("empresa");
            idEmpresa = empresaSelecionada.getIdUsuario();
            nomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            String url = empresaSelecionada.getUrlimagem();
            Picasso.get().load(url).into(imagemEmpresaCardapio);

        }



        // configurando recycleview
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos,this);
        recyclerView.setAdapter(adapterProduto);

        recuperarProdutos();

        recuperarDadosUsuarios();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                confirmarQuantidade(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }
        ));
    }

    public void confirmarQuantidade(final int posicao){

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Quantidade");
    builder.setMessage("digite a quantidade");
        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");
        builder.setView(editQuantidade);
    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            String quantidade =  editQuantidade.getText().toString();
            Produto produtoSelecionado = produtos.get(posicao);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdUproduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));
            itensCarrinho.add(itemPedido );
            if(pedidoRecuperado == null){
                pedidoRecuperado = new Pedido(idUsuarioLogado,idEmpresa);


            }
            pedidoRecuperado.setNome(usuario.getNome());
            pedidoRecuperado.setEndereco(usuario.getEndereco());
            pedidoRecuperado.setItens(itensCarrinho);
            pedidoRecuperado.salvar();

        }
    });
    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    });

    AlertDialog dialog = builder.create();
    dialog.show();
    }
    public void recuperarDadosUsuarios(){
        dialog = new SpotsDialog.Builder().setContext(this)
        .setMessage("Carregando dados")
        .setCancelable(false)
        .build();
        dialog.show();

        DatabaseReference  usuarioRef = firabaseRef
                .child("usuarios")
                .child(idUsuarioLogado);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Usuario.class);


                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

public void confirmarPedido(){
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Selecione Método de pagamento");
        CharSequence [] itens = new CharSequence[]{
                "Dinheiro","Máquina de cartão"
        };
         builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 metodoDePagamento = i;


             }

         });

    final EditText editTextObservacao = new EditText(this);
    editTextObservacao.setHint("Digite uma observação");

    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String  observacao = editTextObservacao.getText().toString();
            pedidoRecuperado.setMetodoPagamento(metodoDePagamento);
            pedidoRecuperado.setObservacao(observacao);
            pedidoRecuperado.setStatus("confirmado");
            pedidoRecuperado.confirmar();
            pedidoRecuperado.remover();
            pedidoRecuperado=null;
        }
    });

    builder.setView(editTextObservacao);
    AlertDialog dialog = builder.create();
    dialog.show();
}
    private void recuperarPedido() {

        DatabaseReference pedidoRef = firabaseRef
                .child("pedidos_usuario")
                .child( idEmpresa )
                .child( idUsuarioLogado );

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                quantidadeItemCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if(dataSnapshot.getValue() != null){

                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();


                    for(ItemPedido itemPedido: itensCarrinho){

                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        quantidadeItemCarrinho += qtde;

                    }

                }

                DecimalFormat df = new DecimalFormat("0.00");

                textViewCarinhoQuantidade.setText( "qtd: " + String.valueOf(quantidadeItemCarrinho) );
                textViewCarrinhoTotal.setText("R$ " + df.format( totalCarrinho ) );

                dialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void recuperarProdutos(){
        final DatabaseReference produtosRef = firabaseRef
                .child("produtos")
                .child(idEmpresa);
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
    public void inicializarComponentes(){
        nomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
        imagemEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        recyclerView = findViewById(R.id.recyclerviewCArdapio);
        textViewCarinhoQuantidade = findViewById(R.id.textViewTotalCarinho);
        textViewCarrinhoTotal = findViewById(R.id.textViewValorTotal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuPedido:
                    confirmarPedido();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
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
import com.elanilsondejesus.com.ifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {
    private EditText edtiNome,edtiEndereco;
    private DatabaseReference firebaseRef;
    private String iDusuarioLogado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);
        iDusuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        configuracoesIniciais();
        //configurar toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recuperarDadosUsuario();
    }
    public void recuperarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(iDusuarioLogado);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    edtiNome.setText(usuario.getNome());
                    edtiEndereco.setText(usuario.getEndereco());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validarDadosUsuario(View view) {
        String nome = edtiNome.getText().toString();
        String endereco = edtiEndereco.getText().toString();

        if (!nome.isEmpty()) {
            if (!endereco.isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(iDusuarioLogado);
                usuario.setNome(nome);
                usuario.setEndereco(endereco);
                usuario.salvar();
                exibirMEnsagem("Dados Atualizados com sucesso!");
                finish();
            } else {
                exibirMEnsagem("Preencha o campo Endereço!");
            }
        }else{
            exibirMEnsagem("Preencha o campo nome!");

        }
    }
    private void configuracoesIniciais(){
        edtiNome = findViewById(R.id.editUsuarioNome);
        edtiEndereco = findViewById(R.id.editEnderecoUsuario);

    }
    public void exibirMEnsagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();

    }
}
package com.elanilsondejesus.com.ifood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.elanilsondejesus.com.ifood.R;
import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.elanilsondejesus.com.ifood.helper.UsuarioFirebase;
import com.elanilsondejesus.com.ifood.model.Empresa;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {
    private EditText edtiNomeEmpresa,edtiCategoria,edtiTempoEntrega,edtiTaxa;
    private CircleImageView perfilEmpresa;
    private static final  int SELECAO_GALERIA = 200;
    private String usuarioLogado;
    private String urlImagemSelecionada ="";

    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);
        configuracoesIniciais();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuarioLogado = UsuarioFirebase.getIdUsuario();
        //configurar toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        perfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (i.resolveActivity(getPackageManager() )!= null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });

        recuperarDadosEmpresa();
    }
private void recuperarDadosEmpresa(){
        DatabaseReference empresaRef =firebaseRef
                .child("empresas")
                .child(usuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null ){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    edtiNomeEmpresa.setText(empresa.getNome());
                    edtiCategoria.setText(empresa.getCategoria());
                    edtiTaxa.setText(""+empresa.getTaxaDeEntrega().toString());
                    edtiTempoEntrega.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlimagem();
                    if(urlImagemSelecionada != " " && !urlImagemSelecionada.isEmpty() ){
                        Picasso.get().load(urlImagemSelecionada)
                                .into(perfilEmpresa);

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem =MediaStore.Images.Media.getBitmap(
                                getContentResolver(),localImagem
                        );
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if (imagem!=null){
                perfilEmpresa.setImageBitmap( imagem);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);

                byte[] dadosImagem = baos.toByteArray();

                StorageReference imagemRef = storageReference
                        .child("imagens")
                        .child("empresas")
                        .child(usuarioLogado+"jpeg");
                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ConfiguracoesEmpresaActivity.this, "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                        Toast.makeText(ConfiguracoesEmpresaActivity.this, "Sucesso ao fazer  upload da imagem", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    public void validarDadosEmpresa(View view){
        String nome = edtiNomeEmpresa.getText().toString();
        String categoria = edtiCategoria.getText().toString();
        String taxa = edtiTaxa.getText().toString();
        String tempo = edtiTempoEntrega.getText().toString();


        if (! nome.isEmpty()){
            if (! categoria.isEmpty()){
                if (! taxa.isEmpty()){
                    if (! tempo.isEmpty()){
                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(usuarioLogado);
                        empresa.setNome(nome);
                        empresa.setCategoria(categoria);
                        empresa.setTaxaDeEntrega(Double.parseDouble(taxa));
                        empresa.setTempo(tempo);
                        empresa.setUrlimagem(urlImagemSelecionada);
                        empresa.salvar();
                        finish();
                    }else{
                        exibirMEnsagem("Preencha o campo tempo de entrega!");
                    }
                }else{
                    exibirMEnsagem("Preencha o campo taxa!");
                }
            }else{
                exibirMEnsagem("Preencha o campo categoria!");
            }
        }else{
            exibirMEnsagem("Preencha o campo nome!");
        }
    }

public void exibirMEnsagem(String texto){
    Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();

}

    private void configuracoesIniciais(){
    edtiNomeEmpresa = findViewById(R.id.editEmpresaNome);
    edtiTempoEntrega = findViewById(R.id.editTEmpresaTempoEntrega);
    edtiCategoria = findViewById(R.id.editEmpresaCategoria);
    edtiTaxa = findViewById(R.id.editEmpresaTAxaEntrega);
    perfilEmpresa = findViewById(R.id.image_perfil_Empresa);
    }
}
package com.elanilsondejesus.com.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.elanilsondejesus.com.ifood.R;
import com.elanilsondejesus.com.ifood.helper.ConfiguracaoFirebase;
import com.elanilsondejesus.com.ifood.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoActivity extends AppCompatActivity {
    private Button botaoAcessar;
    private EditText campEmail, campSenha;
    private Switch tipoAcesso, tipoUsuario;
    private String email,senha;
    private LinearLayout linearLayoutTipoUsuario;



    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);
     //   getSupportActionBar().hide();
        inicializarComponentes();
        carregarAtribuicoes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        vereficarUsuarioLogado();
//        autenticacao.signOut();
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean verificar) {
                if (verificar){//empresa
                    linearLayoutTipoUsuario.setVisibility(View.VISIBLE);

                }else{//usuario
                    linearLayoutTipoUsuario.setVisibility(View.GONE);
                }
            }
        });
    }

    public void vereficarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            String  usuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(usuario);
        }
    }
    public void acessar(View view){
        verificarCampos();

    }
    public void verificarCampos(){
        carregarAtribuicoes();
        if (!email.isEmpty() ){
            if(!senha.isEmpty()  ){
                vereficarSwitch();
            }else{
                Toast.makeText(this, "Preencha o campo senha!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o campo e-mail!", Toast.LENGTH_SHORT).show();
        }
    }
    public void vereficarSwitch(){
        if (tipoAcesso.isChecked()){//cadastro
              cadastrarUsuario();
        }else{//login
            login();
        }
    }
    public void login(){
        autenticacao.signInWithEmailAndPassword(
                email,senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AutenticacaoActivity.this, "Logado com sucesso!", Toast.LENGTH_SHORT).show();
                           String usuario = task.getResult().getUser().getDisplayName();
                    abrirTelaPrincipal(usuario);
                }else{
                    Toast.makeText(AutenticacaoActivity.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void abrirTelaPrincipal(String usuario){
    if(usuario.equals("E")){//empresa
        startActivity(new Intent(AutenticacaoActivity.this,EmpresaMainActivity.class));

    }else{// usuario
        startActivity(new Intent(AutenticacaoActivity.this,HomeActivity.class));

    }
    }
    public void cadastrarUsuario(){
        autenticacao.createUserWithEmailAndPassword(
          email,senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AutenticacaoActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                  String tipodousuariocadastrado = getTipoUsuario();

                    UsuarioFirebase.atualizarTipoUsuario(tipodousuariocadastrado);
                  //  String usuario = task.getResult().getUser().getDisplayName();

                    abrirTelaPrincipal(tipodousuariocadastrado);
                }else{
                        erroExcecoes(task);
                }
            }
        });
    }
    private String getTipoUsuario(){
        return  tipoUsuario.isChecked() ? "E" : "U";
    }
    public void erroExcecoes(Task task){
        String erro ="";
        try{
            throw task.getException();
        }catch (FirebaseAuthWeakPasswordException e){
            erro="Digite uma senha mais forte!";
        }catch (FirebaseAuthInvalidCredentialsException e){
            erro ="Por favor, digite um e-mail válido!";
        }catch (FirebaseAuthUserCollisionException e){
            erro = "Está conta já foi Cadastrada";
        }catch (Exception e){
            erro = "Ao cadastrar usuário: "+e.getMessage();
            e.printStackTrace();
        }
        Toast.makeText(this, "Erro: "+erro, Toast.LENGTH_SHORT).show();
    }
    public void carregarAtribuicoes(){
         email = campEmail.getText().toString();
        senha = campSenha.getText().toString();
    }

    public void inicializarComponentes(){
        botaoAcessar = findViewById(R.id.buttonAcesso);
        campEmail = findViewById(R.id.editTextEmailLogin);
        campSenha = findViewById(R.id.editTextTextPasswordLogin);
        tipoAcesso = findViewById(R.id.switchTipoAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearLayoutTipoUsuario = findViewById(R.id.linearIdTipo);
    }
}
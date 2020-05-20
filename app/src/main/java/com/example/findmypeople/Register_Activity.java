package com.example.findmypeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Register_Activity extends AppCompatActivity implements View.OnClickListener{

    ProgressBar progressBar;
    EditText txtInputName, txtInputEmail, txtInputPassword;
    ImageView btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputName = findViewById(R.id.txtInputName);
        txtInputPassword = findViewById(R.id.txtInputPassword);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnCreateAccount).setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void registerUser(){
        String name = txtInputName.getText().toString().trim();
        String email = txtInputEmail.getText().toString().trim();
        String password = txtInputPassword.getText().toString().trim();

        if(email.isEmpty()){
            txtInputEmail.setError("E-mail é obrigatório!");
            txtInputEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            txtInputPassword.setError("Password é obrigatória!");
            txtInputPassword.requestFocus();
            return;
        }
        if(name.isEmpty()){
            txtInputName.setError("Nome é obrigatório!");
            txtInputName.requestFocus();
            return;
        }
        //Verifica se email é válido
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtInputEmail.setError("Por favor insira um e-mail válido :)");
            txtInputEmail.requestFocus();
            return;
        }
        //Verifica de password tem +6 caracteres
        if (password.length() < 6) {
            txtInputPassword.setError("Por favor insira uma password com mais de 6 caracteres.");
            txtInputPassword.requestFocus();
            return;
        }

        //BUG - Falta alterar a posiçao da progress bar (ta no topo esquerdo) e mudar a cor!
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "User criado com sucesso!", Toast.LENGTH_SHORT).show();
                }else{


                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"Já existe uma conta com estes dados.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCreateAccount:
                registerUser();
                break;
            case R.id.btnBack:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }
}

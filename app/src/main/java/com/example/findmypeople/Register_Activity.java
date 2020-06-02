package com.example.findmypeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Document;

import java.util.Collection;

public class Register_Activity extends AppCompatActivity implements View.OnClickListener {
    ProgressBar progressBar;
    EditText txtInputName, txtInputEmail, txtInputPassword;
    ImageView btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        db = FirebaseFirestore.getInstance();

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

    public void registerUser(){
         final String name = txtInputName.getText().toString().trim();
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
                    FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = firebaseuser.getUid();

                    addUserInfo(name);
                    Toast.makeText(getApplicationContext(), "User created!", Toast.LENGTH_SHORT).show();

                  /* Map<String, Object> map = new HashMap<>();
                   map.put("name", name);
                   map.put("UID", uid);
                   map.put("phone_number", "9122222");

                   db.collection("Users_master")
                           .add(map)
                           .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                               @Override
                               public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "onSuccess: task was successful");
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onSuccess: task aws NOT SUCCESSFULL");
                               }
                           });/*

                    //Adicionar nome & tipo de user à collection com o UID respetivo
                    //falta fazer if radiobuttonchild -> save to users_child



                   /* Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("phone_number", "915849999");

                    db.collection("Users_master").document(uid)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            }); */


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

    public void addUserInfo(final String name) {
        Log.d(TAG, "entrou userinfo");

        //String name = txtInputName.getText().toString().trim();
        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseuser.getUid();


        Toast.makeText(Register_Activity.this, "ToastText", Toast.LENGTH_SHORT).show();
        //CollectionReference dbUsers = db.collection("Users_master");

        final User user = new User(name, uid);
        Log.d(TAG, name);
        Log.d(TAG, uid);

        db.collection("Users_master").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Saved user fo firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failure", e);
                    }
                });


    }

}

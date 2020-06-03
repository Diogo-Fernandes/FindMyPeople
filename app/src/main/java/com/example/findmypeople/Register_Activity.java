package com.example.findmypeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
    EditText txtInputName, txtInputEmail, txtInputPassword, txtInputPhone;
    ImageView btnBack;
    AppCompatRadioButton radioBg, radioVip;
    RadioGroup radioGroup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private static final String TAG = "Register_Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        db = FirebaseFirestore.getInstance();

        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputName = findViewById(R.id.txtInputName);
        txtInputPassword = findViewById(R.id.txtInputPassword);
        txtInputPhone = findViewById(R.id.txtInputPhone);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar2);
        radioBg = findViewById(R.id.radioBg);
        radioVip = findViewById(R.id.radioVIP);
        radioGroup = findViewById(R.id.radioGroup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnCreateAccount).setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void registerUser(){
        String name = txtInputName.getText().toString().trim();
        String email = txtInputEmail.getText().toString().trim();
        String password = txtInputPassword.getText().toString().trim();
        String phone = txtInputPhone.getText().toString().trim();

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
        if (phone.length() != 9) {
            txtInputPhone.setError("Por favor insira um número de telemóvel válido.");
            txtInputPhone.requestFocus();
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
        //THIS NOT WORKING
        /*if (!radioVip.isSelected() && !radioBg.isSelected()) {
            Toast.makeText(getApplicationContext(), "Por favor seleciona um tipo de conta!", Toast.LENGTH_SHORT).show();
            radioGroup.requestFocus();
            return;
        } */

        //BUG - Falta alterar a posiçao da progress bar (ta no topo esquerdo) e mudar a cor!
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = firebaseuser.getUid();

                    Toast.makeText(getApplicationContext(), uid, Toast.LENGTH_SHORT).show();

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
                addUserInfo();
                break;
            case R.id.btnBack:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

    private void addUserInfo() {
        String name = txtInputName.getText().toString().trim();
        String phone = txtInputPhone.getText().toString().trim();
        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseuser.getUid();

        db.collection("Users_master").document(uid);

        /* User user = new User(name, uid);*/

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("uid", uid);
        userProfile.put("phone_number", phone);
        if (radioVip.isSelected()){
            userProfile.put("type", "VIP");
            db.collection("Users_child").document(uid)
                    .set(userProfile)
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
                    });
        }else{
            userProfile.put("type", "bodyguard");

            db.collection("Users_master").document(uid)
                    .set(userProfile)
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
                    });
        }

    }

    public void onRadioButtonClicked(View view){
        int colorPrimary = getResources().getColor(R.color.colorPrimary);
        boolean isSelected = ((AppCompatRadioButton)view).isChecked();

        //Unselected text color vai ser cinzento escuro. Se não ficar bem então muda-se para a colorPrimary.
        switch (view.getId()){
            case R.id.radioBg:
                if (isSelected){
                    radioBg.setTextColor(colorPrimary);
                    radioVip.setTextColor(Color.LTGRAY);
                }
                break;
            case R.id.radioVIP:
                    radioVip.setTextColor(colorPrimary);
                    radioBg.setTextColor(Color.LTGRAY);
                break;
        }
    }

}

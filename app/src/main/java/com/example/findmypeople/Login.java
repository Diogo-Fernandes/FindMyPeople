package com.example.findmypeople;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.Map;


public class Login extends AppCompatActivity implements View.OnClickListener {
    TextView txtRegister2;
    Button btnLogin;
    EditText txtEmail, txtPassword;
    ProgressBar progressBar;
    String userType;

    static DocumentSnapshot userInfo;
    DocumentReference userRef;

    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        txtRegister2 = (TextView) findViewById(R.id.txtRegister2);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);


        findViewById(R.id.txtRegister2).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);

//        getUserChildData();


    }

    //To open Register Activity when yellow text is clicked
    public void openRegisterActivity() {
        Intent intent = new Intent(this, Register_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void userLogin() {

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            txtEmail.setError("E-mail é obrigatório!");
            txtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            txtPassword.setError("Password é obrigatória!");
            txtPassword.requestFocus();
            return;
        }


        //BUG - Falta alterar a posiçao da progress bar (ta no topo esquerdo) e mudar a cor!
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {

                    userTypeDecider();
                    userTypeDecider2();


                    //Log.d("userLogin", "DATA: " + teste);

                    /*if (teste == "VIP") {
                        Log.d("TESTE", "OLAAAAA ");
                        Toast.makeText(getApplicationContext(), "sou Child", Toast.LENGTH_SHORT).show();
                    }*/

                    //Vai carregar a Contacts Activity(pagina branca) porque ainda nao carreguei os fragments da matilde
                    //JÁ CONSEGUI POR DIREITO

                    /*Intent intent = new Intent(Login.this, Main2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    //Verifies if user is master
    public void userTypeDecider(){

        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseuser.getUid();

        CollectionReference masterRef = firebaseFirestore.collection("Users_master");
        Query query = masterRef.whereEqualTo("uid", uid);
        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                userType = "bodyguard";
                                Log.d("LoginDebug", "Successful: " +document.toString());
                                Intent intent = new Intent(Login.this, Main2Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        } else {

                                Log.d("LoginDebug", "Failed: ");
                                Intent intent = new Intent(Login.this, BottomNavChild.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                    }
                });
    }
    //Verifies if user is child
    public void userTypeDecider2(){

        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseuser.getUid();

        CollectionReference masterRef = firebaseFirestore.collection("Users_child");
        Query query = masterRef.whereEqualTo("uid", uid);
        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                userType = "bodyguard";
                                Log.d("LoginDebug", "Successful: " +document.toString());
                                Intent intent = new Intent(Login.this, BottomNavChild.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        } else {

                            Log.d("LoginDebug", "Failed: ");
                            Intent intent = new Intent(Login.this, Main2Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtRegister2:
                openRegisterActivity();
                break;

            case R.id.btnLogin:
                userLogin();
                break;
        }
    }



}

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.Map;


public class Login extends AppCompatActivity implements View.OnClickListener {
    TextView txtRegister2;
    Button btnLogin;
    EditText txtEmail, txtPassword;
    ProgressBar progressBar;

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

                    getUserChildData();
//                    getUserMasterData();

                    Log.d("userLogin", "DATA: " + teste);

                    if (teste == "VIP") {
                        Log.d("TESTE", "OLAAAAA ");
                        Toast.makeText(getApplicationContext(), "sou Child", Toast.LENGTH_SHORT).show();
                    }

                    //Vai carregar a Contacts Activity(pagina branca) porque ainda nao carreguei os fragments da matilde
                    //JÁ CONSEGUI POR DIREITO
                    Intent intent = new Intent(Login.this, Main2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    public void getUserChildData() {
        Log.d("USER ID", "getUserData: " + mAuth.getCurrentUser().getUid());

        // Creates a query and then gets it
        Query query = firebaseFirestore.collection("Users_child").whereEqualTo("uid", mAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Log.d("QUALQUER COISA", "DocumentSnapshot data: " + task.getResult().getDocuments().get(0));

                    // Updates variables whenever a change happens to the database
                    userRef = firebaseFirestore.collection("Users_child").document(task.getResult().getDocuments().get(0).getId());
                    userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (snapshot != null && snapshot.exists()) {
                                Log.d("DATA", "Current data: " + snapshot.getData());

                                // Saves updated user to variable
                                userInfo = snapshot;
                                teste = userInfo.getString("type");
                                Log.d("onComplete", "Type " + teste);
                            } else {
                                Log.d("DATA VAZIO", "Current data: null");
                            }
                        }
                    });
                } else {
                    Log.d("FAIL", "onComplete: FAIL - " + task.getException());
                }
            }
        });
    }


    // Gets the users data from the database
    public void getUserMasterData() {
        Log.d("USER ID", "getUserData: " + mAuth.getCurrentUser().getUid());

        // Creates a query and then gets it
        Query query = firebaseFirestore.collection("Users_master").whereEqualTo("uid", mAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Log.d("QUALQUER COISA", "DocumentSnapshot data: " + task.getResult().getDocuments().get(0));

                    // Updates variables whenever a change happens to the database
                    userRef = firebaseFirestore.collection("Users_master").document(task.getResult().getDocuments().get(0).getId());
                    userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (snapshot != null && snapshot.exists()) {
                                Log.d("DATA", "Current data: " + snapshot.getData());

                                // Saves updated user to variable
                                userInfo = snapshot;
                                teste = userInfo.getString("type");
                                Log.d("DATA", "Type " + teste);
                            } else {
                                Log.d("DATA VAZIO", "Current data: null");
                            }
                        }
                    });

                } else {
                    Log.d("FAIL", "onComplete: FAIL - " + task.getException());
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

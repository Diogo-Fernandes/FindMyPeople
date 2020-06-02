package com.example.findmypeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Contacts_Activity extends AppCompatActivity implements View.OnClickListener {


    EditText txtEditName, txtEditPhone;
    Button btnAccount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        btnAccount = findViewById(R.id.btnAccount);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnAccount:
                addUserInfo();
                break;
    }
}
    private void addUserInfo() {
        String name = txtEditName.getText().toString().trim();
        String phone = txtEditPhone.toString().trim();
        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseuser.getUid();

        CollectionReference dbUsers = db.collection("Users_master");

        User user = new User(name, uid);
        Log.d(TAG, name);
        Log.d(TAG, uid);
        dbUsers.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Contacts_Activity.this, "Deu", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Contacts_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}

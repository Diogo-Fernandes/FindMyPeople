package com.example.findmypeople;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment {

    ImageView btnBack2;
    EditText txtName, txtPhone;
    Button btnAccess;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public AddContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_contact, container, false);

        btnBack2 = v.findViewById(R.id.btnBack2);
        txtName = v.findViewById(R.id.txtName);
        txtPhone = v.findViewById(R.id.txtPhone);
        btnAccess = v.findViewById(R.id.btnAccess);
        db = FirebaseFirestore.getInstance();

        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        btnAccess.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "AddContactFragment";

            @Override
            public void onClick(View v) {
               //String name = txtName.getText().toString();
               String phone = txtPhone.getText().toString().trim();

               /*if(name.isEmpty()){
                    txtName.setError("Nome é obrigatório!");
                    txtName.requestFocus();
                    return;
                }*/ //Feature a ser implementada (Nomes personalizados para cada contacto)

                if(phone.isEmpty()){
                    txtPhone.setError("Nº de Telemóvel é obrigatório!");
                    txtPhone.requestFocus();
                    return;
                }

                CollectionReference childRef = db.collection("Users_child");
                Query query = childRef.whereEqualTo("phone_number", phone);
                        query
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for(QueryDocumentSnapshot document: task.getResult()){
                                        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = firebaseuser.getUid();
                                        Log.d(TAG, "onComplete: " + uid);
                                        String childID = document.getId();
                                        DocumentReference masterRef = db.collection("Users_master").document(uid);
                                        masterRef.update("users_child", FieldValue.arrayUnion(childID));
                                        Log.d(TAG, "onComplete: "+ document.getId() + "->" + document.getData());
                                    }
                                } else {
                                    //fazer toast a avisar que nao há numero de telefone na database
                                    Log.d(TAG, "Error getting documents:", task.getException());
                                }
                            }
                        });


            }
        });

        return v;
    }
}

package com.example.findmypeople;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
public class ContactsFragment extends Fragment {

    Button addContact;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String TAG = "ContactsFragment";

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        getChildData();

        addContact = v.findViewById(R.id.addContact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddContactFragment addContactFragment = new AddContactFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, addContactFragment);
                transaction.addToBackStack(null).commit();
            }
        });

        return v;
    }

    public void getChildData() {

        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseuser.getUid();

        CollectionReference masterRef = firebaseFirestore.collection("Users_master");
        Query query = masterRef.whereEqualTo("uid", uid);
        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = firebaseuser.getUid();
                                Log.d(TAG, "MasterID: " + uid);
                                String childID = document.getId();
                                Log.d(TAG, "childID: " + childID);
//                                DocumentReference masterRef = firebaseFirestore.collection("Users_child").document(uid);
                                Log.d(TAG, "onComplete: " + document.getId() + "->" + document.getData().get("users_child"));

                                DocumentReference childRef = firebaseFirestore.collection("Users_child").document(uid);
                                Log.d(TAG, "onComplete: " + document.getId() + "->" + childRef);

                                //Não sei se é suposto fazer isto
                                Query query = firebaseFirestore.collection("Users_child").whereEqualTo("uid", document.getData().get("users_child"));
                                query
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Entrou: ");
                                                    FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
                                                    String uid = firebaseuser.getUid();

                                                }
                                            }

                                        });
                            }

                        } else {

                            Log.d("LoginDebug", "Failed: ");

                        }
                    }
                });
    }
}

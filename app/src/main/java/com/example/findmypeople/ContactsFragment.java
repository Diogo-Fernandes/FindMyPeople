package com.example.findmypeople;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    Button addContact;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView mFirestoreList;
    private static final String TAG = "ContactsFragment";
    private FirestoreRecyclerAdapter adapter;

    private ArrayList<String> arrayNames = new ArrayList<>();
    ArrayList<String> arrayUserIds = new ArrayList<>();

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        getChildData();

        mFirestoreList = v.findViewById(R.id.firestore_list);
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

                                ArrayList<String> childArray = (ArrayList<String>) document.get("users_child");
                                for (int i = 0; i < childArray.size(); i++) {
                                    Log.d(TAG, "log de um possivel array: " + childArray.get(i));

                                    CollectionReference childRef = firebaseFirestore.collection("Users_child");
                                    //childRef.whereArrayContainsAny não é importado pelo android studio. Porquê? não sei, mesmo. Por causa disto nao dá para carregar a lista de contactos corretamente.
                                    Query queryChild = childRef.whereEqualTo("uid", childArray.get(i));
                                    queryChild
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document2 : task.getResult()) {
                                                            Log.d(TAG, "data de cada child: " + document2.getData());
                                                            arrayNames.add(document2.getString("name"));
                                                            Log.d(TAG, "Array: " + arrayNames);
                                                        }
                                                    }
                                                }
                                            });



                                    FirestoreRecyclerOptions<ContactsModel> options = new FirestoreRecyclerOptions.Builder<ContactsModel>()
                                            .setQuery(queryChild, ContactsModel.class)
                                            .build();


                                    adapter = new FirestoreRecyclerAdapter<ContactsModel, ContactsViewHolder>(options) {
                                        @NonNull
                                        @Override
                                        public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacts, parent, false);
                                            return new ContactsViewHolder(view);
                                        }

                                        @Override
                                        protected void onBindViewHolder(@NonNull ContactsViewHolder contactsViewHolder, int i, @NonNull ContactsModel contactsModel) {
                                            contactsViewHolder.userName.setText(contactsModel.getName());
                                            contactsViewHolder.location.setText(contactsModel.getLocation());
                                            if(contactsViewHolder.location.getText() == "" || contactsViewHolder.location.getText() == null){
                                                contactsViewHolder.location.setText("Unknown street");
                                            }
                                            Log.d(TAG, "name child: " + contactsModel.getName());

//                                            for(int j = 0; j < contactsModel.size(); j++){
//                                                contactsViewHolder.userName.setText(arrayNames.get(i));
//                                                Log.d(TAG, "data : " + arrayNames.get(i));
//                                            }
                                        }
                                    };
                                    mFirestoreList.setHasFixedSize(true);
                                    mFirestoreList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                                    adapter.startListening();
                                    Log.d(TAG, "onComplete: " + adapter);
                                    mFirestoreList.setAdapter(adapter);




                                }
                                //o array com os IDs dos childs está em document.getData().get("users_child")
                                /*DocumentReference childRef = firebaseFirestore.collection("Users_child").document(uid);
                                Log.d(TAG, "onComplete: " + document.getId() + "->" + childRef);*/


                            }
                        } else {
                            Log.d("LoginDebug", "Failed: ");
                        }
                    }
                });
    }

    private class ContactsViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView location;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            location = itemView.findViewById(R.id.location);
            Log.d(TAG, "Array: " + arrayNames);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ChildProfileFragment childProfileFragment = new ChildProfileFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, childProfileFragment);
                    transaction.addToBackStack(null).commit();
                }
            });
        }
    }


    /*@Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

}

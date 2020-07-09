package com.example.findmypeople;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {

    Button btnPanic;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String uid;


    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_help, container, false);
        db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseuser.getUid();

        btnPanic = v.findViewById(R.id.btnPanic);
        btnPanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference childRef = db.collection("Users_child");
                Query query = childRef.whereEqualTo("uid", uid);
                query
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document: task.getResult()){
                                        final String master_uid = document.getString("master_uid");
                                        final String child_name = document.getString("name");
                                        Log.d("HelpFragment", "onClick: "+ master_uid + " "+ child_name);

                                        CollectionReference masterRef = db.collection("Users_master");
                                        Query query2 = masterRef.whereEqualTo("uid", master_uid);
                                        query2
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                        if(task2.isSuccessful()){
                                                            for (QueryDocumentSnapshot document2: task2.getResult()){
                                                                String phone_number = document2.getString("phone_number");
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                                                Date date = new Date();


                                                                Map<String, Object> notification = new HashMap<>();
                                                                notification.put("master_uid", master_uid);
                                                                notification.put("child_uid", uid);
                                                                notification.put("child_name", child_name);
                                                                notification.put("content", "ALERTA! "+child_name+" acabou de utilizar o botão de pânico!");
                                                                notification.put("date", dateFormat.format(date));
                                                                db.collection("Notifications").document().set(notification)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d("Notifs", "onSuccess: fez upload sim ");
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d("Notifs", "onFailure: nao fez upload nao");
                                                                    }
                                                                });

                                                                HelpDialogFragment helpDialogFragment = new HelpDialogFragment();
                                                                helpDialogFragment.show(getFragmentManager(), "MyFragment");
                                                                //starts call intent on master id phone number
                                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                                intent.setData(Uri.parse("tel:"+phone_number));
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        });



        return v;
    }
}

package com.example.findmypeople;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.

 */
public class NotificationsFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseuser.getUid();
        mFirestoreList = v.findViewById(R.id.firestore_listNotifs);

        //query
        Query query = firebaseFirestore.collection("Notifications").whereEqualTo("master_uid", uid);
        //recycleroptions
        FirestoreRecyclerOptions<NotifsModel> options = new FirestoreRecyclerOptions.Builder<NotifsModel>()
                .setQuery(query, NotifsModel.class)
                .build();
         adapter = new FirestoreRecyclerAdapter<NotifsModel, NotifsViewHolder>(options) {
            @NonNull
            @Override
            public NotifsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notif, parent, false);

                return new NotifsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NotifsViewHolder notifsViewHolder, int i, @NonNull NotifsModel notifsModel) {
                notifsViewHolder.notifName.setText(notifsModel.getChild_name());
                notifsViewHolder.notifDate.setText(notifsModel.getDate());
                notifsViewHolder.notifContent.setText(notifsModel.getContent());
            }
        };


        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirestoreList.setAdapter(adapter);



        return v;
    }

    private class NotifsViewHolder extends RecyclerView.ViewHolder{
        private TextView notifName;
        private TextView notifDate;
        private TextView notifContent;

        public NotifsViewHolder(@NonNull View itemView) {
            super(itemView);

            notifName = itemView.findViewById(R.id.notifName);
            notifDate = itemView.findViewById(R.id.notifDate);
            notifContent = itemView.findViewById(R.id.notifContent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

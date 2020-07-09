package com.example.findmypeople;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddDarkZoneFragment extends Fragment {

    EditText txtAddress;
    EditText txtRadius;
    Button btnAdd;
    ImageView btnBack;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public AddDarkZoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_dark_zone, container, false);
        txtAddress = v.findViewById(R.id.txtAddress);
        txtRadius = v.findViewById(R.id.txtRadius);
        btnAdd = v.findViewById(R.id.btnAddZone);


        btnBack = v.findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChildProfileFragment childProfileFragment = new ChildProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, childProfileFragment);
                transaction.addToBackStack(null).commit();
            }
        });



        db = FirebaseFirestore.getInstance();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "AddContactFragment";

            @Override
            public void onClick(View v) {

                String strAddress = txtAddress.getText().toString();
                String radius = txtRadius.getText().toString().trim();

                if (strAddress.isEmpty()) {
                    txtAddress.setError("Nome é obrigatório!");
                    txtAddress.requestFocus();
                    return;
                } //Feature a ser implementada (Nomes personalizados para cada contacto)

                if (radius.isEmpty()) {
                    txtRadius.setError("Nº de Telemóvel é obrigatório!");
                    txtRadius.requestFocus();
                    return;
                }

                String lat = String.valueOf(getLocationFromAddress(strAddress).getLatitude());
                String lng = String.valueOf(getLocationFromAddress(strAddress).getLongitude());

                Map<String, Object> zone = new HashMap<>();
                //Adicionar as coordenadas à bd
                zone.put("Lat", lat);
                zone.put("Lng", lng);
                zone.put("Radius", radius);

                DocumentReference ref = db.collection("Zones").document();
                String zoneId = ref.getId();

                db.collection("Zones").document(zoneId).set(zone)
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
        });
        return v;
    }

    //Converte a morada em coordenadas
    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            Log.d("TAGGG", "OLAAA" + location.getLatitude());
            Log.d("TAGGG", "OLAAA" + location.getLongitude());

            p1 = new GeoPoint((double) (location.getLatitude()), (double) (location.getLongitude()));
            Log.d("TAGGG", "OLAAA" + p1);
            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }


    private void AddZone() {

    }
}

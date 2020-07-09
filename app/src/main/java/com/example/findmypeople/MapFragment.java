package com.example.findmypeople;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    FusedLocationProviderClient client;

    Geocoder geocoder;
    List<Address> addresses;

    FirebaseFirestore db;

    ArrayList<String> address = new ArrayList<>();

    private static final String TAG = "Map fragment";

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        View rootView = inflater.inflate(R.layout.fragment_map_child, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapViewChild);
        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        client = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            getDarkZones();
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast.makeText(getActivity().getApplicationContext(), "Não tem acesso à localização", Toast.LENGTH_SHORT).show();
        }

        mMapView.onResume(); // needed to get the map to display immediately

        return rootView;
    }

    private void getCurrentLocation() {
        Task<Location> task = client.getLastLocation();
        Log.d("AAAAAAAAAh", "AQUI");
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;

                        // For dropping a marker at a point on the Map
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        geocoder = new Geocoder(getContext(), Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                        googleMap.addMarker(markerOptions.title("Eu").snippet(address).flat(true));

                        Log.d("AAAAAAAAAh", "OnCreate" + latLng);

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
            }
        });
    }

    private void getDarkZones() {
        Log.d("AAAAAAAAAh", "OLA" );

        db.collection("Zones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("AAAAAAAAAh", "OnCreate" + document.getData());

                                address.add(document.getString("Lat"));
                                address.add(document.getString("Lng"));
                                address.add(document.getString("Radius"));

                                addCircle(document.getString("Lat"), document.getString("Lng"), document.getString("Radius"));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }



    private void addCircle(String lat, String lng, String radius) {
        Double lat2 = Double.parseDouble(lat);
        Double lng2 = Double.parseDouble(lng);
        Double radius2 = Double.parseDouble(radius);
        LatLng latLng = new LatLng(lat2, lng2);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius2);
        circleOptions.strokeColor(Color.argb(255, 41, 58, 77));
        circleOptions.fillColor(Color.argb(70, 41, 58, 77));
        circleOptions.strokeWidth(4);
        googleMap.addCircle(circleOptions);
    }
}

package com.example.findmypeople;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildProfileFragment extends Fragment {

    Button darkZones;
    Button btnTimeline;

    public ChildProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_child_profile, container, false);

        btnTimeline = v.findViewById(R.id.btnTimeline);
        btnTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineFragment timelineFragment = new TimelineFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, timelineFragment);
                transaction.addToBackStack(null).commit();
            }
        });

        darkZones = v.findViewById(R.id.btnDarkzones);
        darkZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDarkZoneFragment addDarkZonesFragment = new AddDarkZoneFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, addDarkZonesFragment);
                transaction.addToBackStack(null).commit();
            }
        });



        return v;
    }
}

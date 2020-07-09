package com.example.findmypeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main2Activity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ContactsFragment()).commit();
        Log.i("main2","OnCreate");

    }

    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment fragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.contacts:
                            fragment = new ContactsFragment();
                            break;

                        case R.id.timeline:
                            fragment = new TimelineFragment();
                            break;

                        case R.id.map:
                            fragment = new MapFragment();
                            break;

                        case R.id.notifications:
                            fragment = new NotificationsFragment();
                            break;

                        case R.id.profile:
                            fragment = new UserProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

                    return true;
                }
            };

    @Override
    public void onBackPressed() {
     //super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
}

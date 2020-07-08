package com.example.findmypeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavChild extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav_child);

        bottomNavigationView = findViewById(R.id.bottomNavChild);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.containerChild, new HelpFragment()).commit();
    }

    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment fragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.help:
                            fragment = new HelpFragment();
                            break;

                        case R.id.mapChild:
                            fragment = new MapFragment();
                            break;

                        case R.id.reward:
                            fragment = new RewardsFragmentChild();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.containerChild, fragment).addToBackStack(null).commit();

                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
}

package com.group3.spotifywrapped;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.group3.spotifywrapped.MainView.MainPageActivity;

public class settingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_wrapped:
                        Intent intent1 = new Intent(settingsActivity.this, MainPageActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.bottom_settings:
                        break;

//                    case R.id.ic_books:
//                        Intent intent2 = new Intent(MainActivity.this, ActivityTwo.class);
//                        startActivity(intent2);
//                        break;
//
//                    case R.id.ic_center_focus:
//                        Intent intent3 = new Intent(MainActivity.this, ActivityThree.class);
//                        startActivity(intent3);
//                        break;
//
//                    case R.id.ic_backup:
//                        Intent intent4 = new Intent(MainActivity.this, ActivityFour.class);
//                        startActivity(intent4);
//                        break;
                }


                return false;
            }
        });


    }

}

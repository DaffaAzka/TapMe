 package com.dest.tapme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.dest.tapme.fragments.HomeFragment;
import com.dest.tapme.fragments.MarketFragment;
import com.dest.tapme.fragments.QuestFragment;
import com.dest.tapme.fragments.RankFragment;
import com.dest.tapme.models.User;
import com.dest.tapme.utils.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

 public class MainActivity extends AppCompatActivity {

     private BottomNavigationView bottomNavigationView;
     private Fragment selectorFragment;
     private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_quest:
                        selectorFragment = new QuestFragment();
                        break;

                    case R.id.nav_rank:
                        selectorFragment = new RankFragment();
                        break;

                    case R.id.nav_store:
                        selectorFragment = new MarketFragment();
                        break;
                }

                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
                return true;

            }
        });

        Bundle intent = getIntent().getExtras();

        if (intent != null) {
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

     @Override
     protected void onStart() {
         super.onStart();
         FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                 .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {

                 User user = snapshot.getValue(User.class);

                 if (user.getButtonType().equals("none")) {
                     startActivity(new Intent(MainActivity.this , SelectButtonActivity.class));
                     finish();
                 }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
     }
 }
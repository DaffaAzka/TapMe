package com.dest.tapme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dest.tapme.models.User;
import com.dest.tapme.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout settings_my_profile_layout, settings_switch_button_layout, settings_information_layout, settings_sign_out_layout;
    CircleImageView img_settings_my_profile;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings_my_profile_layout = findViewById(R.id.settings_my_profile_layout);
        settings_switch_button_layout = findViewById(R.id.settings_switch_button_layout);
        settings_information_layout = findViewById(R.id.settings_information_layout);
        settings_sign_out_layout = findViewById(R.id.settings_sign_out_layout);

        img_settings_my_profile = findViewById(R.id.img_settings_my_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user.getProfileImg().equals("default")) {
                    img_settings_my_profile.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Picasso.get().load(user.getProfileImg()).into(img_settings_my_profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        settings_my_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MyProfileActivity.class));
                finish();
            }
        });

        settings_switch_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SelectButtonActivity.class));
                finish();
            }
        });

        settings_information_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, InformationActivity.class));
                finish();
            }
        });

        settings_sign_out_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, StartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }
}
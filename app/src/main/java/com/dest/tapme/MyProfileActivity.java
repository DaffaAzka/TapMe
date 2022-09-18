package com.dest.tapme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dest.tapme.models.Medal;
import com.dest.tapme.models.Record;
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

public class MyProfileActivity extends AppCompatActivity {

    CircleImageView img_my_profile_picture;
    ImageView my_back, img_my_edit;
    TextView tv_my_username, tv_my_score, tv_my_point;
    RelativeLayout my_medal_crown_locked_layout, my_medal_one_day_locked_layout, my_medal_diamond_locked_layout, my_medal_high_score_locked_layout, my_medal_high_point_locked_layout;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        img_my_profile_picture = findViewById(R.id.img_my_profile_picture);

        tv_my_username = findViewById(R.id.tv_my_username);
        tv_my_score = findViewById(R.id.tv_my_score);
        tv_my_point = findViewById(R.id.tv_my_point);

        my_back = findViewById(R.id.my_back);
        img_my_edit = findViewById(R.id.img_my_edit);

        my_medal_crown_locked_layout = findViewById(R.id.my_medal_crown_locked_layout);
        my_medal_one_day_locked_layout = findViewById(R.id.my_medal_one_day_locked_layout);
        my_medal_diamond_locked_layout = findViewById(R.id.my_medal_diamond_locked_layout);
        my_medal_high_score_locked_layout = findViewById(R.id.my_medal_high_score_locked_layout);
        my_medal_high_point_locked_layout = findViewById(R.id.my_medal_high_point_locked_layout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                tv_my_username.setText(user.getUsername());

                if (user.getProfileImg().equals("default")) {
                    img_my_profile_picture.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Picasso.get().load(user.getProfileImg()).into(img_my_profile_picture);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Records")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Record record = snapshot.getValue(Record.class);
                tv_my_point.setText(record.getPoint().toString());
                tv_my_score.setText(record.getScore().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Medal medal = snapshot.getValue(Medal.class);

                if (medal.getMedalKing().equals("claimed")) {
                    my_medal_crown_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalOneDay().equals("claimed")) {
                    my_medal_one_day_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalRich().equals("claimed")) {
                    my_medal_diamond_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalHighScore().equals("claimed")) {
                    my_medal_high_score_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalHighPoint().equals("claimed")) {
                    my_medal_high_point_locked_layout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        img_my_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfileActivity.this, EditProfileActivity.class));
            }
        });

        my_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
            }
        });

    }


}
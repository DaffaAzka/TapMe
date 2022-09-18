package com.dest.tapme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dest.tapme.fragments.RankFragment;
import com.dest.tapme.models.Medal;
import com.dest.tapme.models.Record;
import com.dest.tapme.models.User;
import com.dest.tapme.utils.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileActivity extends AppCompatActivity {

    CircleImageView img_other_profile_picture;
    ImageView img_other_back;
    TextView tv_other_username, tv_other_score, tv_other_point;
    RelativeLayout other_medal_crown_locked_layout, other_medal_one_day_locked_layout, other_medal_diamond_locked_layout, other_medal_high_score_locked_layout, other_medal_high_point_locked_layout;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        img_other_profile_picture = findViewById(R.id.img_other_profile_picture);

        tv_other_username = findViewById(R.id.tv_other_username);
        tv_other_score = findViewById(R.id.tv_other_score);
        tv_other_point = findViewById(R.id.tv_other_point);

        img_other_back = findViewById(R.id.img_other_back);

        other_medal_crown_locked_layout = findViewById(R.id.other_medal_crown_locked_layout);
        other_medal_one_day_locked_layout = findViewById(R.id.other_medal_one_day_locked_layout);
        other_medal_diamond_locked_layout = findViewById(R.id.other_medal_diamond_locked_layout);
        other_medal_high_score_locked_layout = findViewById(R.id.other_medal_high_score_locked_layout);
        other_medal_high_point_locked_layout = findViewById(R.id.other_medal_high_point_locked_layout);

        userId = getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                tv_other_username.setText(user.getUsername());

                if (user.getProfileImg().equals("default")) {
                    img_other_profile_picture.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Picasso.get().load(user.getProfileImg()).into(img_other_profile_picture);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Records")
                .child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Record record = snapshot.getValue(Record.class);
                tv_other_point.setText(record.getPoint().toString());
                tv_other_score.setText(record.getScore().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                .child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Medal medal = snapshot.getValue(Medal.class);

                if (medal.getMedalKing().equals("claimed")) {
                    other_medal_crown_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalOneDay().equals("claimed")) {
                    other_medal_one_day_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalRich().equals("claimed")) {
                    other_medal_diamond_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalHighScore().equals("claimed")) {
                    other_medal_high_score_locked_layout.setVisibility(View.GONE);
                }

                if (medal.getMedalHighPoint().equals("claimed")) {
                    other_medal_high_point_locked_layout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        img_other_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OtherProfileActivity.this, RankFragment.class));
            }
        });
    }
}
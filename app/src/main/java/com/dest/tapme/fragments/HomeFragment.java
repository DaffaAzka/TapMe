package com.dest.tapme.fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dest.tapme.MainActivity;
import com.dest.tapme.MyProfileActivity;
import com.dest.tapme.R;
import com.dest.tapme.SelectButtonActivity;
import com.dest.tapme.SettingsActivity;
import com.dest.tapme.StartActivity;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    TextView point, score;
    ImageView button;
    CircleImageView img_home_profile_picture;
    ImageView img_home_settings;

    FirebaseUser firebaseUser;

    Integer pointInDB, duplicatePoint;

    Vibrator vibrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        button = view.findViewById(R.id.home_button);
        point = view.findViewById(R.id.home_point);
        score = view.findViewById(R.id.home_score);
        img_home_profile_picture = view.findViewById(R.id.img_home_profile_picture);
        img_home_settings = view.findViewById(R.id.img_home_settings);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.getButtonType().equals("one")) {
                    button.setImageResource(R.drawable.custom_button_one);
                } else if (user.getButtonType().equals("two")) {
                    button.setImageResource(R.drawable.custom_button_two);
                }

                if (user.getProfileImg().equals("default")) {
                    img_home_profile_picture.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Picasso.get().load(user.getProfileImg()).into(img_home_profile_picture);
                }

                duplicatePoint = user.getDuplicatePoint();
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
                point.setText(record.getPoint().toString());
                score.setText(record.getScore().toString());
                pointInDB = record.getPoint();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        img_home_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyProfileActivity.class));
            }
        });

        img_home_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                    );
                } else {
                    long[] pattern = {0, 100};
                    vibrator.vibrate(pattern, -1);
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("point", pointInDB + duplicatePoint);
                FirebaseDatabase.getInstance(Util.realtimeDBLink())
                        .getReference().child("Records").child(firebaseUser.getUid()).updateChildren(map);
            }
        });
        return view;
    }
}
package com.dest.tapme.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dest.tapme.R;
import com.dest.tapme.models.Medal;
import com.dest.tapme.models.Record;
import com.dest.tapme.utils.Util;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class QuestFragment extends Fragment {

    LinearProgressIndicator quest_progress_indicator_one, quest_progress_indicator_two;
    RelativeLayout quest_one_btn, quest_one_btn_clicked, quest_two_btn, quest_two_btn_clicked;

    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quest, container, false);

        quest_progress_indicator_one = view.findViewById(R.id.quest_progress_indicator_one);
        quest_one_btn = view.findViewById(R.id.quest_one_btn);
        quest_one_btn_clicked = view.findViewById(R.id.quest_one_btn_clicked);

        quest_progress_indicator_two = view.findViewById(R.id.quest_progress_indicator_two);
        quest_two_btn = view.findViewById(R.id.quest_two_btn);
        quest_two_btn_clicked = view.findViewById(R.id.quest_two_btn_clicked);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        quest_progress_indicator_one.setMax(100);
        quest_progress_indicator_two.setMax(15000);

        quest_one(view);
        quest_two(view);

        return view;
    }

    private void quest_one(View view) {
        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Medal medal = snapshot.getValue(Medal.class);

                if (medal.getMedalKing().equals("claimed")) {
                    quest_progress_indicator_one.setProgress(100);
                    quest_one_btn_clicked.setVisibility(View.GONE);
                } else {

                    if (medal.getMedalKing().equals("claim")) {
                        quest_progress_indicator_one.setProgress(100);
                        quest_one_btn_clicked.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        quest_one_btn_clicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("medalKing", "claimed");
                FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                        .child(firebaseUser.getUid()).updateChildren(map);

                Snackbar snackbar = Snackbar.make(view, "Successfully claimed!", BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.navy_700));
                snackbar.setActionTextColor(getResources().getColor(R.color.white));
                snackbar.show();

                quest_two_btn_clicked.setVisibility(View.GONE);
            }
        });

    }


    private void quest_two(View view) {
        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Records")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Record record  = snapshot.getValue(Record.class);
                quest_progress_indicator_two.setProgress(record.getPoint());

                FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                        .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Medal medal = snapshot.getValue(Medal.class);

                        if (medal.getMedalRich().equals("claimed")) {
                            quest_two_btn_clicked.setVisibility(View.GONE);
                        } else {
                            if (record.getPoint() > 15000) {
                                quest_two_btn_clicked.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        quest_two_btn_clicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("medalRich", "claimed");
                FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                        .child(firebaseUser.getUid()).updateChildren(map);

                Snackbar snackbar = Snackbar.make(view, "Successfully claimed!", BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.navy_700));
                snackbar.setActionTextColor(getResources().getColor(R.color.white));
                snackbar.show();

                quest_two_btn_clicked.setVisibility(View.GONE);
            }
        });

    }


}
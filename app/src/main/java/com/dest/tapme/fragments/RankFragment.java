package com.dest.tapme.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dest.tapme.R;
import com.dest.tapme.adapter.RankAdapter;
import com.dest.tapme.models.Record;
import com.dest.tapme.models.User;
import com.dest.tapme.utils.Util;
import com.dest.tapme.validates.Validate;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankFragment extends Fragment {

    CircleImageView img_rank_profile_picture;
    TextView tv_rank_username, tv_rank_score, tv_rank_point_to_score;
    EditText et_rank_point;
    RelativeLayout btn_rank_to_change_option, btn_rank_change, rank_change_score_layout;
    RecyclerView recyclerView;

    List<Record> records;
    RankAdapter rankAdapter;

    Integer myPoint, myScore;

    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        img_rank_profile_picture = view.findViewById(R.id.img_rank_profile_picture);

        tv_rank_username = view.findViewById(R.id.tv_rank_username);
        tv_rank_score = view.findViewById(R.id.tv_rank_score);
        tv_rank_point_to_score = view.findViewById(R.id.tv_rank_point_to_score);

        et_rank_point = view.findViewById(R.id.et_rank_point);

        btn_rank_to_change_option = view.findViewById(R.id.btn_rank_to_change_option);
        btn_rank_change = view.findViewById(R.id.btn_rank_change);

        rank_change_score_layout = view.findViewById(R.id.rank_change_score_layout);

        recyclerView = view.findViewById(R.id.recycler_view_rank);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rank_change_score_layout.animate().alpha(0f).setDuration(0);

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                tv_rank_username.setText(user.getUsername());

                if (user.getProfileImg().equals("default")) {
                    img_rank_profile_picture.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Picasso.get().load(user.getProfileImg()).into(img_rank_profile_picture);
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

                myPoint = record.getPoint();
                myScore = record.getScore();

                tv_rank_score.setText("" + myScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_rank_to_change_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rank_change_score_layout.setVisibility(View.VISIBLE);
                rank_change_score_layout.animate().alpha(1f).setDuration(800);
            }
        });

        et_rank_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    changePointToScore();
                } catch (NumberFormatException e) {
                    Log.d("Exceptions", e.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        ===================================================================== ( RC View )

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        records = new ArrayList<>();

        rankAdapter = new RankAdapter(getContext(), records);

        recyclerView.setAdapter(rankAdapter);

        getRanked();

//        =====================================================================

        return view;
    }

    public void changePointToScore() {
        Integer point = Integer.parseInt(et_rank_point.getText().toString());
        Integer score = Integer.parseInt(et_rank_point.getText().toString()) / 2;

        tv_rank_point_to_score.setText(String.valueOf(score));

        btn_rank_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messages = Validate.pointToScore(myPoint, point, et_rank_point.getText().toString());
                if (messages != null) {
                    Snackbar snackbar = Snackbar.make(rank_change_score_layout, messages, BaseTransientBottomBar.LENGTH_LONG);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                } else {
                    btn_rank_change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (point < myPoint || point.equals(myPoint)) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("point", myPoint - point);
                                map.put("score", myScore + score);
                                FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                        .getReference().child("Records").child(firebaseUser.getUid()).updateChildren(map);

                                btn_rank_change.animate().rotation(360f).setDuration(1000).start();
                                Snackbar snackbar = Snackbar.make(rank_change_score_layout, "Exchange success.", BaseTransientBottomBar.LENGTH_LONG);
                                snackbar.setBackgroundTint(getResources().getColor(R.color.green));
                                snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                snackbar.show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void getRanked() {

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Records")
                .orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                records.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Record record = snapshot.getValue(Record.class);
                    records.add(record);

                }

                rankAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
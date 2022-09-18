package com.dest.tapme.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dest.tapme.R;
import com.dest.tapme.models.Medal;
import com.dest.tapme.models.Record;
import com.dest.tapme.models.User;
import com.dest.tapme.utils.Util;
import com.dest.tapme.validates.Validate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MarketFragment extends Fragment {

    TextView tv_market_duplicate_point, tv_market_price_duplicate_point;
    ImageView btn_market_buy_duplicate_point, btn_market_buy_high_score, btn_market_buy_high_point;
    RelativeLayout market_layout;

    Integer myScore, myPoint, priceDP, duplicatePoint;

    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        tv_market_duplicate_point = view.findViewById(R.id.tv_market_duplicate_point);
        tv_market_price_duplicate_point = view.findViewById(R.id.tv_market_price_duplicate_point);

        btn_market_buy_duplicate_point = view.findViewById(R.id.btn_market_buy_duplicate_point);
        btn_market_buy_high_score = view.findViewById(R.id.btn_market_buy_high_score);
        btn_market_buy_high_point = view.findViewById(R.id.btn_market_buy_high_point);

        market_layout = view.findViewById(R.id.market_layout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Records")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Record record = snapshot.getValue(Record.class);
                myPoint = record.getPoint();
                myScore = record.getScore();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user.getDuplicatePoint() < 10) {
                    priceDP = 150 * user.getDuplicatePoint();
                } else if (user.getDuplicatePoint() < 20) {
                    priceDP = 230 * user.getDuplicatePoint();
                } else {
                    priceDP = 350 * (user.getDuplicatePoint() + 5);
                }

                duplicatePoint = user.getDuplicatePoint() + 1;

                tv_market_price_duplicate_point.setText(priceDP + " Score");
                tv_market_duplicate_point.setText("x" + duplicatePoint + " Point");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_market_buy_duplicate_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Are you sure you want to buy this item?")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String messages = Validate.market(priceDP, myScore, "Score");

                        if (messages != null) {
                            Snackbar snackbar = Snackbar.make(market_layout, messages, BaseTransientBottomBar.LENGTH_LONG);
                            snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                            snackbar.setActionTextColor(getResources().getColor(R.color.white));
                            snackbar.show();
                        } else {
                            HashMap<String, Object> score = new HashMap<>();
                            score.put("score", myScore - priceDP);

                            HashMap<String, Object> dp = new HashMap<>();
                            dp.put("duplicatePoint", duplicatePoint);


                            FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                    .getReference().child("Records").child(firebaseUser.getUid()).updateChildren(score);

                            FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                    .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(dp);

                            Snackbar snackbar = Snackbar.make(market_layout, "Successful purchase of items", BaseTransientBottomBar.LENGTH_LONG);
                            snackbar.setBackgroundTint(getResources().getColor(R.color.green));
                            snackbar.setActionTextColor(getResources().getColor(R.color.white));
                            snackbar.show();

                        }

                    }
                }).show();
            }
        });

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Medal medal = snapshot.getValue(Medal.class);

                if (medal.getMedalHighScore().equals("locked")) {

                    btn_market_buy_high_score.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new MaterialAlertDialogBuilder(getContext())
                                    .setTitle("Are you sure you want to buy this item?")
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String messages = Validate.market(3000, myScore, "Score");

                                    if (messages != null) {
                                        Snackbar snackbar = Snackbar.make(market_layout, messages, BaseTransientBottomBar.LENGTH_LONG);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                        snackbar.show();
                                    } else {
                                        HashMap<String, Object> score = new HashMap<>();
                                        score.put("score", myScore - 3000);

                                        HashMap<String, Object> highScore = new HashMap<>();
                                        highScore.put("medalHighScore", "claimed");


                                        FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                                .getReference().child("Records").child(firebaseUser.getUid()).updateChildren(score);

                                        FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                                .getReference().child("Medals").child(firebaseUser.getUid()).updateChildren(highScore);

                                        Snackbar snackbar = Snackbar.make(market_layout, "Successful purchase of items", BaseTransientBottomBar.LENGTH_LONG);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.green));
                                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                        snackbar.show();

                                    }

                                }
                            }).show();
                        }
                    });

                } else {

                    btn_market_buy_high_score.setBackgroundResource(R.drawable.ic_check_circle);

                }

                if (medal.getMedalHighPoint().equals("locked")) {

                    btn_market_buy_high_point.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new MaterialAlertDialogBuilder(getContext())
                                    .setTitle("Are you sure you want to buy this item?")
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String messages = Validate.market(6000, myPoint, "Point");

                                    if (messages != null) {
                                        Snackbar snackbar = Snackbar.make(market_layout, messages, BaseTransientBottomBar.LENGTH_LONG);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                        snackbar.show();
                                    } else {
                                        HashMap<String, Object> score = new HashMap<>();
                                        score.put("point", myPoint - 6000);

                                        HashMap<String, Object> highScore = new HashMap<>();
                                        highScore.put("medalHighPoint", "claimed");


                                        FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                                .getReference().child("Records").child(firebaseUser.getUid()).updateChildren(score);

                                        FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                                .getReference().child("Medals").child(firebaseUser.getUid()).updateChildren(highScore);

                                        Snackbar snackbar = Snackbar.make(market_layout, "Successful purchase of items", BaseTransientBottomBar.LENGTH_LONG);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.green));
                                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                        snackbar.show();

                                    }

                                }
                            }).show();
                        }
                    });

                }  else {

                    btn_market_buy_high_point.setBackgroundResource(R.drawable.ic_check_circle);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return view;
    }


}
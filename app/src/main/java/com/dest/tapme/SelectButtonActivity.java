package com.dest.tapme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.dest.tapme.utils.Util;
import com.dest.tapme.validates.Validate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SelectButtonActivity extends AppCompatActivity {

    RelativeLayout select_button_one, select_button_two;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_button);

        select_button_one = findViewById(R.id.select_button_one);
        select_button_two = findViewById(R.id.select_button_two);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        select_button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(SelectButtonActivity.this)
                        .setTitle("Are you sure you want to select this button type?")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("buttonType", "one");

                                FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                        .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(hashMap);

                                startActivity(new Intent(SelectButtonActivity.this, MainActivity.class));

                            }
                }).show();
            }
        });

        select_button_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(SelectButtonActivity.this)
                        .setTitle("Are you sure you want to select this button type?")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("buttonType", "two");

                        FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(hashMap);

                        startActivity(new Intent(SelectButtonActivity.this, MainActivity.class));

                    }
                }).show();
            }
        });
    }
}
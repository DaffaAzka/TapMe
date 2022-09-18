package com.dest.tapme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dest.tapme.utils.Util;
import com.dest.tapme.validates.Validate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private RelativeLayout sign_up_layout;
    private LinearProgressIndicator progressIndicator;
    private TextInputLayout et_username, et_email, et_password, et_retry_password;
    private Button btn_sign_up;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sign_up_layout = findViewById(R.id.sign_up_layout);

        progressIndicator = findViewById(R.id.sign_up_progress_indicator);

        et_username = findViewById(R.id.et_sign_up_username);
        et_email = findViewById(R.id.et_sign_up_email);
        et_password = findViewById(R.id.et_sign_up_password);
        et_retry_password = findViewById(R.id.et_sign_up_retry_password);

        btn_sign_up = findViewById(R.id.btn_sign_up);

        databaseReference = FirebaseDatabase.getInstance("https://tapme-2605-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressIndicator.setVisibility(View.VISIBLE);

                String txt_username = et_username.getEditText().getText().toString();
                String txt_email = et_email.getEditText().getText().toString();
                String txt_password = et_password.getEditText().getText().toString();
                String txt_retry_password = et_retry_password.getEditText().getText().toString();

                String messages = Validate.signUpOrSignIn(txt_username, txt_email, txt_password, txt_retry_password);

                if (messages != null) {
                    progressIndicator.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(sign_up_layout, messages, BaseTransientBottomBar.LENGTH_LONG);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                } else {
                    signUpUser(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    private void signUpUser(final String username, final String email, final String password) {
        progressDialog.setMessage("Please wait!");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> users = new HashMap<>();
                users.put("id", firebaseAuth.getCurrentUser().getUid());
                users.put("username", username);
                users.put("email", email);
                users.put("buttonType", "none");
                users.put("profileImg", "default");
                users.put("duplicatePoint", 1);

                HashMap<String, Object> records = new HashMap<>();
                records.put("id", firebaseAuth.getCurrentUser().getUid());
                records.put("point", 0);
                records.put("score", 0);

                HashMap<String, Object> medals = new HashMap<>();
                medals.put("id", firebaseAuth.getCurrentUser().getUid());
                medals.put("medalRich", "locked");
                medals.put("medalKing", "locked");
                medals.put("medalOneDay", "locked");
                medals.put("medalHighScore", "locked");
                medals.put("medalHighPoint", "locked");

                databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(users)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    progressIndicator.setVisibility(View.GONE);

                                    databaseReference.child("Records").child(firebaseAuth.getCurrentUser().getUid()).setValue(records);
                                    databaseReference.child("Medals").child(firebaseAuth.getCurrentUser().getUid()).setValue(medals);

                                    Snackbar snackbar = Snackbar.make(sign_up_layout, "Welcome " + username + "!", BaseTransientBottomBar.LENGTH_LONG);
                                    snackbar.setBackgroundTint(getResources().getColor(R.color.green));
                                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                                    snackbar.show();

                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                    finish();
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                progressIndicator.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(sign_up_layout, e.getMessage(), BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                snackbar.setActionTextColor(getResources().getColor(R.color.white));
                snackbar.show();
            }
        });
    }
}
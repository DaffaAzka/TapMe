package com.dest.tapme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dest.tapme.validates.Validate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private RelativeLayout sign_in_layout;
    private LinearProgressIndicator progressIndicator;
    private TextInputLayout et_email, et_password;
    private Button btn_sign_in;

    private FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sign_in_layout = findViewById(R.id.sign_in_layout);
        progressIndicator = findViewById(R.id.sign_in_progress_indicator);

        et_email = findViewById(R.id.et_sign_in_email);
        et_password = findViewById(R.id.et_sign_in_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressIndicator.setVisibility(View.VISIBLE);

                String txt_email = et_email.getEditText().getText().toString();
                String txt_password = et_password.getEditText().getText().toString();

                String messages = Validate.signUpOrSignIn(null, txt_email, txt_password, null);

                if (messages != null) {
                    progressIndicator.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(sign_in_layout, messages, BaseTransientBottomBar.LENGTH_LONG);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();
                } else {
                    signInUser(txt_email, txt_password);
                }
            }
        });
    }

    private void signInUser(String txt_email, String txt_password) {
        progressDialog.setMessage("Please wait!");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    progressIndicator.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(sign_in_layout, "Login Berhasil!", BaseTransientBottomBar.LENGTH_LONG);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.green));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.show();

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    finish();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                progressIndicator.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(sign_in_layout, e.getMessage(), BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                snackbar.setActionTextColor(getResources().getColor(R.color.white));
                snackbar.show();
            }
        });
    }
}
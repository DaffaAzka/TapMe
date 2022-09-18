package com.dest.tapme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dest.tapme.models.User;
import com.dest.tapme.utils.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    TextInputLayout et_edit_username;
    CircleImageView img_edit_display_profile_picture;
    ImageView img_edit_profile_picture, img_my_save;
    RelativeLayout edit_profile_layout;

    Uri imageUri;
    String imageUrl;
    StorageTask storageTask;
    StorageReference storageReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        et_edit_username = findViewById(R.id.et_edit_username);

        img_edit_display_profile_picture = findViewById(R.id.img_edit_display_profile_picture);

        img_edit_profile_picture = findViewById(R.id.img_edit_profile_picture);
        img_my_save = findViewById(R.id.img_my_save);

        edit_profile_layout = findViewById(R.id.edit_profile_layout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance(Util.storageDBLink())
                .getReference().child("Uploads").child("ImageProfile");

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                et_edit_username.getEditText().setText(user.getUsername());

                if (user.getProfileImg().equals("default")) {
                    img_edit_display_profile_picture.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Picasso.get().load(user.getProfileImg()).into(img_edit_display_profile_picture);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        img_edit_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE).start(EditProfileActivity.this);
            }
        });

        img_my_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", et_edit_username.getEditText().getText().toString());

        FirebaseDatabase.getInstance(Util.realtimeDBLink())
                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);

        startActivity(new Intent(EditProfileActivity.this, MyProfileActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            uploadImg();
        } else {
            Snackbar snackbar = Snackbar.make(edit_profile_layout, "Something wrong!", BaseTransientBottomBar.LENGTH_LONG);
            snackbar.setBackgroundTint(getResources().getColor(R.color.red));
            snackbar.setActionTextColor(getResources().getColor(R.color.white));
            snackbar.show();
        }
    }

    private void uploadImg() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null) {

            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            storageTask = fileRef.putFile(imageUri);

            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        imageUrl = downloadUri.toString();

                        FirebaseDatabase.getInstance(Util.realtimeDBLink())
                                .getReference().child("Users").child(firebaseUser.getUid()).child("profileImg").setValue(imageUrl);
                        progressDialog.dismiss();
                    } else {
                        Snackbar snackbar = Snackbar.make(edit_profile_layout, "Upload Failed!", BaseTransientBottomBar.LENGTH_LONG);
                        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        snackbar.show();
                    }
                }
            });

        } else {
            Snackbar snackbar = Snackbar.make(edit_profile_layout, "No image selected!", BaseTransientBottomBar.LENGTH_LONG);
            snackbar.setBackgroundTint(getResources().getColor(R.color.red));
            snackbar.setActionTextColor(getResources().getColor(R.color.white));
            snackbar.show();
        }
    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }
}
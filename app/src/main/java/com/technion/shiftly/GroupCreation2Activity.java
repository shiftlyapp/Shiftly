package com.technion.shiftly;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.abdularis.civ.CircleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GroupCreation2Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private UploadTask uploadTask;
    private Uri mImageUri;
    private CircleImageView circleImageView;
    private byte[] compressed_data;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            LottieAnimationView upload_anim = findViewById(R.id.upload_anim);
            upload_anim.setVisibility(View.GONE);
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                ImageView group_pic_baseline = (ImageView) findViewById(R.id.group_image);
                Bitmap compressed_bitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                compressed_bitmap.compress(Bitmap.CompressFormat.PNG,Constants.COMPRESSION_QUALITY, stream);
                byte[] byteArray = stream.toByteArray();
                compressed_bitmap.recycle();
                group_pic_baseline.setImageBitmap(bitmap);
                uploadToStorage(byteArray);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void compressImage() {
        circleImageView.setDrawingCacheEnabled(true);
        circleImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) circleImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESSION_QUALITY, baos);
        compressed_data = baos.toByteArray();
    }

    private void uploadToStorage(byte[] compressed_bitmap) {
        uploadTask = mStorageRef.putBytes(compressed_bitmap);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
               // mCustomSnackbar.show(getApplicationContext(),view,"Upload fail",0);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_2);
        mAuth = FirebaseAuth.getInstance();
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_2);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("/group_pics/"+"kjkj");

        circleImageView = findViewById(R.id.group_image);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        Button continue_button = findViewById(R.id.continue_button_group_creation_2);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent options_config_intent = new Intent(getApplicationContext(), GroupCreation25Activity.class);
                String group_name = getIntent().getExtras().getString("GROUP_NAME");
                options_config_intent.putExtra("GROUP_NAME", group_name);
                startActivity(options_config_intent);
                finish();
            }
        });

    }
}
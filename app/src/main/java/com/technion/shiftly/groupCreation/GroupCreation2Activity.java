package com.technion.shiftly.groupCreation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.abdularis.civ.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.technion.shiftly.R;
import com.technion.shiftly.entry.LoginActivity;
import com.technion.shiftly.utility.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// The second activity of the group creation process.
// In this activity the future admin sets the group icon.

public class GroupCreation2Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Uri mImageUri;
    private CircleImageView circleImageView;
    private byte[] compressed_group_byte_array;

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
        startActivityForResult(Intent.createChooser(intent, "Select Group Picture"), Constants.PICK_IMAGE_REQUEST);
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
                Bitmap compressed_bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                compressed_bitmap.compress(Bitmap.CompressFormat.PNG, Constants.COMPRESSION_QUALITY, stream);
                compressed_group_byte_array = stream.toByteArray();
                group_pic_baseline.setImageBitmap(bitmap);
                compressed_bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                Intent options_config_intent = new Intent(getApplicationContext(), GroupCreation3Activity.class);
                String group_name = getIntent().getExtras().getString("GROUP_NAME");
                options_config_intent.putExtra("GROUP_NAME", group_name);
                if (compressed_group_byte_array != null) {
                    options_config_intent.putExtra("GROUP_PICTURE", compressed_group_byte_array);
                }
                startActivity(options_config_intent);
                finish();
            }
        });

    }
}
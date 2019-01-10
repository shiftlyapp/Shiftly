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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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

public class GroupCreation25Activity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_25);
        mAuth = FirebaseAuth.getInstance();
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_25);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mStorage = FirebaseStorage.getInstance();

        // Setting the spinners with their adapters
        Spinner days_spinner = findViewById(R.id.days_num_spinner);
        ArrayAdapter<CharSequence> days_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.days_num, android.R.layout.simple_spinner_item);
        days_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days_spinner.setAdapter(days_spinner_adapter);

        Spinner shifts_per_day_spinner = findViewById(R.id.shifts_per_day_num_spinner);
        ArrayAdapter<CharSequence> shifts_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shifts_per_day, android.R.layout.simple_spinner_item);
        shifts_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shifts_per_day_spinner.setAdapter(shifts_spinner_adapter);

        Spinner employees_per_shift_spinner = findViewById(R.id.employees_per_shift_num_spinner);
        ArrayAdapter<CharSequence> employees_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.employees_per_shift, android.R.layout.simple_spinner_item);
        employees_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employees_per_shift_spinner.setAdapter(employees_spinner_adapter);


        Button apply_button = findViewById(R.id.continue_button);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timeslots_config_intent = new Intent(getApplicationContext(), GroupCreation3Activity.class);
                String group_name = getIntent().getExtras().getString("GROUP_NAME");

                final Spinner days_num_spinner = findViewById(R.id.days_num_spinner);
                Long days_num = Long.parseLong(days_num_spinner.getSelectedItem().toString());

                final Spinner shifts_num_spinner = findViewById(R.id.shifts_per_day_num_spinner);
                Long shifts_per_day = Long.parseLong(shifts_num_spinner.getSelectedItem().toString());

                final Spinner employees_per_shift_num_spinner = findViewById(R.id.employees_per_shift_num_spinner);
                Long employees_per_shift = Long.parseLong(employees_per_shift_num_spinner.getSelectedItem().toString());

                timeslots_config_intent.putExtra("GROUP_NAME", group_name);
                timeslots_config_intent.putExtra("DAYS_NUM", days_num);
                timeslots_config_intent.putExtra("SHIFTS_PER_DAY", shifts_per_day);
                timeslots_config_intent.putExtra("EMPLOYEES_PER_SHIFT", employees_per_shift);
                startActivity(timeslots_config_intent);
                finish();
            }
        });

    }
}
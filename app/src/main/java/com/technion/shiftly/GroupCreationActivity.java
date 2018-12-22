package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class GroupCreationActivity extends AppCompatActivity {

    private String UUID_code;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String user_first_name, user_last_name;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(GroupCreationActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);
        mAuth = FirebaseAuth.getInstance();
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create));
        TextView uuid = findViewById(R.id.group_code);
        UUID_code = UUID.randomUUID().toString().replace("-", "");
        uuid.setText(UUID_code);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference().child(("Users"));
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(currentUser.getUid()).getValue(User.class);
                user_first_name = user.getFirstname();
                user_last_name = user.getLastname();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ImageView share = findViewById(R.id.share_image);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String group_name = findViewById(R.id.group_name_edittext).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction((Intent.ACTION_SEND));
                String inv_str = "Hi!" + user_first_name + " " + user_last_name + " would like to invite you to use Shiftly! - The best app for managing your shifts at work.\n\nGroup name: " + group_name + ".\nYour unique code to join the group is: " + UUID_code + "\n\nWe hope to see you there!";
                sendIntent.putExtra(Intent.EXTRA_TEXT, inv_str);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        Button create_button = findViewById(R.id.create_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TimeslotsConfigActivity.class);
                startActivity(intent);
            }
        });
    }
}
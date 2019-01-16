package com.technion.shiftly;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupCreation3Activity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), GroupListsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("FRAGMENT_TO_LOAD",Constants.GROUPS_I_MANAGE_FRAGMENT);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_3);
        Toolbar mainToolbar = findViewById(R.id.group_creation_confirm_toolbar_3);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Bundle extras = getIntent().getExtras();
        final String group_name = extras.getString("GROUP_NAME");
        TextView signup_text = findViewById(R.id.signup_header);
        final Resources res = getResources();
        signup_text.setText(String.format(res.getString(R.string.group_create_succeed), group_name));
        MediaPlayer success_sound = MediaPlayer.create(this, R.raw.success);
        success_sound.start();

        LottieAnimationView done_animation = findViewById(R.id.success_img);
        done_animation.setSpeed(0.5f);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String admin_UID = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mGroupRef = database.getReference().child(("Groups"));

        final String group_UID = mGroupRef.push().getKey();
        Long days_num = extras.getLong("DAYS_NUM");
        Long shifts_per_day = extras.getLong("SHIFTS_PER_DAY");
        Long employees_per_shift = extras.getLong("EMPLOYEES_PER_SHIFT");

        final Group group = new Group(admin_UID, group_name, 0L,
                days_num, shifts_per_day, employees_per_shift);
        mGroupRef.child(group_UID).setValue(group);

        EditText group_code_edittext = findViewById(R.id.group_code);
        group_code_edittext.setText(group_UID);
        final String message_share_group_code = res.getString(R.string.message1_share_group_code) + " " +
                group_name + " " + res.getString(R.string.message2_share_group_code) + " " + group_UID + "\n\n" +
                res.getString(R.string.message3_share_group_code);

        // Whatsapp sharing
        ImageView whatsapp_share = findViewById(R.id.whatsapp_share);
        whatsapp_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsapp_intent = new Intent(Intent.ACTION_SEND);
                whatsapp_intent.setType("text/plain");
                whatsapp_intent.setPackage("com.whatsapp");

                whatsapp_intent.putExtra(Intent.EXTRA_TEXT, message_share_group_code);
                try {
                    startActivity(whatsapp_intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(view.getContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Email sharing
        ImageView email_share = findViewById(R.id.email_share);
        email_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email_intent = new Intent(Intent.ACTION_SEND);
                email_intent.setType("text/html");
                email_intent.putExtra(Intent.EXTRA_EMAIL, "");
                email_intent.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.email_subject_message_share_group_code));
                email_intent.putExtra(Intent.EXTRA_TEXT, message_share_group_code);
                startActivity(Intent.createChooser(email_intent, "Send Email"));
            }
        });

        // Text message sharing
        ImageView sms_share = findViewById(R.id.sms_share);
        sms_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 1);
                String sms_number = null;
                Uri uri1 = intent.getData();
                Cursor cursor = getContentResolver().query(uri1, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    sms_number = cursor.getString(phoneIndex);
                }
                cursor.close();
                Uri uri = Uri.parse("smsto:" + sms_number);
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, uri);
                sms_intent.putExtra("sms_body", message_share_group_code);
                startActivity(sms_intent);
            }
        });
    }
}
package com.technion.shiftly;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupCreation3Activity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,GroupListsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_3);
        Toolbar mainToolbar = findViewById(R.id.group_creation_confirm_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        @SuppressWarnings("unchecked")
        List<Map<String, Boolean>> timeslots = (List<Map<String, Boolean>>) getIntent().getSerializableExtra("TIMESLOTS_ARRAY");

        Bundle extras = getIntent().getExtras();
        String group_name = extras.getString("GROUP_NAME");
        TextView signup_text = findViewById(R.id.signup_header);
        Resources res = getResources();
        signup_text.setText(String.format(res.getString(R.string.group_create_succeed), group_name));

        ImageView success = findViewById(R.id.success_img);
        ScaleAnimation t = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        t.setDuration((long) 750);
        t.setRepeatCount(0);
        success.startAnimation(t);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String admin_UID = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mGroupRef = database.getReference().child(("Groups"));

        String group_UID = mGroupRef.push().getKey();
        Group group = new Group(admin_UID, group_name, 0L);
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = null;
        ArrayList<ArrayList<HashMap<String, Boolean>>> schedule = null;
        group.setTimeslots(timeslots);
        group.setOptions(options);
        group.setSchedule(schedule);
        mGroupRef.child(group_UID).setValue(group);

        EditText group_code_edittext = findViewById(R.id.group_code);
        group_code_edittext.setText(group_UID);

        ImageView whatsapp_share = findViewById(R.id.whatsapp_share);
        whatsapp_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(view.getContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView email_share = findViewById(R.id.email_share);
        email_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailintent = new Intent(Intent.ACTION_SEND);
                emailintent.setType("text/html");
                emailintent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
                emailintent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailintent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
                startActivity(Intent.createChooser(emailintent, "Send Email"));
            }
        });

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
                Intent smsintent = new Intent(Intent.ACTION_SENDTO, uri);
                smsintent.putExtra("sms_body", "Here you can set the SMS text to be sent");
                startActivity(smsintent);
            }
        });
    }
}
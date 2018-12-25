package com.technion.shiftly;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class GroupCreationActivityConfirm extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String user_first_name, user_last_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_confirm);
        mAuth = FirebaseAuth.getInstance();
        Toolbar mainToolbar = findViewById(R.id.group_creation_confirm_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView uuid = findViewById(R.id.group_code);
        String UUID_code = UUID.randomUUID().toString().replace("-", "");
        uuid.setText(UUID_code);
        uuid.setFocusable(false);

        ImageView success = findViewById(R.id.success_img);
        ScaleAnimation t = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        t.setDuration((long) 750);
        t.setRepeatCount(0);
        success.startAnimation(t);

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
package com.technion.shiftly.groupCreation;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technion.shiftly.R;
import com.technion.shiftly.dataTypes.Group;
import com.technion.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftly.utility.Constants;

import static com.technion.shiftly.utility.Constants.SHOW_LOADING_ANIMATION;

public class GroupCreation4Activity extends AppCompatActivity {

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private DatabaseReference mGroupRef;
    private UploadTask uploadTask;

    private Toolbar mainToolbar;
    private TextView signup_text, share_with_friends_txt;
    private LottieAnimationView done_animation, loading_animation;
    private ImageView whatsapp_share, email_share, sms_share, etc_share;
    private EditText group_code_edittext;
    private boolean back_pressed_locked;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!back_pressed_locked) {
            super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), GroupListsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("FRAGMENT_TO_LOAD", Constants.GROUPS_I_MANAGE_FRAGMENT);
            startActivity(intent);
        }
    }

    private void pushGroupToDatabase(byte[] compressed_bitmap, final String filename,
                                     final Long days_num, final Long shifts_per_day,
                                     final Long employees_per_shift, final String starting_hour, final Long shift_length, final String admin_UID,
                                     final String group_name, final String group_UID) {
        mStorageRef = mStorage.getReference().child("group_pics/" + filename + ".png");
        if (compressed_bitmap == null) {
            // No image upload
            handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
            Group group = new Group(admin_UID, group_name, 0L, days_num, shifts_per_day, employees_per_shift, starting_hour, shift_length, "none");
            mGroupRef.child(group_UID).setValue(group);
            MediaPlayer success_sound = MediaPlayer.create(getBaseContext(), R.raw.success);
            success_sound.start();
        } else {
            uploadTask = mStorageRef.putBytes(compressed_bitmap);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Image upload failed
                    handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
                    Group group = new Group(admin_UID, group_name, 0L, days_num, shifts_per_day, employees_per_shift, starting_hour, shift_length, "none");
                    mGroupRef.child(group_UID).setValue(group);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image upload succeed
                    mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
                            Group group = new Group(admin_UID, group_name, 0L, days_num, shifts_per_day, employees_per_shift, starting_hour, shift_length, uri.toString());
                            mGroupRef.child(group_UID).setValue(group);
                            MediaPlayer success_sound = MediaPlayer.create(getBaseContext(), R.raw.success);
                            success_sound.start();
                        }
                    });

                }
            });
        }
    }

    private void handleLoadingState(int state) {
        if (state == SHOW_LOADING_ANIMATION) {
            loading_animation.setVisibility(View.VISIBLE);
            back_pressed_locked = true;
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            signup_text.setVisibility(View.GONE);
            done_animation.setVisibility(View.GONE);
            email_share.setVisibility(View.GONE);
            whatsapp_share.setVisibility(View.GONE);
            sms_share.setVisibility(View.GONE);
            group_code_edittext.setVisibility(View.GONE);
            etc_share.setVisibility(View.GONE);
            share_with_friends_txt.setVisibility(View.GONE);
        } else {
            loading_animation.setVisibility(View.GONE);
            back_pressed_locked = false;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mainToolbar.setEnabled(true);
            signup_text.setVisibility(View.VISIBLE);
            done_animation.setVisibility(View.VISIBLE);
            done_animation.playAnimation();
            email_share.setVisibility(View.VISIBLE);
            whatsapp_share.setVisibility(View.VISIBLE);
            sms_share.setVisibility(View.VISIBLE);
            group_code_edittext.setVisibility(View.VISIBLE);
            etc_share.setVisibility(View.VISIBLE);
            share_with_friends_txt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_4);
        // Get views -------------------------------------------------------------------------------
        mainToolbar = findViewById(R.id.group_creation_confirm_toolbar_3);
        loading_animation = findViewById(R.id.loading_icon_creation);
        done_animation = findViewById(R.id.success_img);
        whatsapp_share = findViewById(R.id.whatsapp_share);
        email_share = findViewById(R.id.email_share);
        sms_share = findViewById(R.id.sms_share);
        etc_share = findViewById(R.id.etc_share);
        group_code_edittext = findViewById(R.id.group_code);
        signup_text = findViewById(R.id.signup_header);
        share_with_friends_txt = findViewById(R.id.share_with_friends_txt);
        //------------------------------------------------------------------------------------------
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // -----------------------------------------------------------------------------------------
        handleLoadingState(SHOW_LOADING_ANIMATION);
        // -----------------------------------------------------------------------------------------
        final Resources res = getResources();
        Bundle extras = getIntent().getExtras();
        done_animation.setSpeed(0.5f);
        // ------------------------- Firebase instances --------------------------------------------
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mGroupRef = database.getReference().child(("Groups"));
        // -----------------------------------------------------------------------------------------
        String group_UID = mGroupRef.push().getKey();
        String group_name = extras.getString("GROUP_NAME");
        byte[] group_pic_array = extras.getByteArray("GROUP_PICTURE");
        String admin_UID = currentUser.getUid();
        Long days_num = extras.getLong("DAYS_NUM");
        Long shifts_per_day = extras.getLong("SHIFTS_PER_DAY");
        Long employees_per_shift = extras.getLong("EMPLOYEES_PER_SHIFT");
        String starting_hour = extras.getString("STARTING_HOUR");
        Long shift_length = extras.getLong("SHIFT_LEN");

        pushGroupToDatabase(group_pic_array, group_UID, days_num, shifts_per_day, employees_per_shift, starting_hour, shift_length, admin_UID, group_name, group_UID);
        signup_text.setText(String.format(res.getString(R.string.group_create_succeed), group_name));
        group_code_edittext.setText(group_UID);

        final String message_share_group_code = res.getString(R.string.message1_share_group_code) + " " +
                group_name + " " + res.getString(R.string.message2_share_group_code) + "\n\n" + group_UID + "\n\n" +
                res.getString(R.string.message3_share_group_code);

        // Whatsapp sharing
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
        // TODO: Broken - NEED TO FIX CODE
        sms_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("smsto:"));
                sendIntent.putExtra("sms_body", message_share_group_code);

                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
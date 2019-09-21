package com.technion.shiftlyapp.shiftly.groupCreation;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import static com.technion.shiftlyapp.shiftly.utility.Constants.SHOW_LOADING_ANIMATION;

// The fourth activity of the group creation process.
// In this activity the future admin gets the group code and can share it.
// In this activity the group was successfully created.

public class GroupCreation4Activity extends AppCompatActivity {

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private UploadTask uploadTask;

    private Toolbar mainToolbar;
    private TextView signup_text, share_with_friends_txt;
    private LottieAnimationView done_animation, loading_animation;
    private ImageView whatsapp_share, email_share, etc_share;
    private EditText group_code_edittext;
    private boolean back_pressed_locked;
    private ConstraintLayout mLayout;
    private CustomSnackbar mSnackbar;
    private Group group;
    private String group_UID;
    DataAccess dataAccess = new DataAccess();

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

    private void pushGroupToDatabase(byte[] compressed_bitmap) {
        String adminUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        group.setAdmin(adminUid);
        group_UID = dataAccess.addGroup(group);

        if (compressed_bitmap == null) {
            // No image upload
            handleLoadingState(Constants.HIDE_LOADING_ANIMATION);

            MediaPlayer success_sound = MediaPlayer.create(getBaseContext(), R.raw.success);
            success_sound.start();
        } else {
            mStorageRef = mStorage.getReference().child("group_pics/" + group_UID + ".png");
            uploadTask = mStorageRef.putBytes(compressed_bitmap);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Image upload failed
                    handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image upload succeed
                    mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            handleLoadingState(Constants.HIDE_LOADING_ANIMATION);

                            group.setGroup_icon_url(uri.toString());
                            dataAccess.updateGroup(group_UID, group);

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
        loading_animation.setScale(Constants.LOADING_ANIM_SCALE);
        mLayout = (ConstraintLayout) findViewById(R.id.anim_bg);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);

        done_animation = findViewById(R.id.success_img);
        whatsapp_share = findViewById(R.id.whatsapp_share);
        email_share = findViewById(R.id.email_share);
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
        mStorage = FirebaseStorage.getInstance();
        // ------------------------------ Group upload ---------------------------------------------
        group = getIntent().getExtras().getParcelable("GROUP");

        byte[] group_pic_array = extras.getByteArray("GROUP_PICTURE");

        pushGroupToDatabase(group_pic_array);
        signup_text.setText(String.format(res.getString(R.string.group_create_succeed), group.getGroup_name()));
        group_code_edittext.setText(group_UID);
        group_code_edittext.setInputType(InputType.TYPE_NULL);
        group_code_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("group code", group_code_edittext.getText());
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(GroupCreation4Activity.this, R.string.copy_group_code_text, Toast.LENGTH_LONG).show();
            }
        });

        final String message_share_group_code = res.getString(R.string.message1_share_group_code) + " " +
                group.getGroup_name() + " " + res.getString(R.string.message2_share_group_code) + "\n\n" + group_UID + "\n\n" +
                res.getString(R.string.message3_share_group_code);

        // Whatsapp sharing
        setWhatsappSharing(message_share_group_code);
        // Email sharing
        setEmailSharing(res, message_share_group_code);
        // Other sharing
        setOtherSharing(res, message_share_group_code);
    }

    private void setOtherSharing(final Resources res, final String message_share_group_code) {
        etc_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent etc_intent = new Intent(Intent.ACTION_SEND);
                etc_intent.setType("text/html");
                etc_intent.putExtra(Intent.EXTRA_EMAIL, "");
                etc_intent.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.email_subject_message_share_group_code));
                etc_intent.putExtra(Intent.EXTRA_TEXT, message_share_group_code);
                startActivity(Intent.createChooser(etc_intent, "Share"));
            }
        });
    }

    private void setEmailSharing(final Resources res, final String message_share_group_code) {
        email_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email_intent = new Intent(Intent.ACTION_SENDTO);

                email_intent.setData(Uri.parse("mailto:"));
                email_intent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                email_intent.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.email_subject_message_share_group_code));
                email_intent.putExtra(Intent.EXTRA_TEXT, message_share_group_code);

                try {
                    startActivity(email_intent);
                } catch (ActivityNotFoundException e) {
                    // In case no email app is available
                    mSnackbar.show(GroupCreation4Activity.this, mLayout, getResources().getString(R.string.no_email_app), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

    private void setWhatsappSharing(final String message_share_group_code) {
        whatsapp_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsapp_intent = new Intent(Intent.ACTION_SEND);
                whatsapp_intent.setType("text/plain");
                whatsapp_intent.setPackage("com.whatsapp");

                whatsapp_intent.putExtra(Intent.EXTRA_TEXT, message_share_group_code);
                try {
                    startActivity(whatsapp_intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(view.getContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
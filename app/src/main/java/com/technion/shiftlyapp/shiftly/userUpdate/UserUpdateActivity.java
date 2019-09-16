package com.technion.shiftlyapp.shiftly.userUpdate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import java.util.ArrayList;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class UserUpdateActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private EditText /*email_edt,*/ password_edt, firstname_edt, lastname_edt;
    private DatabaseReference databaseRef;
    private String user_id;
    private ArrayList<String> groupsUserIsMemberOf;

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) UserUpdateActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void setupParent(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView);
            }
        }
    }

    private void updateUI() {
        finish();
        startActivity(new Intent(getApplicationContext(), GroupListsActivity.class));
    }

    private void runAnimation() {
        AnimationDrawable animationDrawable = (AnimationDrawable) mLayout.getBackground();
        animationDrawable.setEnterFadeDuration(Constants.ANIM_DURATION);
        animationDrawable.setExitFadeDuration(Constants.ANIM_DURATION);
        animationDrawable.start();
        ScaleAnimation t = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        t.setDuration((long) Constants.ANIM_DURATION);
        t.setRepeatCount(Constants.SCALE_ANIMATION_REPEAT_COUNT);
        LottieAnimationView signup_img = (LottieAnimationView) findViewById(R.id.user_update_pic);
        signup_img.startAnimation(t);
        findViewById(R.id.user_update_header).startAnimation(t);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);
        mLayout = (ConstraintLayout) findViewById(R.id.user_update_cl);
        setupParent(mLayout);
        runAnimation();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        addSignUpValidation(mAwesomeValidation, UserUpdateActivity.this);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user_id = user.getUid();

        password_edt = findViewById(R.id.user_update_password_edittext);
        firstname_edt = findViewById(R.id.user_update_firstname_edittext);
        lastname_edt = findViewById(R.id.user_update_lastname_edittext);

        // Get user details to present in the initial form
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        loadUserDetails();

        findViewById(R.id.user_update_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CustomSnackbar snackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);

                if (mAwesomeValidation.validate()) {

                    updateUserDetails(user);

                    snackbar.show(UserUpdateActivity.this, mLayout,
                            getResources().getString(R.string.account_updated),
                            CustomSnackbar.SNACKBAR_SUCCESS ,Snackbar.LENGTH_SHORT);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    }, Constants.REDIRECTION_DELAY);
                } else {
                    snackbar.show(UserUpdateActivity.this, mLayout, "An error occurred",
                            CustomSnackbar.SNACKBAR_ERROR,Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

    private void updateUserDetails(FirebaseUser user) {
        final String newfirstname = firstname_edt.getText().toString();
        final String newlastname = lastname_edt.getText().toString();
        String newPassword = password_edt.getText().toString();

        // Change first and last names
        databaseRef.child("firstname").setValue(newfirstname);
        databaseRef.child("lastname").setValue(newlastname);

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        for (String groupId : groupsUserIsMemberOf) {
            databaseRef.child(groupId).child("members").child(user_id)
                    .setValue(String.format("%s %s", newfirstname, newlastname));
        }

        // If password is empty, it means its unchanged
        if (!newPassword.isEmpty()) {
            user.updatePassword(newPassword);
        }
    }

    private void loadUserDetails() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstname").getValue().toString();
                String lastName = dataSnapshot.child("lastname").getValue().toString();
                groupsUserIsMemberOf = new ArrayList<>();
                if(dataSnapshot.child("groups").exists()) {
                    for (DataSnapshot group : dataSnapshot.child("groups").getChildren()) {
                        groupsUserIsMemberOf.add(group.getValue().toString());
                    }
                }

                firstname_edt.setText(firstName);
                lastname_edt.setText(lastName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public static void addSignUpValidation(AwesomeValidation mAwesomeValidation, Activity activity) {
        mAwesomeValidation.addValidation(activity, R.id.user_update_firstname_edittext,
                Constants.REGEX_NAME_VALIDATION, R.string.err_firstname);
        mAwesomeValidation.addValidation(activity, R.id.user_update_lastname_edittext,
                Constants.REGEX_NAME_VALIDATION, R.string.err_lastname);
        mAwesomeValidation.addValidation(activity, R.id.user_update_password_edittext,
                Constants.REGEX_PASSWORD_CHANGE_VALIDATION, R.string.err_password);
        mAwesomeValidation.addValidation(activity, R.id.user_update_confirm_password_edittext,
                R.id.user_update_password_edittext, R.string.err_password_confirmation);
    }
}
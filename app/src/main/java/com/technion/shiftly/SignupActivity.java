package com.technion.shiftly;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ConstraintLayout mLayout;
    private DatabaseReference mDatabase, newUser;
    private FirebaseUser mCurrentUser;
    private EditText email_edt, password_edt, firstname_edt, lastname_edt;

    private void showCustomSnakeBar(String msg) {
        final Snackbar mySnackbar = Snackbar.make(mLayout, msg, Snackbar.LENGTH_SHORT);
        final View snackbarView = mySnackbar.getView();
        final TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbarView.setBackgroundColor(getResources().getColor(R.color.text_color_primary));
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv.setTextSize(22);
        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.snackbar_error, 0, 0, 0);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(snackbarView, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                snackbarView.setVisibility(View.VISIBLE);
                snackbarView.setAlpha(0);
                mySnackbar.show();
            }
        });
        fadeIn.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mLayout = (ConstraintLayout) findViewById(R.id.signup_cl);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
        ImageView signup_img = (ImageView) findViewById(R.id.signup_pic);
        ScaleAnimation t = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        t.setDuration((long) 1000);
        t.setRepeatCount(0);
        signup_img.startAnimation(t);
        findViewById(R.id.signup_header).startAnimation(t);

        mAuth = FirebaseAuth.getInstance();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_firstname_edittext, "[a-zA-Z]+[[\\s][a-zA-Z]+]*", R.string.err_firstname);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_lastname_edittext, "[a-zA-Z]+[[\\s][a-zA-Z]+]*", R.string.err_lastname);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_email_edittext, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        String regexPassword = "^(?!.*\\s).{6,14}$";
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_password_edittext, regexPassword, R.string.err_password);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_confirm_password_edittext, R.id.signup_password_edittext, R.string.err_password_confirmation);

        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    email_edt = findViewById(R.id.signup_email_edittext);
                    password_edt = findViewById(R.id.signup_password_edittext);
                    firstname_edt = findViewById(R.id.signup_firstname_edittext);
                    lastname_edt = findViewById(R.id.signup_lastname_edittext);
                    final String firstname = firstname_edt.getText().toString();
                    final String lastname = lastname_edt.getText().toString();
                    final String email = email_edt.getText().toString();
                    String password = password_edt.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        showCustomSnakeBar(getResources().getString(R.string.account_created));
                                        FirebaseDatabase database =  FirebaseDatabase.getInstance();
                                        FirebaseUser c_user =  mAuth.getCurrentUser();
                                        String userId = c_user.getUid();
                                        User user = new User(firstname, lastname, email);
                                        DatabaseReference mRef =  database.getReference().child("Users").child(userId);
                                        mRef.setValue(user);
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Account creation failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
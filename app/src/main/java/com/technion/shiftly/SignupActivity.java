package com.technion.shiftly;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.basgeekball.awesomevalidation.AwesomeValidation;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ConstraintLayout mLayout = (ConstraintLayout) findViewById(R.id.signup_cl);
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
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_firstname_edittext, "[a-zA-Z\\s]+", R.string.err_firstname);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_lastname_edittext, "[a-zA-Z\\s]+", R.string.err_lastname);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_email_edittext, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{6,14}";
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_password_edittext, regexPassword, R.string.err_password);
        mAwesomeValidation.addValidation(SignupActivity.this, R.id.signup_confirm_password_edittext, R.id.signup_password_edittext, R.string.err_password_confirmation);

        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.validate();
            }
        });

    }
}


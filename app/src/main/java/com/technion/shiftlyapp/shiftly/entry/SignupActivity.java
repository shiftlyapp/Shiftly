package com.technion.shiftlyapp.shiftly.entry;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.User;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ConstraintLayout mLayout;
    private EditText email_edt, password_edt, firstname_edt, lastname_edt;

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) SignupActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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

    private void pushUserIntoDatabase(String firstname, String lastname, String email) {
        User user = new User(firstname, lastname, email);

        DataAccess dataAccess = new DataAccess();
        dataAccess.addUser(user);
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
        LottieAnimationView signup_img = (LottieAnimationView) findViewById(R.id.signup_pic);
        signup_img.startAnimation(t);
        findViewById(R.id.signup_header).startAnimation(t);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mLayout = (ConstraintLayout) findViewById(R.id.signup_cl);
        setupParent(mLayout);
        runAnimation();

        mAuth = FirebaseAuth.getInstance();

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        addSignUpValidation(mAwesomeValidation, SignupActivity.this);

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
                                    CustomSnackbar snackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
                                    if (task.isSuccessful()) {
                                        pushUserIntoDatabase(firstname, lastname, email);
                                        snackbar.show(SignupActivity.this, mLayout, getResources().getString(R.string.account_created),CustomSnackbar.SNACKBAR_SUCCESS ,Snackbar.LENGTH_SHORT);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateUI();
                                            }
                                        }, Constants.REDIRECTION_DELAY);
                                    } else {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            snackbar.show(SignupActivity.this, mLayout, getResources().getString(R.string.err_invalid_email_2), CustomSnackbar.SNACKBAR_ERROR,Snackbar.LENGTH_SHORT);
                                        } catch (Exception e) {
                                            snackbar.show(SignupActivity.this, mLayout, task.getException().toString(), CustomSnackbar.SNACKBAR_ERROR,Snackbar.LENGTH_SHORT);
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    public static void addSignUpValidation(AwesomeValidation mAwesomeValidation, Activity activity) {
        mAwesomeValidation.addValidation(activity, R.id.signup_firstname_edittext, Constants.REGEX_NAME_VALIDATION, R.string.err_firstname);
        mAwesomeValidation.addValidation(activity, R.id.signup_lastname_edittext, Constants.REGEX_NAME_VALIDATION, R.string.err_lastname);
        mAwesomeValidation.addValidation(activity, R.id.signup_email_edittext, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email);
        mAwesomeValidation.addValidation(activity, R.id.signup_password_edittext, Constants.REGEX_PASSWORD_VALIDATION, R.string.err_password);
        mAwesomeValidation.addValidation(activity, R.id.signup_confirm_password_edittext, R.id.signup_password_edittext, R.string.err_password_confirmation);

    }
}
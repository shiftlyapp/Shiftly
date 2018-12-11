package com.technion.shiftly;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ConstraintLayout mLayout;
    private EditText email_edt, password_edt;
    private String sbError;

    private void showCustomSnakeBar(String error) {
        final Snackbar mySnackbar = Snackbar.make(mLayout, error, Snackbar.LENGTH_SHORT);
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
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mLayout = (ConstraintLayout)findViewById(R.id.anim_bg);
        ImageView logo = (ImageView) findViewById(R.id.shiftly_logo);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
        ImageView user_pic = (ImageView) findViewById(R.id.user_pic);
        TextView signup_txt = (TextView) findViewById(R.id.new_to_our_app);
        email_edt = findViewById(R.id.email_edittext);
        password_edt = findViewById(R.id.password_edittext);

        Animation bounce_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
        logo.startAnimation(bounce_anim);
        user_pic.startAnimation(bounce_anim);
        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.forgot_pass_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AwesomeValidation mAwesomeValidation_forgotpass = new AwesomeValidation(BASIC);
                mAwesomeValidation_forgotpass.addValidation(LoginActivity.this, R.id.email_edittext, android.util.Patterns.EMAIL_ADDRESS, R.string.err_login_email);
                if (mAwesomeValidation_forgotpass.validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.CustomAlertDialog);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email_edt.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sbError = getResources().getString(R.string.reset_email_sent);
                                            } else {
                                                sbError = getResources().getString(R.string.err_invalid_email);
                                            }
                                            showCustomSnakeBar(sbError);
                                        }
                                    });
                        }
                    });

                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    String b_message = getResources().getString(R.string.reset_pass_text) + email_edt.getText().toString();
                    ;
                    builder.setMessage(b_message)
                            .setTitle(R.string.reset_pass_title);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(LoginActivity.this, R.id.email_edittext, android.util.Patterns.EMAIL_ADDRESS, R.string.err_login_email);
        String regexPassword = "^(?=.{1,}$).*";
        mAwesomeValidation.addValidation(LoginActivity.this, R.id.password_edittext, regexPassword, R.string.login_err_password);

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    String password = password_edt.getText().toString();
                    String email = email_edt.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidUserException e) {
                                            sbError = getResources().getString(R.string.err_invalid_email);
                                            email_edt.requestFocus();
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            sbError = getResources().getString(R.string.err_invalid_pass);
                                            password_edt.requestFocus();
                                        } catch (FirebaseTooManyRequestsException e) {
                                            sbError = getResources().getString(R.string.err_login_attempts);
                                        } catch (Exception e) {
                                            sbError = task.getException().getMessage();
                                        }
                                        showCustomSnakeBar(sbError);
                                    }
                                }
                            });
                }
            }
        });
    }
}
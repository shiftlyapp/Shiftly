package com.technion.shiftly;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;
    private ConstraintLayout mLayout;
    private EditText email_edt, password_edt;
    private CustomSnackbar snackbar;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (currentUser != null || account != null) {
            Intent intent = new Intent(LoginActivity.this, GroupListsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.GOOGLE_LOGIN_SUCCESS) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately// ...
            }
        }
    }

    private void updateUI() {
        finish();
        startActivity(new Intent(getApplicationContext(), GroupListsActivity.class));
    }

    private void signInWithGoogle() {
        Intent signInWithGoogleIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInWithGoogleIntent, Constants.GOOGLE_LOGIN_SUCCESS);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            snackbar.show(LoginActivity.this, mLayout, getResources().getString(R.string.login_success), 0);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();
                                }
                            }, Constants.REDIRECTION_DELAY);
                        } else {
                            return;
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mLayout = (ConstraintLayout) findViewById(R.id.anim_bg);
        snackbar = new CustomSnackbar(18);
        ImageView logo = (ImageView) findViewById(R.id.shiftly_logo);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLayout.getBackground();
        animationDrawable.setEnterFadeDuration(Constants.ANIM_DURATION);
        animationDrawable.setExitFadeDuration(Constants.ANIM_DURATION);
        animationDrawable.start();
        ImageView user_pic = (ImageView) findViewById(R.id.user_pic);
        TextView signup_txt = (TextView) findViewById(R.id.new_to_our_app);
        findViewById(R.id.sign_in_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
        email_edt = findViewById(R.id.email_edittext);
        password_edt = findViewById(R.id.password_edittext);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
                                                snackbar.show(LoginActivity.this, mLayout, getResources().getString(R.string.reset_email_sent), 1);
                                            } else {
                                                snackbar.show(LoginActivity.this, mLayout, getResources().getString(R.string.err_invalid_email), 0);
                                            }
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
                                        currentUser = mAuth.getCurrentUser();
                                        snackbar.show(LoginActivity.this, mLayout, getResources().getString(R.string.login_success), 0);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateUI();
                                            }
                                        }, Constants.REDIRECTION_DELAY);
                                    } else {
                                        String sbError;
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
                                        snackbar.show(LoginActivity.this, mLayout, sbError, 0);
                                    }
                                }
                            });
                }
            }
        });
    }
}
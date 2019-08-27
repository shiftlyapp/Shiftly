package com.technion.shiftlyapp.shiftly.entry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataTypes.User;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;
    private ConstraintLayout mLayout;
    private EditText email_edt, password_edt;
    private CheckBox mLoginCheckBox;
    private CustomSnackbar mSnackbar;
    private CallbackManager mCallbackManager;
    private SharedPreferences prefs;

    private void goToMainActivity() {
        mSnackbar.show(LoginActivity.this, mLayout, getResources().getString(R.string.login_success), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_SHORT);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, Constants.REDIRECTION_DELAY);
    }

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
        loadPreferences();
    }

    private void loadPreferences() {
        if (prefs != null) {
            String email_pref = prefs.getString("EMAIL", "");
            String pwd_pref = prefs.getString("PWD", "");
            boolean checkbox_state = prefs.getBoolean("CHECKBOX_STATE", false);
            email_edt.setText(email_pref);
            password_edt.setText(pwd_pref);
            mLoginCheckBox.setChecked(checkbox_state);
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EMAIL", email_edt.getText().toString());
        editor.putString("PWD", password_edt.getText().toString());
        editor.putBoolean("CHECKBOX_STATE", true);
        editor.apply();
    }

    private void clearPreferences() {
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("EMAIL");
            editor.remove("PWD");
            editor.remove("CHECKBOX_STATE");
            editor.apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GOOGLE_LOGIN_SUCCESS) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }
    }

    private void updateUI() {
        Intent i = new Intent(getApplicationContext(), GroupListsActivity.class);
        i.putExtra("FRAGMENT_TO_LOAD", Constants.GROUPS_I_BELONG_FRAGMENT);
        startActivity(i);
        finish();
    }

    private void signInWithGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        Intent signInWithGoogleIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInWithGoogleIntent, Constants.GOOGLE_LOGIN_SUCCESS);
    }

    private void getFbDetails(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        JSONObject json = response.getJSONObject();
                        try {
                            if (json != null) {
                                String firstname = json.getString("first_name");
                                String lastname = json.getString("last_name");
                                String email = json.getString("email");
                                pushUserIntoDatabase(firstname, lastname, email);
                            }
                        } catch (JSONException e) {
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                getFbDetails(token);
                            }
                            updateUI();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void pushUserIntoDatabase(String firstname, String lastname, String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String userId = mCurrentUser.getUid();
        User user = new User(firstname, lastname, email);
        DatabaseReference mDatabase = database.getReference().child("Users").child(userId);
        mDatabase.setValue(user);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                String firstname = acct.getGivenName();
                                String lastname = acct.getFamilyName();
                                String email = acct.getEmail();
                                pushUserIntoDatabase(firstname, lastname, email);
                            }
                            goToMainActivity();
                        } else {
                            mSnackbar.show(LoginActivity.this, mLayout, task.getException().toString(), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                        }
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
        mLoginCheckBox = (CheckBox) findViewById(R.id.remember_login_checkbox);
        ImageView logo = (ImageView) findViewById(R.id.shiftly_logo);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLayout.getBackground();
        animationDrawable.setEnterFadeDuration(Constants.ANIM_DURATION);
        animationDrawable.setExitFadeDuration(Constants.ANIM_DURATION);
        animationDrawable.start();
        final LottieAnimationView clock_anim = findViewById(R.id.clock_anim);
        clock_anim.setSpeed(Constants.CLOCK_ANIM_SPEED);
        clock_anim.setScale(Constants.CLOCK_ANIM_SCALE);
        ImageButton fb_login = findViewById(R.id.facebook_login_button);
        mCallbackManager = CallbackManager.Factory.create();
        final LoginButton fb_button_hidden = findViewById(R.id.hidden_fb_button);
        fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb_button_hidden.performClick();
            }
        });
        fb_button_hidden.setPermissions(Arrays.asList("email", "public_profile"));
        fb_button_hidden.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,"Facebok Login Error"
                        ,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        fb_button_hidden.setSoundEffectsEnabled(false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ImageButton google_login = findViewById(R.id.google_login_button);
        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

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
                                                mSnackbar.show(LoginActivity.this, mLayout, getResources().getString(R.string.reset_email_sent), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_SHORT);
                                            } else {
                                                mSnackbar.show(LoginActivity.this, mLayout, getResources().getString(R.string.err_invalid_email), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
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
                                        if (mLoginCheckBox.isChecked()) {
                                            savePreferences();
                                        } else {
                                            clearPreferences();
                                        }
                                        goToMainActivity();
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
                                        mSnackbar.show(LoginActivity.this, mLayout, sbError, CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                                    }
                                }
                            });
                }
            }
        });
    }
}
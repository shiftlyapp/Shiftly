package com.technion.shiftly;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
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
    private DatabaseReference mDatabase, newUser;
    private FirebaseUser mCurrentUser;
    private EditText email_edt, password_edt, firstname_edt, lastname_edt;

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
                                        Toast.makeText(SignupActivity.this, "Account created Successfully.",
                                                Toast.LENGTH_SHORT).show();

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
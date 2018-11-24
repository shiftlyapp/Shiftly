package com.technion.shiftly;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView welcome_text = (TextView) findViewById(R.id.welcome_header);
        ImageView smiley = (ImageView)findViewById(R.id.smiley_pic);
        Animation fade_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
        welcome_text.startAnimation(fade_anim);
        smiley.startAnimation(fade_anim);
    }
}

package com.technion.shiftly;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageView logo = (ImageView)findViewById(R.id.shiftly_logo);
        ConstraintLayout mLayout = (ConstraintLayout)findViewById(R.id.anim_bg);
        AnimationDrawable animationDrawable = (AnimationDrawable)mLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
        ImageView user_pic = (ImageView)findViewById(R.id.user_pic);
        TextView signup_txt = (TextView)findViewById(R.id.new_to_our_app);
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
    }
}

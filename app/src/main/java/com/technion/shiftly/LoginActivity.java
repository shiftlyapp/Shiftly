package com.technion.shiftly;

import android.content.Intent;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView welcome_text = (TextView) findViewById(R.id.welcome_header);
        ImageView smiley = (ImageView)findViewById(R.id.smiley_pic);
        TextView signup_txt = (TextView)findViewById(R.id.new_to_our_app);
        Animation bounce_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
        welcome_text.startAnimation(bounce_anim);
        smiley.startAnimation(bounce_anim);
        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}

package com.example.musicplayermedia;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;


import android.os.Bundle;
import android.os.Handler;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    public static int SPLASH_SCREEN = 3000;
    TextView txtSlogan;
    ImageView logoPic, logoText;
    Animation bottomAni, topAni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();

        topAni = AnimationUtils.loadAnimation(this,R.anim.top_animtion);
        bottomAni = AnimationUtils.loadAnimation(this,R.anim.bottom_animtion);

        logoPic.setAnimation(topAni);
        logoText.setAnimation(bottomAni);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class );
            startActivity(intent);
            finish();
        },SPLASH_SCREEN);

    }
    private void AnhXa()
    {
        txtSlogan = findViewById(R.id.TextSlogan);
        logoPic = findViewById(R.id.LogoPic);
        logoText = findViewById(R.id.LogoMusic);
    }

}
package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        getSupportActionBar().hide();
        TextView timeView = findViewById(R.id.timeView);
        TextView bestTimeView = findViewById(R.id.bestTimeView);

        long time = getIntent().getLongExtra("TIME",0L);

        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000 * 60)) % 60);

        String timeFormat = String.format("%02d:%02d", minutes, seconds);

        timeView.setText("Time : " + timeFormat);

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        long highestScore = prefs.getLong("highestScore", 0);
        if (highestScore == 0 || highestScore > time){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("highestScore", time);
            editor.apply();
        }
        highestScore = prefs.getLong("highestScore", 0);

        int bestSeconds = (int) (highestScore / 1000) % 60 ;
        int bestMinutes = (int) ((highestScore / (1000 * 60)) % 60);

        timeFormat = String.format("%02d:%02d", bestMinutes, bestSeconds);
        bestTimeView.setText("Best Time : " + timeFormat);

        final ImageButton play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(GameOverActivity.this, MainActivity.class);
                GameOverActivity.this.startActivity(playIntent);
                finish();
            }
        });

    }
}

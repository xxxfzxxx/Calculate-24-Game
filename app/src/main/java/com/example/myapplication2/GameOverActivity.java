package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView timeView = findViewById(R.id.timeView);
        TextView bestTimeView = findViewById(R.id.bestTimeView);
        long time = getIntent().getLongExtra("TIME",0L);

        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000*60)) % 60);

        String timeString = String.format("%02d:%02d", minutes, seconds);

        timeView.setText("Time: " + timeString);

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        long bestScore = prefs.getLong("bestScore", 0);
        if (bestScore == 0 || bestScore > time){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("bestScore", time);
            editor.apply();
        }
        bestScore = prefs.getLong("bestScore", 0);

        int bestseconds = (int) (bestScore / 1000) % 60 ;
        int bestminutes = (int) ((bestScore / (1000*60)) % 60);

        timeString = String.format("%02d:%02d", bestminutes, bestseconds);
        bestTimeView.setText("Best Time: " + timeString);
    }

    protected void play(View view) {
        Intent myIntent = new Intent(GameOverActivity.this, MainActivity.class);
        GameOverActivity.this.startActivity(myIntent);
        finish();
    }
}

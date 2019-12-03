package com.example.myapplication2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import com.show.api.ShowApiRequest;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private int current_int;
    private String current_operation;
    private boolean lastClickInt;
    private ArrayList<Integer> active_numbers;
    private int numbersUsed;

    private long time;
    private Chronometer chronometer;
    private Handler handler = new Handler();
    public TextView hint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        long bestScore = prefs.getLong("bestScore", 0);
        int seconds = (int) (bestScore / 1000) % 60 ;
        int minutes = (int) ((bestScore / (1000*60)) % 60);

        TextView scoreView = findViewById(R.id.best_score);
        hint = findViewById(R.id.hint);
        String timeString = String.format("%02d:%02d", minutes, seconds);
        scoreView.setText(timeString);

        current_int = 9999;
        current_operation = "";
        numbersUsed = 0;
        lastClickInt = false;
        setActive_numbers();

        ImageButton button1 = findViewById(R.id.button1);
        ImageButton button2 = findViewById(R.id.button2);
        ImageButton button3 = findViewById(R.id.button3);
        ImageButton button4 = findViewById(R.id.button4);
        ImageButton plus = findViewById(R.id.plus);
        ImageButton minus = findViewById(R.id.minus);
        ImageButton multiply = findViewById(R.id.multiply);
        ImageButton divide = findViewById(R.id.divide);
        ImageButton clear = findViewById(R.id.clear);
        ImageButton skip = findViewById(R.id.skip);
        final ImageButton showHint = findViewById(R.id.showHint);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);
        clear.setOnClickListener(this);
        skip.setOnClickListener(this);

        showHint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(){
                    //在新线程中发送网络请求
                    public void run() {
                        String numbers = active_numbers.get(0) + "," + active_numbers.get(1) + "," + active_numbers.get(2) + "," + active_numbers.get(3);
                        String appid = "116982";
                        String secret = "1c729e5685ba43588d87b4c0874fb6cc";
                        String output = new ShowApiRequest("http://route.showapi.com/1023-1", appid, secret)
                                .addTextPara("numbers", numbers)
                                .addTextPara("score", "24")
                                .addTextPara("getAll", "true")
                                .addTextPara("getOthers", "false")
                                .post();
                        final String res = output.split(",")[4];
                        handler.post(new Thread(){
                            public void run() {
                                hint.setText(res);
                            }
                        });
                    }
                }.start();
            }
        });
        chronometer = findViewById(R.id.chronometer);
        chronometer.start();
    }

    @Override
    public void onClick(View view){
        TextView inputView = findViewById(R.id.input);
        switch (view.getId()){
            case R.id.button1:
                ImageButton button1 = findViewById(R.id.button1);
                clickIntegerButton(active_numbers.get(0), button1);
                break;
            case R.id.button2:
                ImageButton button2 = findViewById(R.id.button2);
                clickIntegerButton(active_numbers.get(1), button2);
                break;
            case R.id.button3:
                ImageButton button3 = findViewById(R.id.button3);
                clickIntegerButton(active_numbers.get(2), button3);
                break;
            case R.id.button4:
                ImageButton button4 = findViewById(R.id.button4);
                clickIntegerButton(active_numbers.get(3), button4);
                break;
            case R.id.plus:
                ImageButton button5 = findViewById(R.id.plus);
                clickOperator("+", button5);
                break;
            case R.id.minus:
                ImageButton button6 = findViewById(R.id.minus);
                clickOperator("-", button6);
                break;
            case R.id.multiply:
                ImageButton button7 = findViewById(R.id.multiply);
                clickOperator("x", button7);
                break;
            case R.id.divide:
                ImageButton button8 = findViewById(R.id.divide);
                clickOperator("/", button8);
                break;
            case R.id.clear:
                clear();
                break;
            case R.id.skip:
                resetFilters();
                setActive_numbers();
                current_operation = "";
                current_int = 9999;
                numbersUsed = 0;
                inputView.setText("");
                lastClickInt = false;
                enableButtons();
                chronometer.setBase(SystemClock.elapsedRealtime());
                hint.setText("");
                break;
        }
    }

    protected void clickIntegerButton(int i, View button){
        if (!lastClickInt){
            if (current_int == 9999){
                current_int = i;
            }
            if (current_operation.equals("+")) {
                current_int = current_int + i;
            } else if (current_operation.equals("-")) {
                current_int = current_int - i;
            } else if (current_operation.equals("x")) {
                current_int = current_int * i;
            } else if (current_operation.equals("/")) {
                current_int = current_int / i;
            }

            current_operation = "";
            lastClickInt = true;
            numbersUsed++;

            ((ImageButton)button).setColorFilter(Color.argb(150,200,200,200));
            button.setEnabled(false);
            setFormula();
            gameOver();
        }
    }

    protected void clickOperator(String s, View button){
        if (lastClickInt) {
            current_operation = s;
            lastClickInt = false;
            setFormula();
            button.setEnabled(true);
        }
    }

    protected void setActive_numbers() {
        ImageButton buttonOne = findViewById(R.id.button1);
        ImageButton buttonTwo = findViewById(R.id.button2);
        ImageButton buttonThree = findViewById(R.id.button3);
        ImageButton buttonFour = findViewById(R.id.button4);
        active_numbers = Util.getValidDigits();
        setButton(buttonOne, active_numbers.get(0));
        setButton(buttonTwo, active_numbers.get(1));
        setButton(buttonThree, active_numbers.get(2));
        setButton(buttonFour, active_numbers.get(3));
    }

    protected void setButton(View button, int i) {
        if (i == 1) {
            ((ImageButton) button).setImageResource(R.drawable.one);
        } else if (i == 2) {
            ((ImageButton) button).setImageResource(R.drawable.two);
        } else if (i == 3) {
            ((ImageButton) button).setImageResource(R.drawable.three);
        } else if (i == 4) {
            ((ImageButton) button).setImageResource(R.drawable.four);
        } else if (i == 5) {
            ((ImageButton) button).setImageResource(R.drawable.five);
        } else if (i == 6) {
            ((ImageButton) button).setImageResource(R.drawable.six);
        } else if (i == 7) {
            ((ImageButton) button).setImageResource(R.drawable.seven);
        } else if (i == 8) {
            ((ImageButton) button).setImageResource(R.drawable.eight);
        } else if (i == 9) {
            ((ImageButton) button).setImageResource(R.drawable.nine);
        } else if (i == 10) {
            ((ImageButton) button).setImageResource(R.drawable.ten);
        } else if (i == 11) {
            ((ImageButton) button).setImageResource(R.drawable.jack);
        } else if (i == 12) {
            ((ImageButton) button).setImageResource(R.drawable.queen);
        } else if (i == 13) {
            ((ImageButton) button).setImageResource(R.drawable.knight);
        }
    }

    protected void setFormula() {
        String typeAnswer = current_int + " " + current_operation;
        TextView inputView = findViewById(R.id.input);
        inputView.setText(typeAnswer);
    }

    protected void clear(){
        TextView inputView = findViewById(R.id.input);
        current_int = 9999;
        numbersUsed = 0;
        current_operation = "";
        lastClickInt = false;
        inputView.setText("");
        resetFilters();
        enableButtons();
    }

    protected void gameOver(){
        if (numbersUsed == 4) {
            if (current_int == 24) {
                time = SystemClock.elapsedRealtime() - chronometer.getBase();
                Intent intent = new Intent(this, GameOverActivity.class);
                intent.putExtra("TIME", time);
                startActivity(intent);
                finish();
            } else {
                clear();
            }
        }
    }

    protected void enableButtons(){
        findViewById(R.id.button1).setEnabled(true);
        findViewById(R.id.button2).setEnabled(true);
        findViewById(R.id.button3).setEnabled(true);
        findViewById(R.id.button4).setEnabled(true);
        findViewById(R.id.plus).setEnabled(true);
        findViewById(R.id.minus).setEnabled(true);
        findViewById(R.id.multiply).setEnabled(true);
        findViewById(R.id.divide).setEnabled(true);
    }

    protected void resetFilters(){
        ImageButton buttonOne = findViewById(R.id.button1);
        ImageButton buttonTwo = findViewById(R.id.button2);
        ImageButton buttonThree = findViewById(R.id.button3);
        ImageButton buttonFour = findViewById(R.id.button4);
        ImageButton buttonFive = findViewById(R.id.plus);
        ImageButton buttonSix = findViewById(R.id.minus);
        ImageButton buttonSeven = findViewById(R.id.multiply);
        ImageButton buttonEight = findViewById(R.id.divide);

        buttonOne.clearColorFilter();
        buttonTwo.clearColorFilter();
        buttonThree.clearColorFilter();
        buttonFour.clearColorFilter();
        buttonFive.clearColorFilter();
        buttonSix.clearColorFilter();
        buttonSeven.clearColorFilter();
        buttonEight.clearColorFilter();

    }

    protected void showHint() {

        active_numbers.get(0);
        active_numbers.get(1);
        active_numbers.get(2);
        active_numbers.get(3);
    }
}

package com.example.zkazino;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvReel1, tvReel2, tvReel3, tvResult;
    private Button btnSpin;
    private Random random = new Random();
    private boolean isSpinning = false;


    private String[] symbols = {"♣", "♥", "♦", "♠", "BAR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvReel1 = findViewById(R.id.tvReel1);
        tvReel2 = findViewById(R.id.tvReel2);
        tvReel3 = findViewById(R.id.tvReel3);
        tvResult = findViewById(R.id.tvResult);
        btnSpin = findViewById(R.id.btnSpin);


        btnSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSpinning) {
                    spin();
                }
            }
        });
    }

    private void spin() {
        isSpinning = true;
        btnSpin.setEnabled(false);
        tvResult.setText("");


        for (int i = 0; i < 10; i++) {
            final int delay = i * 100;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvReel1.setText(symbols[random.nextInt(symbols.length)]);
                    tvReel2.setText(symbols[random.nextInt(symbols.length)]);
                    tvReel3.setText(symbols[random.nextInt(symbols.length)]);
                }
            }, delay);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String result1 = symbols[random.nextInt(symbols.length)];
                String result2 = symbols[random.nextInt(symbols.length)];
                String result3 = symbols[random.nextInt(symbols.length)];

                tvReel1.setText(result1);
                tvReel2.setText(result2);
                tvReel3.setText(result3);

                checkWin(result1, result2, result3);

                isSpinning = false;
                btnSpin.setEnabled(true);
            }
        }, 1000);
    }

    private void checkWin(String r1, String r2, String r3) {

        if (r1.equals("BAR") && r2.equals("BAR") && r3.equals("BAR")) {
            tvResult.setText("ДЖЕКПОТ! ТРИ BAR");
        } else if (r1.equals(r2) && r2.equals(r3)) {
            tvResult.setText("ВЫИГРЫШ! Три одинаковых");
        } else if (r1.equals(r2) || r2.equals(r3) || r1.equals(r3)) {
            tvResult.setText("МАЛЕНЬКИЙ ВЫИГРЫШ! Два одинаковых");
        } else {
            tvResult.setText("Повезет в следующий раз");
        }
    }
}
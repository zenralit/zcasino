package com.example.zkazino;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;

    private TextView tvReel1, tvReel2, tvReel3, tvResult;
    private TextView rouletteResult, tvRouletteResult;
    private Button btnRed, btnBlack, btnGreen;
    private Button btnBetMinus, btnBetPlus, btnRouletteSpin;
    private TextView tvRouletteBet;

    private TextView tvBalance, tvUserEmail;
    private Button btnSpin;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;

    private TextView menuProfile, menuHistory, menuSettings, menuLogout;

    private Random random = new Random();
    private boolean isSpinning = false;
    private String[] symbols = {"🍒", "🍋", "⭐", "💎", "7️⃣"};

    private boolean isRouletteSpinning = false;
    private int selectedColor = 0;
    private int rouletteBet = 100;
    private int[] rouletteNumbers = {0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26};
    private int[] redNumbers = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};

    private int balance = 1000;
    private static final int SPIN_COST = 50;

    private void saveBalance() {
        userRef.update("balance", balance)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userRef = db.collection("users").document(user.getUid());

        initViews();
        setupListeners();
        loadUserData(user);
    }

    private void initViews() {
        tvReel1 = findViewById(R.id.tvReel1);
        tvReel2 = findViewById(R.id.tvReel2);
        tvReel3 = findViewById(R.id.tvReel3);
        tvResult = findViewById(R.id.tvResult);

        rouletteResult = findViewById(R.id.rouletteResult);
        tvRouletteResult = findViewById(R.id.tvRouletteResult);
        btnRed = findViewById(R.id.btnRed);
        btnBlack = findViewById(R.id.btnBlack);
        btnGreen = findViewById(R.id.btnGreen);
        btnBetMinus = findViewById(R.id.btnBetMinus);
        btnBetPlus = findViewById(R.id.btnBetPlus);
        btnRouletteSpin = findViewById(R.id.btnRouletteSpin);
        tvRouletteBet = findViewById(R.id.tvRouletteBet);

        tvBalance = findViewById(R.id.tvBalance);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnSpin = findViewById(R.id.btnSpin);
        btnMenu = findViewById(R.id.btnMenu);
        drawerLayout = findViewById(R.id.drawerLayout);

        menuProfile = findViewById(R.id.menuProfile);

        menuSettings = findViewById(R.id.menuSettings);
        menuLogout = findViewById(R.id.menuLogout);
    }

    private void setupListeners() {
        btnMenu.setOnClickListener(v -> drawerLayout.open());

        btnSpin.setOnClickListener(v -> {
            if (!isSpinning) {
                if (balance >= SPIN_COST) {
                    balance -= SPIN_COST;
                    updateBalance();
                    spinSlots();
                    saveBalance();
                } else {
                    Toast.makeText(this, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRed.setOnClickListener(v -> {
            selectedColor = 1;
            highlightColorButton(btnRed);
        });
        btnBlack.setOnClickListener(v -> {
            selectedColor = 2;
            highlightColorButton(btnBlack);
        });
        btnGreen.setOnClickListener(v -> {
            selectedColor = 3;
            highlightColorButton(btnGreen);
        });

        btnBetPlus.setOnClickListener(v -> {
            if (rouletteBet < 500 && balance >= rouletteBet + 50) {
                rouletteBet += 50;
                tvRouletteBet.setText("$ " + rouletteBet);
            }
        });
        btnBetMinus.setOnClickListener(v -> {
            if (rouletteBet > 50) {
                rouletteBet -= 50;
                tvRouletteBet.setText("$ " + rouletteBet);
            }
        });

        btnRouletteSpin.setOnClickListener(v -> {
            if (!isRouletteSpinning) {
                if (selectedColor == 0) {
                    Toast.makeText(this, "Выберите цвет!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (balance < rouletteBet) {
                    Toast.makeText(this, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                    return;
                }
                balance -= rouletteBet;
                updateBalance();
                spinRoulette();
                saveBalance();
            }
        });

        menuLogout.setOnClickListener(v -> logout());
        menuProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            drawerLayout.close();
        });

        menuSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Настройки в разработке", Toast.LENGTH_SHORT).show();
            drawerLayout.close();
        });
    }

    private void highlightColorButton(Button selected) {
        btnRed.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        btnBlack.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        btnGreen.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));

        if (selected == btnRed) {
            btnRed.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark));
        } else if (selected == btnBlack) {
            btnBlack.setBackgroundTintList(getColorStateList(android.R.color.black));
        } else if (selected == btnGreen) {
            btnGreen.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
        }
    }

    private void loadUserData(FirebaseUser user) {
        tvUserEmail.setText(user.getEmail());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long savedBalance = documentSnapshot.getLong("balance");
                if (savedBalance != null) {
                    balance = savedBalance.intValue();
                    updateBalance();
                }
            } else {
                saveBalance();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Ошибка загрузки баланса: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void spinSlots() {
        isSpinning = true;
        btnSpin.setEnabled(false);
        tvResult.setText("Крутим...");

        for (int i = 0; i < 15; i++) {
            final int delay = i * 80;
            new Handler().postDelayed(() -> {
                tvReel1.setText(symbols[random.nextInt(symbols.length)]);
                tvReel2.setText(symbols[random.nextInt(symbols.length)]);
                tvReel3.setText(symbols[random.nextInt(symbols.length)]);
            }, delay);
        }

        new Handler().postDelayed(() -> {
            String result1 = symbols[random.nextInt(symbols.length)];
            String result2 = symbols[random.nextInt(symbols.length)];
            String result3 = symbols[random.nextInt(symbols.length)];

            tvReel1.setText(result1);
            tvReel2.setText(result2);
            tvReel3.setText(result3);

            checkSlotWin(result1, result2, result3);

            isSpinning = false;
            btnSpin.setEnabled(true);
        }, 1300);
    }

    private void checkSlotWin(String r1, String r2, String r3) {
        int winAmount = 0;

        if (r1.equals("7️⃣") && r2.equals("7️⃣") && r3.equals("7️⃣")) {
            winAmount = 5000;
            tvResult.setText("ДЖЕКПОТ! 777! +$" + winAmount);
        } else if (r1.equals("💎") && r2.equals("💎") && r3.equals("💎")) {
            winAmount = 1000;
            tvResult.setText("ТРИ АЛМАЗА! +$" + winAmount);
        } else if (r1.equals(r2) && r2.equals(r3)) {
            winAmount = 200;
            tvResult.setText("ТРИ ОДИНАКОВЫХ! +$" + winAmount);
        } else if (r1.equals(r2) || r2.equals(r3) || r1.equals(r3)) {
            winAmount = 50;
            tvResult.setText("ДВА ОДИНАКОВЫХ! +$" + winAmount);
        } else {
            tvResult.setText("Попробуй ещё раз");
        }

        if (winAmount > 0) {
            int oldBalance = balance;
            balance += winAmount;
            updateBalance();
            saveBalance();
            saveBalanceWithHistory(oldBalance, winAmount);
        }
    }

    private void spinRoulette() {
        isRouletteSpinning = true;
        btnRouletteSpin.setEnabled(false);
        tvRouletteResult.setText("Крутим рулетку...");

        RotateAnimation rotate = new RotateAnimation(
                0, 720,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(2000);
        rotate.setFillAfter(true);

        new Handler().postDelayed(() -> {
            int finalNumber = rouletteNumbers[random.nextInt(rouletteNumbers.length)];
            rouletteResult.setText(String.valueOf(finalNumber));

            int resultColor = 0;
            if (finalNumber == 0) {
                resultColor = 0;
                rouletteResult.setBackgroundColor(getColor(android.R.color.holo_green_dark));
            } else if (isRedNumber(finalNumber)) {
                resultColor = 1;
                rouletteResult.setBackgroundColor(getColor(android.R.color.holo_red_dark));
            } else {
                resultColor = 2;
                rouletteResult.setBackgroundColor(getColor(android.R.color.black));
            }

            checkRouletteWin(finalNumber, resultColor);

            isRouletteSpinning = false;
            btnRouletteSpin.setEnabled(true);
        }, 2000);
    }

    private boolean isRedNumber(int number) {
        for (int red : redNumbers) {
            if (red == number) return true;
        }
        return false;
    }

    private void checkRouletteWin(int number, int resultColor) {
        int winAmount = 0;
        String resultText = "";

        if (resultColor == 0) {
            if (selectedColor == 3) {
                winAmount = rouletteBet * 35;
                resultText = "ЗЕРО! Выигрыш x35! +$" + winAmount;
            } else {
                resultText = "Зеро! Вы проиграли";
            }
        } else if (resultColor == 1) {
            if (selectedColor == 1) {
                winAmount = rouletteBet * 2;
                resultText = "КРАСНОЕ! Выигрыш x2! +$" + winAmount;
            } else {
                resultText = "Красное! Вы проиграли";
            }
        } else if (resultColor == 2) {
            if (selectedColor == 2) {
                winAmount = rouletteBet * 2;
                resultText = "ЧЁРНОЕ! Выигрыш x2! +$" + winAmount;
            } else {
                resultText = "Чёрное! Вы проиграли";
            }
        }

        tvRouletteResult.setText(resultText + " (выпало " + number + ")");

        if (winAmount > 0) {
            int oldBalance = balance;
            balance += winAmount;
            updateBalance();
            saveBalance();
            saveBalanceWithHistory(oldBalance, winAmount);
        }
    }

    private void updateBalance() {
        tvBalance.setText("$ " + balance);
    }

    private void saveBalanceWithHistory(int oldBalance, int winAmount) {
        userRef.update("balance", balance)
                .addOnSuccessListener(aVoid -> {
                    saveBalanceHistory(oldBalance, winAmount);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка сохранения баланса: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveBalanceHistory(int oldBalance, int winAmount) {
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("oldBalance", oldBalance);
        historyEntry.put("newBalance", balance);
        historyEntry.put("winAmount", winAmount);
        historyEntry.put("timestamp", System.currentTimeMillis());

        userRef.collection("balanceHistory")
                .add(historyEntry)
                .addOnSuccessListener(documentReference -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(this, "Выход выполнен", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
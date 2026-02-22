package com.example.zkazino;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;

    // UI элементы
    private TextView tvReel1, tvReel2, tvReel3, tvResult;
    private TextView tvBalance, tvUserEmail;
    private Button btnSpin;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;

    // Меню
    private TextView menuProfile, menuHistory, menuSettings, menuLogout;

    // Логика слотов
    private Random random = new Random();
    private boolean isSpinning = false;
    private String[] symbols = {"🍒", "🍋", "🍊", "⭐", "💎", "7️⃣"};

    // Баланс
    private int balance = 1000;
    private static final int SPIN_COST = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Проверка авторизации
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Ссылка на документ пользователя
        userRef = db.collection("users").document(user.getUid());

        // Инициализация UI
        initViews();
        setupListeners();

        // Загрузка данных пользователя
        loadUserData(user);
    }

    private void initViews() {
        tvReel1 = findViewById(R.id.tvReel1);
        tvReel2 = findViewById(R.id.tvReel2);
        tvReel3 = findViewById(R.id.tvReel3);
        tvResult = findViewById(R.id.tvResult);
        tvBalance = findViewById(R.id.tvBalance);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnSpin = findViewById(R.id.btnSpin);
        btnMenu = findViewById(R.id.btnMenu);
        drawerLayout = findViewById(R.id.drawerLayout);

        // Меню
        menuProfile = findViewById(R.id.menuProfile);
        menuHistory = findViewById(R.id.menuHistory);
        menuSettings = findViewById(R.id.menuSettings);
        menuLogout = findViewById(R.id.menuLogout);
    }

    private void setupListeners() {
        // Бургер меню
        btnMenu.setOnClickListener(v -> drawerLayout.open());

        // SPIN кнопка
        btnSpin.setOnClickListener(v -> {
            if (!isSpinning) {
                if (balance >= SPIN_COST) {
                    balance -= SPIN_COST;
                    updateBalance();
                    spin();
                } else {
                    Toast.makeText(this, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Пункты меню
        menuLogout.setOnClickListener(v -> logout());
        menuProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Профиль в разработке", Toast.LENGTH_SHORT).show();
            drawerLayout.close();
        });
        menuHistory.setOnClickListener(v -> {
            Toast.makeText(this, "История в разработке", Toast.LENGTH_SHORT).show();
            drawerLayout.close();
        });
        menuSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Настройки в разработке", Toast.LENGTH_SHORT).show();
            drawerLayout.close();
        });
    }

    private void loadUserData(FirebaseUser user) {
        tvUserEmail.setText(user.getEmail());

        // Можно загрузить баланс из Firestore
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long savedBalance = documentSnapshot.getLong("balance");
                if (savedBalance != null) {
                    balance = savedBalance.intValue();
                    updateBalance();
                }
            }
        });
    }

    private void spin() {
        isSpinning = true;
        btnSpin.setEnabled(false);
        tvResult.setText("🎰 Крутим...");

        // Анимация кручения
        for (int i = 0; i < 15; i++) {
            final int delay = i * 80;
            new Handler().postDelayed(() -> {
                tvReel1.setText(symbols[random.nextInt(symbols.length)]);
                tvReel2.setText(symbols[random.nextInt(symbols.length)]);
                tvReel3.setText(symbols[random.nextInt(symbols.length)]);
            }, delay);
        }

        // Финальный результат
        new Handler().postDelayed(() -> {
            String result1 = symbols[random.nextInt(symbols.length)];
            String result2 = symbols[random.nextInt(symbols.length)];
            String result3 = symbols[random.nextInt(symbols.length)];

            tvReel1.setText(result1);
            tvReel2.setText(result2);
            tvReel3.setText(result3);

            checkWin(result1, result2, result3);

            isSpinning = false;
            btnSpin.setEnabled(true);
        }, 1300);
    }

    private void checkWin(String r1, String r2, String r3) {
        int winAmount = 0;

        if (r1.equals("7️⃣") && r2.equals("7️⃣") && r3.equals("7️⃣")) {
            winAmount = 5000;
            tvResult.setText("🔥 ДЖЕКПОТ! 777! +$" + winAmount);
        } else if (r1.equals("💎") && r2.equals("💎") && r3.equals("💎")) {
            winAmount = 1000;
            tvResult.setText("💎 ТРИ АЛМАЗА! +$" + winAmount);
        } else if (r1.equals(r2) && r2.equals(r3)) {
            winAmount = 200;
            tvResult.setText("ТРИ ОДИНАКОВЫХ! +$" + winAmount);
        } else if (r1.equals(r2) || r2.equals(r3) || r1.equals(r3)) {
            winAmount = 50;
            tvResult.setText("ДВА ОДИНАКОВЫХ! +$" + winAmount);
        } else {
            tvResult.setText("❌ Попробуй ещё раз");
        }

        if (winAmount > 0) {
            balance += winAmount;
            updateBalance();
            saveBalance();
        }
    }

    private void updateBalance() {
        tvBalance.setText("$ " + balance);
    }

    private void saveBalance() {
        userRef.update("balance", balance);
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(this, "Выход выполнен", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
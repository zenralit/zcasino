package com.example.zkazino;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Переменные для View (элементы интерфейса)
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvCreateAccount, tvForgotPass;

    // Переменные для Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Инициализация Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Привязываем элементы из XML к коду
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPass = findViewById(R.id.tvForgotPass);

        // 3. Кнопка "Войти"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString();

                // Проверка на пустоту
                if (email.isEmpty()) {
                    etEmail.setError("Введите email");
                    return;
                }
                if (pass.isEmpty()) {
                    etPassword.setError("Введите пароль");
                    return;
                }

                // Вход через Firebase
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // Успешный вход
                                Toast.makeText(LoginActivity.this, "Вход успешен!", Toast.LENGTH_SHORT).show();

                                // Здесь можно перейти на главный экран
                                 startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                 finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Ошибка входа
                                Toast.makeText(LoginActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // 4. Кнопка "Нет аккаунта? Создайте его" -> Переход на регистрацию
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        // 5. Кнопка "Забыли пароль?" (заглушка)
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Функция восстановления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Проверка: если пользователь уже вошёл, не показываем экран логина
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Пользователь уже авторизован
             startActivity(new Intent(this, MainActivity.class));
             finish();
        }
    }
}
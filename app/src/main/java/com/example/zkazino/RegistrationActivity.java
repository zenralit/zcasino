package com.example.zkazino;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout; // <--- Добавь этот импорт
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    EditText etSurname, etName, etPatronymic, etDob, etEmailPhone, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvHaveAccount, tvCloseHint;

    LinearLayout cardPasswordHint;

    CheckBox cbAgreement, cbPersonal, cbResponsibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvHaveAccount = findViewById(R.id.tvHaveAccount);

        cardPasswordHint = findViewById(R.id.cardPasswordHint);

        tvCloseHint = findViewById(R.id.tvCloseHint);

        cbAgreement = findViewById(R.id.cbAgreement);
        cbPersonal = findViewById(R.id.cbPersonal);
        cbResponsibility = findViewById(R.id.cbResponsibility);


        tvCloseHint.setOnClickListener(v -> cardPasswordHint.setVisibility(View.GONE));


        btnRegister.setOnClickListener(v -> {
            String pass = etPassword.getText().toString();
            String confirmPass = etConfirmPassword.getText().toString();

            //  длина > 6
            if (pass.length() <= 6) {
                // попап
                cardPasswordHint.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Пароль слишком короткий!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка совпадения паролей
            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            //Проверка чекбоксов
            if (!cbAgreement.isChecked() || !cbPersonal.isChecked() || !cbResponsibility.isChecked()) {
                Toast.makeText(this, "Примите все соглашения!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Успех
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_LONG).show();
            // finish();
        });

        tvHaveAccount.setOnClickListener(v -> finish());
    }
}
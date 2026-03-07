package com.example.zkazino;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText etSurname, etName, etPatronymic, etEmailPhone, etPassword, etConfirmPassword;
    TextView tvDob;
    RadioGroup rgGender;
    Button btnRegister;
    TextView tvHaveAccount, tvCloseHint;
    LinearLayout cardPasswordHint;
    CheckBox cbAgreement, cbPersonal, cbResponsibility;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String selectedDate = "";
    private String selectedGender = "Мужской";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvHaveAccount = findViewById(R.id.tvHaveAccount);
        cardPasswordHint = findViewById(R.id.cardPasswordHint);
        tvCloseHint = findViewById(R.id.tvCloseHint);
        cbAgreement = findViewById(R.id.cbAgreement);
        cbPersonal = findViewById(R.id.cbPersonal);
        cbResponsibility = findViewById(R.id.cbResponsibility);

        etSurname = findViewById(R.id.etSurname);
        etName = findViewById(R.id.etName);
        etPatronymic = findViewById(R.id.etPatronymic);
        etEmailPhone = findViewById(R.id.etEmailPhone);
        tvDob = findViewById(R.id.tvDob);
        rgGender = findViewById(R.id.rgGender);
    }

    private void setupListeners() {
        // Выбор даты рождения
        tvDob.setOnClickListener(v -> showDatePicker());

        // Выбор пола
        rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMale) {
                selectedGender = "Мужской";
            } else {
                selectedGender = "Женский";
            }
        });

        // Закрытие подсказки пароля
        tvCloseHint.setOnClickListener(v -> cardPasswordHint.setVisibility(View.GONE));

        // Кнопка регистрации
        btnRegister.setOnClickListener(v -> {
            String email = etEmailPhone.getText().toString().trim();
            String pass = etPassword.getText().toString();
            String confirmPass = etConfirmPassword.getText().toString();
            String surname = etSurname.getText().toString();
            String name = etName.getText().toString();
            String patronymic = etPatronymic.getText().toString();
            String phone = etEmailPhone.getText().toString();

            // Проверка даты
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Выберите дату рождения!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка пароля
            if (pass.length() <= 6) {
                cardPasswordHint.setVisibility(View.VISIBLE);
                return;
            }

            // Проверка совпадения паролей
            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка соглашений
            if (!cbAgreement.isChecked()) {
                Toast.makeText(this, "Примите соглашения", Toast.LENGTH_SHORT).show();
                return;
            }

            // Регистрация
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        saveUserData(email, surname, name, patronymic, phone, selectedDate, selectedGender);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // Переход к логину
        tvHaveAccount.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "." + (selectedMonth + 1) + "." + selectedYear;
                    tvDob.setText(selectedDate);
                },
                year,
                month,
                day
        );


        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18); //  18 лет
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void saveUserData(String email, String surname, String name,
                              String patronymic, String phone, String dob, String gender) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("surname", surname);
        userData.put("name", name);
        userData.put("patronymic", patronymic);
        userData.put("phone", phone);
        userData.put("dateOfBirth", dob);
        userData.put("gender", gender);
        userData.put("balance", 1000);
        userData.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка БД: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
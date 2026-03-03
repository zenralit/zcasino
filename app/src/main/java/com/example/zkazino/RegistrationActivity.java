package com.example.zkazino;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText etSurname, etName, etPatronymic, etDob, etEmailPhone, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvHaveAccount, tvCloseHint;
    LinearLayout cardPasswordHint;
    CheckBox cbAgreement, cbPersonal, cbResponsibility;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        tvCloseHint.setOnClickListener(v -> cardPasswordHint.setVisibility(View.GONE));

        btnRegister.setOnClickListener(v -> {
            String email = etEmailPhone.getText().toString().trim();
            String pass = etPassword.getText().toString();
            String confirmPass = etConfirmPassword.getText().toString();

            String surname = etSurname.getText().toString();
            String name = etName.getText().toString();

            if (pass.length() <= 6) {
                cardPasswordHint.setVisibility(View.VISIBLE);
                return;
            }
            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cbAgreement.isChecked()) {
                Toast.makeText(this, "Примите соглашения", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        saveUserData(email, surname, name);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        tvHaveAccount.setOnClickListener(v -> finish());
    }

    private void saveUserData(String email, String surname, String name) {
        String userId = mAuth.getCurrentUser().getUid();

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setSurname(surname);
        user.setName(name);
        user.setPatronymic(etPatronymic.getText().toString());
        user.setPhone(etEmailPhone.getText().toString());
        user.setGender("M");
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(userId)
                            .update("balance", 1000);
                    Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка БД: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}
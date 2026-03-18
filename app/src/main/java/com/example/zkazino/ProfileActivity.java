package com.example.zkazino;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail, tvProfileBalance;
    private TextView tvProfileSurname, tvProfilePatronymic, tvProfilePhone, tvProfileGender, tvProfileDob;
    private EditText etEditName;
    private Button btnSaveName, btnBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
        loadProfileData();
    }

    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileBalance = findViewById(R.id.tvProfileBalance);
        tvProfileSurname = findViewById(R.id.tvProfileSurname);
        tvProfilePatronymic = findViewById(R.id.tvProfilePatronymic);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileGender = findViewById(R.id.tvProfileGender);
        tvProfileDob = findViewById(R.id.tvProfileDob);
        etEditName = findViewById(R.id.etEditName);
        btnSaveName = findViewById(R.id.btnSaveName);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSaveName.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Введите имя!", Toast.LENGTH_SHORT).show();
                return;
            }
            updateName(newName);
        });
    }

    private void loadProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvProfileEmail.setText(user.getEmail());

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String surname = documentSnapshot.getString("surname");
                            String patronymic = documentSnapshot.getString("patronymic");
                            String phone = documentSnapshot.getString("phone");
                            String gender = documentSnapshot.getString("gender");
                            String dob = documentSnapshot.getString("dateOfBirth");
                            Long balance = documentSnapshot.getLong("balance");

                            tvProfileName.setText(name != null ? name : "Не указано");
                            tvProfileSurname.setText(surname != null ? surname : "Не указано");
                            tvProfilePatronymic.setText(patronymic != null ? patronymic : "Не указано");
                            tvProfilePhone.setText(phone != null ? phone : "Не указано");
                            tvProfileGender.setText(gender != null ? gender : "Не указано");
                            tvProfileDob.setText(dob != null ? dob : "Не указано");
                            tvProfileBalance.setText(balance != null ? "$ " + balance : "$ 0");

                            etEditName.setText(name != null ? name : "");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateName(String newName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .update("name", newName)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Имя обновлено!", Toast.LENGTH_SHORT).show();
                        tvProfileName.setText(newName);
                        etEditName.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
package kr.co.example.treeplz;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etPasswordConfirm;
    private MaterialButton btnSignUpAction;
    private ImageButton btnBack;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        preferenceHelper = PreferenceHelper.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnSignUpAction = findViewById(R.id.btnSignUpAction);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSignUpAction.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etPasswordConfirm.getText().toString().trim();

            // 1. 빈칸 체크
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. 비밀번호 일치 체크
            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. 이메일 형식 체크 (간단하게 @ 포함 여부만)
            if (!email.contains("@")) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. 회원가입 처리 (저장)
            preferenceHelper.registerUser(email, password);

            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            finish(); // 로그인 화면으로 복귀
        });
    }
}
package kr.co.example.treeplz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnLoginAction;
    private TextView tvGuestLogin, tvSignUp;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 자동 로그인 체크
        preferenceHelper = PreferenceHelper.getInstance(this);
        if (preferenceHelper.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLoginAction = findViewById(R.id.btnLoginAction);
        tvGuestLogin = findViewById(R.id.tvGuestLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {
        // 로그인 버튼
        btnLoginAction.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // [변경] 저장된 회원정보와 일치하는지 확인
            if (preferenceHelper.validateUser(email, password)) {
                performLoginSuccess();
            } else {
                if (!preferenceHelper.hasRegisteredUser()) {
                    Toast.makeText(this, "No account found. Please Sign Up first.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 회원가입 버튼 (이동)
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // 게스트 로그인 (테스트용)
        tvGuestLogin.setOnClickListener(v -> {
            performLoginSuccess();
        });
    }

    private void performLoginSuccess() {
        // 로그인 상태 저장
        preferenceHelper.setLoggedIn(true);
        Toast.makeText(this, "Welcome to TreePlz! 🌿", Toast.LENGTH_SHORT).show();
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
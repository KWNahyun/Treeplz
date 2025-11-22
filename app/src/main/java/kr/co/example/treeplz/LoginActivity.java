package kr.co.example.treeplz;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    // private Switch switchLanguage; // ❌ Switch 타입에서 ToggleButton 타입으로 변경
    private ToggleButton switchLanguage; // ✅ 타입 수정
    private TextView tvWelcome, tvTitle, tvSubtitle, tvTerms;
    private MaterialButton btnGoogle, btnTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        // switchLanguage = findViewById(R.id.switchLanguage); // ❌ Switch로 캐스팅되는 것을 방지
        switchLanguage = findViewById(R.id.switchLanguage); // ✅ ToggleButton으로 올바르게 캐스팅 (또는 <ToggleButton> 명시)
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvTerms = findViewById(R.id.tvTerms);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnTutorial = findViewById(R.id.btnTutorial);

        // Init language (default EN). If you want start as KO, call LanguageManager.setLanguage(...)
        updateTexts();

        // Language toggle: switch between en / ko
        // setOnCheckedChangeListener는 Switch와 ToggleButton 모두에 호환됩니다.
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            } else {
                LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            }
            updateTexts();
        });

        // Google button (demo)
        btnGoogle.setOnClickListener(v -> {
            // Demo: show toast. Replace with real auth flow if needed.
            Toast.makeText(this, LanguageManager.getInstance().t("login.googleButton") + " (demo)", Toast.LENGTH_SHORT).show();
            // Example: startActivity(new Intent(this, MainActivity.class));
        });

        // Tutorial button
        btnTutorial.setOnClickListener(v -> {
            Toast.makeText(this, LanguageManager.getInstance().t("login.tutorial") + " (demo)", Toast.LENGTH_SHORT).show();
            // Example: start tutorial activity
            // startActivity(new Intent(this, TutorialActivity.class));
        });

        // Optional: load remote background image with Glide (uncomment if you add dependency)
        // ImageView bg = findViewById(R.id.bgImage);
        // Glide.with(this).load("https://images.unsplash.com/...").into(bg);
    }

    private void updateTexts() {
        tvWelcome.setText(LanguageManager.getInstance().t("login.welcome"));
        tvSubtitle.setText(LanguageManager.getInstance().t("login.subtitle"));
        tvTitle.setText("Treeplz"); // title is brand; keep as-is or translate if desired
        btnGoogle.setText(LanguageManager.getInstance().t("login.googleButton"));
        btnTutorial.setText(LanguageManager.getInstance().t("login.tutorial"));
        tvTerms.setText(LanguageManager.getInstance().t("login.terms"));

        // set switch checked state to reflect current language
        switchLanguage.setChecked(LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO);
    }
}
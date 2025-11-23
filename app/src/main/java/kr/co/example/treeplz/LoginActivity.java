package kr.co.example.treeplz;

import android.os.Bundle;
<<<<<<< Updated upstream
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
=======
import android.widget.ImageButton;
>>>>>>> Stashed changes
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private Switch switchLanguage;
    private TextView tvWelcome, tvTitle, tvSubtitle, tvTerms;
    private ImageButton btnGoogle;
    private MaterialButton btnTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        switchLanguage = findViewById(R.id.switchLanguage);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvTerms = findViewById(R.id.tvTerms);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnTutorial = findViewById(R.id.btnTutorial);

        // Init language (default EN). If you want start as KO, call LanguageManager.setLanguage(...)
        updateTexts();

        // Language toggle: switch between en / ko
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
        tvTerms.setText(LanguageManager.getInstance().t("login.terms"));

        // set switch checked state to reflect current language
        switchLanguage.setChecked(LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO);
    }
}

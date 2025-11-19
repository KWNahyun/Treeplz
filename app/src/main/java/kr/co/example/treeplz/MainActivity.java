package kr.co.example.treeplz;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    // aiUsage dummy (in a real app, pass via Intent extras)
    private int requests = 8;
    private int tokens = 3200;
    private double timeSpent = 12.5; // minutes
    private int carbonFootprint = 120; // grams

    private ConstraintLayout root;
    private TextView tvTreeHealthPercent, tvHealthMessage;
    private ProgressBar progressHealth;
    private TextView badgeRequests, badgeCarbon;
    private TextView tvStatRequests, tvStatTime, tvStatTokens;
    private Switch switchLanguage;
    private MaterialButton btnLearnPrompting, btnViewUsage;
    private ImageButton btnCalendar, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If you want to accept aiUsage from Intent extras:
        // requests = getIntent().getIntExtra("requests", requests); // etc.

        root = findViewById(R.id.root);
        tvTreeHealthPercent = findViewById(R.id.tvTreeHealthPercent);
        tvHealthMessage = findViewById(R.id.tvHealthMessage);
        progressHealth = findViewById(R.id.progressHealth);
        badgeRequests = findViewById(R.id.badgeRequests);
        badgeCarbon = findViewById(R.id.badgeCarbon);

        tvStatRequests = findViewById(R.id.tvStatRequests);
        tvStatTime = findViewById(R.id.tvStatTime);
        tvStatTokens = findViewById(R.id.tvStatTokens);

        switchLanguage = findViewById(R.id.switchLanguage);
        btnLearnPrompting = findViewById(R.id.btnLearnPrompting);
        btnViewUsage = findViewById(R.id.btnViewUsage);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnSettings = findViewById(R.id.btnSettings);

        // Init UI from aiUsage
        updateAiUsageUI();

        // Set language initial state
        switchLanguage.setChecked(LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO);
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            else LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            updateStrings();
        });

        // Buttons (demo actions)
        btnLearnPrompting.setOnClickListener(v -> {
            Toast.makeText(this, LanguageManager.getInstance().t("main.learnPrompting") + " (demo)", Toast.LENGTH_SHORT).show();
            // onShowPrompting()
        });

        btnViewUsage.setOnClickListener(v -> {
            Toast.makeText(this, LanguageManager.getInstance().t("main.viewUsage") + " (demo)", Toast.LENGTH_SHORT).show();
            // onShowUsage()
        });

        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });


        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings (demo)", Toast.LENGTH_SHORT).show();
            // onShowSettings()
        });

        // initial labels
        updateStrings();
    }

    private void updateAiUsageUI() {
        // compute health like TSX
        int treeHealth = calculateTreeHealth();

        // update progress and labels
        progressHealth.setProgress(treeHealth);
        tvTreeHealthPercent.setText(treeHealth + "%");
        tvHealthMessage.setText(getHealthMessage(treeHealth));

        // floating badges
        badgeRequests.setText(requests + " " + LanguageManager.getInstance().t("main.requests").toLowerCase());
        badgeCarbon.setText(carbonFootprint + "g " + LanguageManager.getInstance().t("main.carbonFootprint"));

        // quick stats
        tvStatRequests.setText(String.valueOf(requests));
        tvStatTime.setText(Math.round(timeSpent) + LanguageManager.getInstance().t("main.minutes"));
        tvStatTokens.setText(String.format("%.1fk", tokens / 1000.0));

        // dynamic background gradient based on health
        applyBackgroundGradient(treeHealth);
    }

    private void updateStrings() {
        // update texts that depend on language
        findViewById(R.id.tvBrand).requestLayout();
        findViewById(R.id.tvTodayUsageBadge).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.tvTodayUsageBadge)).setText(LanguageManager.getInstance().t("main.todayUsage"));
        ((TextView)findViewById(R.id.tvTreeHealthLabel)).setText(LanguageManager.getInstance().t("main.treeHealth"));
        ((TextView)findViewById(R.id.tvLabelRequests)).setText(LanguageManager.getInstance().t("main.requests"));
        ((TextView)findViewById(R.id.tvLabelTime)).setText(LanguageManager.getInstance().t("main.time"));
        ((TextView)findViewById(R.id.tvLabelTokens)).setText(LanguageManager.getInstance().t("main.tokens"));
        btnLearnPrompting.setText(LanguageManager.getInstance().t("main.learnPrompting"));
        btnViewUsage.setText(LanguageManager.getInstance().t("main.viewUsage"));

        // refresh the AI usage UI so language-dependent strings update
        updateAiUsageUI();
    }

    private int calculateTreeHealth() {
        int baseHealth = 100;
        int usageFactor = Math.min(requests * 2 + tokens / 1000, 100);
        int health = Math.max(0, baseHealth - usageFactor);
        return Math.round(health);
    }

    private String getHealthMessage(int treeHealth) {
        if (treeHealth >= 80) return LanguageManager.getInstance().t("main.healthMessage.thriving");
        if (treeHealth >= 60) return LanguageManager.getInstance().t("main.healthMessage.healthy");
        if (treeHealth >= 40) return LanguageManager.getInstance().t("main.healthMessage.declining");
        if (treeHealth >= 20) return LanguageManager.getInstance().t("main.healthMessage.wilting");
        return LanguageManager.getInstance().t("main.healthMessage.critical");
    }

    private void applyBackgroundGradient(int treeHealth) {
        // choose two colors based on health
        int startColor, midColor, endColor;
        if (treeHealth >= 80) {
            startColor = 0xFFF8F9F5; midColor = 0xFFF0F5EA; endColor = 0xFFE8F2DF;
        } else if (treeHealth >= 60) {
            startColor = 0xFFF8F9F5; midColor = 0xFFF2F4EE; endColor = 0xFFEAEEE2;
        } else if (treeHealth >= 40) {
            startColor = 0xFFF5F6F2; midColor = 0xFFF0F1ED; endColor = 0xFFE7E8E4;
        } else if (treeHealth >= 20) {
            startColor = 0xFFF2F3F0; midColor = 0xFFEDedea; endColor = 0xFFE4E5E2;
        } else {
            startColor = 0xFFEFEEEF; midColor = 0xFFE8E8E9; endColor = 0xFFE0E0E1;
        }

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, midColor, endColor});
        root.setBackground(gd);
    }
}

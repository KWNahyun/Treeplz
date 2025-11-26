package kr.co.example.treeplz;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

import kr.co.example.treeplz.model.AiUsage;
import kr.co.example.treeplz.network.ApiClient;
import kr.co.example.treeplz.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private PreferenceHelper preferenceHelper;

    // 대시보드 표시용 데이터 변수
    private int requests;
    private long tokens;
    private long timeSpentMs;
    private double carbonFootprint;

    private AiUsage latestUsageFromApi;

    private ConstraintLayout root;
    private TextView tvBrand, tvTodayUsageBadge, btnLogin; // btnLogin ID는 그대로 두고 텍스트만 Logout으로 바꿉니다
    private ToggleButton switchLanguage;
    private ImageButton btnCalendar, btnSettings;

    private TextView tvTreeHealthLabel, tvTreeHealthPercent, tvHealthMessage;
    private ProgressBar progressHealth;

    private ImageView imgTreeState;
    private TextView badgeRequests, badgeCarbon;

    private TextView tvStatRequests, tvLabelRequests;
    private TextView tvStatTime, tvLabelTime;
    private TextView tvStatTokens, tvLabelTokens;

    private MaterialButton btnLearnPrompting, btnViewUsage, btnChatDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceHelper = PreferenceHelper.getInstance(this);

        initViews();
        setupListeners();
        refreshDashboard();
        fetchUsageFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
        fetchUsageFromServer();
    }

    private void initViews() {
        root = findViewById(R.id.root);
        tvBrand = findViewById(R.id.tvBrand);
        tvTodayUsageBadge = findViewById(R.id.tvTodayUsageBadge);
        btnLogin = findViewById(R.id.btnLogin);
        switchLanguage = findViewById(R.id.switchLanguage);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnSettings = findViewById(R.id.btnSettings);

        tvTreeHealthLabel = findViewById(R.id.tvTreeHealthLabel);
        tvTreeHealthPercent = findViewById(R.id.tvTreeHealthPercent);
        progressHealth = findViewById(R.id.progressHealth);
        tvHealthMessage = findViewById(R.id.tvHealthMessage);

        imgTreeState = findViewById(R.id.imgTreeState);
        badgeRequests = findViewById(R.id.badgeRequests);
        badgeCarbon = findViewById(R.id.badgeCarbon);

        tvStatRequests = findViewById(R.id.tvStatRequests);
        tvLabelRequests = findViewById(R.id.tvLabelRequests);

        tvStatTime = findViewById(R.id.tvStatTime);
        tvLabelTime = findViewById(R.id.tvLabelTime);

        tvStatTokens = findViewById(R.id.tvStatTokens);
        tvLabelTokens = findViewById(R.id.tvLabelTokens);

        btnLearnPrompting = findViewById(R.id.btnLearnPrompting);
        btnViewUsage = findViewById(R.id.btnViewUsage);
        btnChatDemo = findViewById(R.id.btnChatDemo);

        boolean isKo = LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO;
        switchLanguage.setChecked(isKo);

        // [변경] 초기 텍스트를 Logout으로 설정
        btnLogin.setText("Logout");
    }

    private void setupListeners() {
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            } else {
                LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            }
            updateStrings();
        });

        btnLearnPrompting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EfficientPromptingActivity.class);
            startActivity(intent);
        });

        btnViewUsage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UsageDetailsActivity.class);
            startActivity(intent);
        });

        btnChatDemo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> showApiKeyDialog());

        // [변경] 로그인 버튼 -> 로그아웃 기능으로 변경
        btnLogin.setOnClickListener(v -> {
            // 1. 로그인 상태 해제
            preferenceHelper.setLoggedIn(false);

            // 2. 로그인 화면으로 이동
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // 중요: 뒤로가기 눌렀을 때 다시 메인으로 못 오게 스택 비우기
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // 현재 액티비티 종료

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });

        imgTreeState.setOnClickListener(v -> {
            preferenceHelper.addUsage(500, 1500);
            refreshDashboard();
            Toast.makeText(this, "+500 Tokens (Demo)", Toast.LENGTH_SHORT).show();
        });

        imgTreeState.setOnLongClickListener(v -> {
            preferenceHelper.clearTodayData();
            refreshDashboard();
            Toast.makeText(this, "Tree Health Restored! 🌿", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void refreshDashboard() {
        tokens = preferenceHelper.getTodayTokens();
        requests = preferenceHelper.getTodayRequests();
        timeSpentMs = preferenceHelper.getTodayTime();
        carbonFootprint = tokens * 0.0002;

        updateAiUsageUI();
    }

    private void fetchUsageFromServer() {
        String apiKey = preferenceHelper.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) return;

        ApiService api = ApiClient.getInstance().getRetrofit().create(ApiService.class);
        String authHeader = "Bearer " + apiKey;

        api.getMyUsage(authHeader).enqueue(new Callback<AiUsage>() {
            @Override
            public void onResponse(Call<AiUsage> call, Response<AiUsage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    latestUsageFromApi = response.body();
                    requests = latestUsageFromApi.requests;
                    tokens = latestUsageFromApi.tokens;
                    timeSpentMs = (long) (latestUsageFromApi.timeSpent * 60 * 1000);
                    carbonFootprint = latestUsageFromApi.carbonFootprint;
                    updateAiUsageUI();

                    preferenceHelper.setTodayRequests(latestUsageFromApi.requests);
                    preferenceHelper.setTodayTokens(latestUsageFromApi.tokens);
                    preferenceHelper.setTodayTime((long)(latestUsageFromApi.timeSpent * 60 * 1000));

                }
            }

            @Override
            public void onFailure(Call<AiUsage> call, Throwable t) {
            }
        });
    }

    private void updateAiUsageUI() {
        int treeHealth = calculateTreeHealth();

        progressHealth.setProgress(treeHealth);
        tvTreeHealthPercent.setText(treeHealth + "%");
        tvHealthMessage.setText(getHealthMessage(treeHealth));

        badgeRequests.setText(requests + " " + LanguageManager.getInstance().t("main.requests"));
        badgeCarbon.setText(String.format("%.1fg %s", carbonFootprint, LanguageManager.getInstance().t("main.carbonFootprint")));

        tvStatRequests.setText(String.valueOf(requests));
        long minutes = timeSpentMs / 1000 / 60;
        tvStatTime.setText(minutes + LanguageManager.getInstance().t("main.minutes"));
        tvStatTokens.setText(String.format("%.1fk", tokens / 1000.0));

        applyBackgroundGradient(treeHealth);
        updateTreeImage(treeHealth);
    }

    private void updateStrings() {
        tvTodayUsageBadge.setText(LanguageManager.getInstance().t("main.todayUsage"));
        tvTreeHealthLabel.setText(LanguageManager.getInstance().t("main.treeHealth"));
        tvLabelRequests.setText(LanguageManager.getInstance().t("main.requests"));
        tvLabelTime.setText(LanguageManager.getInstance().t("main.time"));
        tvLabelTokens.setText(LanguageManager.getInstance().t("main.tokens"));
        btnLearnPrompting.setText(LanguageManager.getInstance().t("main.learnPrompting"));
        btnViewUsage.setText(LanguageManager.getInstance().t("main.viewUsage"));

        // [변경] 언어가 바뀌어도 버튼 텍스트는 Logout으로 유지 (혹은 다국어 지원 시 t("main.logout") 사용)
        btnLogin.setText("Logout");

        updateAiUsageUI();
    }

    private int calculateTreeHealth() {
        int baseHealth = 100;
        int usageFactor = (int) Math.min(requests * 2 + tokens / 500, 100);
        return Math.max(0, baseHealth - usageFactor);
    }

    private String getHealthMessage(int treeHealth) {
        if (treeHealth >= 80) return LanguageManager.getInstance().t("main.healthMessage.thriving");
        if (treeHealth >= 60) return LanguageManager.getInstance().t("main.healthMessage.healthy");
        if (treeHealth >= 40) return LanguageManager.getInstance().t("main.healthMessage.declining");
        if (treeHealth >= 20) return LanguageManager.getInstance().t("main.healthMessage.wilting");
        return LanguageManager.getInstance().t("main.healthMessage.critical");
    }

    private void updateTreeImage(int treeHealth) {
        imgTreeState.setAlpha(1.0f);
        if (treeHealth >= 85) imgTreeState.setImageResource(R.drawable.tree_state_1);
        else if (treeHealth >= 70) imgTreeState.setImageResource(R.drawable.tree_state_2);
        else if (treeHealth >= 55) imgTreeState.setImageResource(R.drawable.tree_state_3);
        else if (treeHealth >= 40) imgTreeState.setImageResource(R.drawable.tree_state_4);
        else if (treeHealth >= 20) imgTreeState.setImageResource(R.drawable.tree_state_5);
        else imgTreeState.setImageResource(R.drawable.tree_state_6);
    }

    private void applyBackgroundGradient(int treeHealth) {
        int startColor, midColor, endColor;
        if (treeHealth >= 80) { startColor = 0xFFF8F9F5; midColor = 0xFFF0F5EA; endColor = 0xFFE8F2DF; }
        else if (treeHealth >= 60) { startColor = 0xFFF8F9F5; midColor = 0xFFF2F4EE; endColor = 0xFFEAEEE2; }
        else if (treeHealth >= 40) { startColor = 0xFFF5F6F2; midColor = 0xFFF0F1ED; endColor = 0xFFE7E8E4; }
        else if (treeHealth >= 20) { startColor = 0xFFF2F3F0; midColor = 0xFFEDedea; endColor = 0xFFE4E5E2; }
        else { startColor = 0xFFEFEEEF; midColor = 0xFFE8E8E9; endColor = 0xFFE0E0E1; }

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, midColor, endColor});
        root.setBackground(gd);
    }

    private void showApiKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("API Key");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String key = input.getText().toString().trim();
            if(!key.isEmpty()){
                preferenceHelper.setApiKey(key);
                fetchUsageFromServer();
                Toast.makeText(this, "API Key Saved", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
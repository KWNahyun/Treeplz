package kr.co.example.treeplz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import kr.co.example.treeplz.model.ChatRequest;
import kr.co.example.treeplz.model.ChatResponse;
import kr.co.example.treeplz.network.ApiClient;
import kr.co.example.treeplz.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private PreferenceHelper preferenceHelper;

    private int requests;
    private long tokens;
    private long timeSpentMs;
    private double carbonFootprint;

    private AiUsage latestUsageFromApi;

    private ConstraintLayout root;
    private TextView tvTreeHealthPercent, tvHealthMessage;
    private ProgressBar progressHealth;
    private TextView badgeRequests, badgeCarbon;
    private TextView tvStatRequests, tvStatTime, tvStatTokens;
    private MaterialButton btnLearnPrompting, btnViewUsage;
    private ImageButton btnCalendar, btnSettings;
    private ImageView imgTreeState;
    private ToggleButton switchLanguage;
    private MaterialButton btnChatDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchLanguage = findViewById(R.id.switchLanguage);
        preferenceHelper = PreferenceHelper.getInstance(this);

        initViews();
        refreshDashboard();
        fetchUsageFromServer();

        switchLanguage.setChecked(
                LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO
        );
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            else LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            updateStrings();
        });

        setupListeners();
    }

    private void initViews() {
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
        imgTreeState = findViewById(R.id.imgTreeState);
        btnChatDemo = findViewById(R.id.btnChatDemo);
    }

    private void setupListeners() {
        btnLearnPrompting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EfficientPromptingActivity.class);
            startActivity(intent);
        });

        btnChatDemo.setOnClickListener(v -> {
            String apiKey = preferenceHelper.getApiKey();
            if (apiKey == null || apiKey.isEmpty()) {
                Toast.makeText(this, "먼저 API Key를 설정해 주세요.", Toast.LENGTH_SHORT).show();
                showApiKeyDialog();
                return;
            }
            ApiService api = ApiClient.getInstance().getRetrofit().create(ApiService.class);
            String authHeader = "Bearer " + apiKey;
            ChatRequest body = new ChatRequest("친환경적으로 LLM을 사용하는 팁을 한국어로 3줄만 알려줘.");
            api.chat(authHeader, body).enqueue(new Callback<ChatResponse>() {
                @Override
                public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(MainActivity.this, "Chat 호출 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Ask AI Demo")
                            .setMessage(response.body().reply)
                            .setPositiveButton("OK", null)
                            .show();
                    fetchUsageFromServer();
                }

                @Override
                public void onFailure(Call<ChatResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnViewUsage.setOnClickListener(v -> {
            UsageDetailsActivity.AiUsage usageForDetail;
            if (latestUsageFromApi != null) {
                usageForDetail = new UsageDetailsActivity.AiUsage(
                        latestUsageFromApi.requests,
                        latestUsageFromApi.tokens,
                        latestUsageFromApi.timeSpent,
                        latestUsageFromApi.carbonFootprint
                );
            } else {
                double minutesDouble = timeSpentMs / 1000.0 / 60.0;
                int carbonInt = (int) Math.round(carbonFootprint);
                usageForDetail = new UsageDetailsActivity.AiUsage(requests, (int) tokens, minutesDouble, carbonInt);
            }
            UsageDetailsActivity.start(MainActivity.this, usageForDetail);
        });

        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> showApiKeyDialog());

        imgTreeState.setOnClickListener(v -> {
            preferenceHelper.addUsage(500, 1500);
            refreshDashboard();
            Toast.makeText(this, "+500 Tokens (Demo)", Toast.LENGTH_SHORT).show();
        });

        imgTreeState.setOnLongClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("treeplz_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("today_tokens", 0L);
            editor.putInt("today_requests", 0);
            editor.putLong("today_time", 0L);
            editor.apply();

            refreshDashboard();
            Toast.makeText(MainActivity.this, "토큰 사용량이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void refreshDashboard() {
        tokens = preferenceHelper.getTodayTokens();
        requests = preferenceHelper.getTodayRequests();
        timeSpentMs = preferenceHelper.getTodayTime();
        EcoCalculator.EcoMetrics metrics = EcoCalculator.calculateImpact(tokens);
        carbonFootprint = metrics.co2Grams;
        updateAiUsageUI();
    }

    private void fetchUsageFromServer() {
        String apiKey = preferenceHelper.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return;
        }
        ApiService api = ApiClient.getInstance().getRetrofit().create(ApiService.class);
        String authHeader = "Bearer " + apiKey;
        Call<AiUsage> call = api.getMyUsage(authHeader);
        call.enqueue(new Callback<AiUsage>() {
            @Override
            public void onResponse(Call<AiUsage> call, Response<AiUsage> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MainActivity.this, "Failed to fetch usage. Using local data.", Toast.LENGTH_SHORT).show();
                    return;
                }
                AiUsage usage = response.body();
                latestUsageFromApi = usage;
                requests = usage.requests;
                tokens = usage.tokens;
                timeSpentMs = (long) (usage.timeSpent * 60_000L);
                if (usage.carbonFootprint > 0) {
                    carbonFootprint = usage.carbonFootprint;
                } else {
                    EcoCalculator.EcoMetrics metrics = EcoCalculator.calculateImpact(tokens);
                    carbonFootprint = metrics.co2Grams;
                }
                updateAiUsageUI();
            }

            @Override
            public void onFailure(Call<AiUsage> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error. Using local data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAiUsageUI() {
        int treeHealth = calculateTreeHealth();
        progressHealth.setProgress(treeHealth);
        tvTreeHealthPercent.setText(treeHealth + "%");
        tvHealthMessage.setText(getHealthMessage(treeHealth));
        badgeRequests.setText(requests + " " + LanguageManager.getInstance().t("main.requests").toLowerCase());
        badgeCarbon.setText(String.format("%.1fg %s", carbonFootprint, LanguageManager.getInstance().t("main.carbonFootprint")));
        tvStatRequests.setText(String.valueOf(requests));
        long minutes = Math.round(timeSpentMs / 1000.0 / 60.0);
        tvStatTime.setText(minutes + LanguageManager.getInstance().t("main.minutes"));
        tvStatTokens.setText(String.format("%.1fk", tokens / 1000.0));
        applyBackgroundGradient(treeHealth);
        updateTreeImage(treeHealth);
    }

    private void updateStrings() {
        findViewById(R.id.tvBrand).requestLayout();
        ((TextView) findViewById(R.id.tvTodayUsageBadge)).setText(LanguageManager.getInstance().t("main.todayUsage"));
        ((TextView) findViewById(R.id.tvTreeHealthLabel)).setText(LanguageManager.getInstance().t("main.treeHealth"));
        ((TextView) findViewById(R.id.tvLabelRequests)).setText(LanguageManager.getInstance().t("main.requests"));
        ((TextView) findViewById(R.id.tvLabelTime)).setText(LanguageManager.getInstance().t("main.time"));
        ((TextView) findViewById(R.id.tvLabelTokens)).setText(LanguageManager.getInstance().t("main.tokens"));
        btnLearnPrompting.setText(LanguageManager.getInstance().t("main.learnPrompting"));
        btnViewUsage.setText(LanguageManager.getInstance().t("main.viewUsage"));
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
        if (treeHealth >= 85) {
            imgTreeState.setImageResource(R.drawable.tree_state_1);
        } else if (treeHealth >= 70) {
            imgTreeState.setImageResource(R.drawable.tree_state_2);
        } else if (treeHealth >= 55) {
            imgTreeState.setImageResource(R.drawable.tree_state_3);
        } else if (treeHealth >= 40) {
            imgTreeState.setImageResource(R.drawable.tree_state_4);
        } else if (treeHealth >= 20) {
            imgTreeState.setImageResource(R.drawable.tree_state_5);
        } else {
            imgTreeState.setImageResource(R.drawable.tree_state_6);
        }
    }

    private void applyBackgroundGradient(int treeHealth) {
        int startColor, midColor, endColor;
        if (treeHealth >= 80) {
            startColor = 0xFFF8F9F5;
            midColor = 0xFFF0F5EA;
            endColor = 0xFFE8F2DF;
        } else if (treeHealth >= 60) {
            startColor = 0xFFF8F9F5;
            midColor = 0xFFF2F4EE;
            endColor = 0xFFEAEEE2;
        } else if (treeHealth >= 40) {
            startColor = 0xFFF5F6F2;
            midColor = 0xFFF0F1ED;
            endColor = 0xFFE7E8E4;
        } else if (treeHealth >= 20) {
            startColor = 0xFFF2F3F0;
            midColor = 0xFFEDedea;
            endColor = 0xFFE4E5E2;
        } else {
            startColor = 0xFFEFEEEF;
            midColor = 0xFFE8E8E9;
            endColor = 0xFFE0E0E1;
        }
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, midColor, endColor});
        root.setBackground(gd);
    }

    private void showApiKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenAI API Key");
        builder.setMessage("Enter your API Key to track usage.");
        final EditText input = new EditText(this);
        input.setHint("sk-...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String key = input.getText().toString().trim();
            if (!key.isEmpty()) {
                preferenceHelper.setApiKey(key);
                fetchUsageFromServer();
                Toast.makeText(MainActivity.this, "Key Saved!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}

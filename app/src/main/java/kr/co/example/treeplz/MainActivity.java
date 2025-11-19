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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    // 저장소 헬퍼 (이전에 만든 파일)
    private PreferenceHelper preferenceHelper;

    // 실제 데이터 변수
    private int requests;
    private long tokens;
    private long timeSpentMs;
    private double carbonFootprint; // EcoCalculator로 계산됨

    private ConstraintLayout root;
    private TextView tvTreeHealthPercent, tvHealthMessage;
    private ProgressBar progressHealth;
    private TextView badgeRequests, badgeCarbon;
    private TextView tvStatRequests, tvStatTime, tvStatTokens;
    private MaterialSwitch switchLanguage;
    private MaterialButton btnLearnPrompting, btnViewUsage;
    private ImageButton btnCalendar, btnSettings;
    private ImageView imgTreeState; // 나무 이미지 뷰 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 저장소 초기화
        preferenceHelper = new PreferenceHelper(this);

        // 뷰 초기화
        initViews();

        // 2. 초기 데이터 로드 및 UI 갱신
        refreshDashboard();

        // 언어 설정 초기화
        switchLanguage.setChecked(LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO);
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            else LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            updateStrings();
        });

        // 버튼 리스너 설정
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
    }

    private void setupListeners() {
        // Prompt Learning Activity 이동
        btnLearnPrompting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EfficientPromptingActivity.class);
            startActivity(intent);
        });

        btnViewUsage.setOnClickListener(v -> {
            Toast.makeText(this, LanguageManager.getInstance().t("main.viewUsage") + " (Detailed View)", Toast.LENGTH_SHORT).show();
        });

        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        // [중요] 설정 버튼 -> API Key 입력 팝업
        btnSettings.setOnClickListener(v -> showApiKeyDialog());

        // [데모용 기능] 나무 이미지를 클릭하면 사용량이 늘어난 것처럼 시뮬레이션
        // 발표할 때 나무를 다다다닥 클릭해서 시드는 모습을 보여주세요.
        imgTreeState.setOnClickListener(v -> {
            preferenceHelper.addUsage(500, 1500); // 500토큰, 1.5초 사용 추가
            refreshDashboard(); // 화면 즉시 갱신
            Toast.makeText(this, "+500 Tokens (Demo)", Toast.LENGTH_SHORT).show();
        });
    }

    // 저장된 데이터를 불러와 변수에 할당하고 UI를 그리는 함수
    private void refreshDashboard() {
        tokens = preferenceHelper.getTodayTokens();
        requests = preferenceHelper.getTodayRequests();
        timeSpentMs = preferenceHelper.getTodayTime();

        // 환경 영향 계산 (EcoCalculator 활용)
        EcoCalculator.EcoMetrics metrics = EcoCalculator.calculateImpact(tokens);
        carbonFootprint = metrics.co2Grams;

        updateAiUsageUI();
    }

    private void updateAiUsageUI() {
        // 1. 나무 체력 계산 (사용량 기반)
        int treeHealth = calculateTreeHealth();

        // 2. UI 업데이트
        progressHealth.setProgress(treeHealth);
        tvTreeHealthPercent.setText(treeHealth + "%");
        tvHealthMessage.setText(getHealthMessage(treeHealth));

        // 3. 배지 업데이트 (다국어 지원 포함)
        badgeRequests.setText(requests + " " + LanguageManager.getInstance().t("main.requests").toLowerCase());

        // 탄소 배출량 포맷팅 (소수점 1자리)
        badgeCarbon.setText(String.format("%.1fg %s", carbonFootprint, LanguageManager.getInstance().t("main.carbonFootprint")));

        // 4. 하단 통계 업데이트
        tvStatRequests.setText(String.valueOf(requests));
        // 밀리초 -> 분 단위 변환 (반올림)
        long minutes = Math.round(timeSpentMs / 1000.0 / 60.0);
        tvStatTime.setText(minutes + LanguageManager.getInstance().t("main.minutes"));
        // 토큰 -> k 단위 변환
        tvStatTokens.setText(String.format("%.1fk", tokens / 1000.0));

        // 5. 배경 그라데이션 및 나무 이미지 변경
        applyBackgroundGradient(treeHealth);
        updateTreeImage(treeHealth);
    }

    private void updateStrings() {
        findViewById(R.id.tvBrand).requestLayout();
        ((TextView)findViewById(R.id.tvTodayUsageBadge)).setText(LanguageManager.getInstance().t("main.todayUsage"));
        ((TextView)findViewById(R.id.tvTreeHealthLabel)).setText(LanguageManager.getInstance().t("main.treeHealth"));
        ((TextView)findViewById(R.id.tvLabelRequests)).setText(LanguageManager.getInstance().t("main.requests"));
        ((TextView)findViewById(R.id.tvLabelTime)).setText(LanguageManager.getInstance().t("main.time"));
        ((TextView)findViewById(R.id.tvLabelTokens)).setText(LanguageManager.getInstance().t("main.tokens"));
        btnLearnPrompting.setText(LanguageManager.getInstance().t("main.learnPrompting"));
        btnViewUsage.setText(LanguageManager.getInstance().t("main.viewUsage"));

        updateAiUsageUI(); // 스트링 변경 후 값 다시 표시
    }

    private int calculateTreeHealth() {
        int baseHealth = 100;
        // 로직: 요청 횟수 * 2 + (토큰 / 500) 만큼 체력 감소 (조정 가능)
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

    // 나무 체력에 따라 이미지 변경 (drawables가 있다고 가정)
    private void updateTreeImage(int treeHealth) {
        if (treeHealth >= 80) {
            imgTreeState.setImageResource(R.drawable.tree_state_1);
        } else if (treeHealth >= 50) {
            // imgTreeState.setImageResource(R.drawable.tree_state_2); // 이미지가 있다면 주석 해제
            imgTreeState.setAlpha(0.9f); // 임시 효과
        } else if (treeHealth >= 20) {
            // imgTreeState.setImageResource(R.drawable.tree_state_3);
            imgTreeState.setAlpha(0.7f);
        } else {
            // imgTreeState.setImageResource(R.drawable.tree_state_4);
            imgTreeState.setAlpha(0.5f); // 시들한 느낌
        }
    }

    private void applyBackgroundGradient(int treeHealth) {
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
            startColor = 0xFFEFEEEF; midColor = 0xFFE8E8E9; endColor = 0xFFE0E0E1; // 잿빛
        }

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, midColor, endColor});
        root.setBackground(gd);
    }

    // API Key 입력 다이얼로그
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
                // 키를 새로 넣었으니 사용량을 0으로 초기화할지, 유지할지는 선택 (여기선 유지)
                refreshDashboard();
                Toast.makeText(MainActivity.this, "Key Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
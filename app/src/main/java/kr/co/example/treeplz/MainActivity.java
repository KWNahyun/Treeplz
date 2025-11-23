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

    // 서버에서 받아온 원본 데이터 (상세 화면 전달용)
    private AiUsage latestUsageFromApi;

    // UI 컴포넌트
    private ConstraintLayout root;
    private TextView tvTreeHealthPercent, tvHealthMessage;
    private ProgressBar progressHealth;
    private TextView badgeRequests, badgeCarbon;
    private TextView tvStatRequests, tvStatTime, tvStatTokens;
    private MaterialSwitch switchLanguage;
    private MaterialButton btnLearnPrompting, btnViewUsage;
    private ImageButton btnCalendar, btnSettings;
    private ImageView imgTreeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 저장소 초기화 (싱글톤 사용)
        preferenceHelper = PreferenceHelper.getInstance(this);

        // 2. 뷰 연결
        initViews();

        // 3. 버튼 리스너 설정
        setupListeners();

        // 4. 초기 데이터 로드 (저장된 값 불러오기)
        refreshDashboard();

        // 5. 서버 데이터 요청 (최신 값 갱신)
        fetchUsageFromServer();
    }

    // ★ [핵심] 다른 화면 갔다가 돌아올 때 데이터 새로고침
    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
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

        // 언어 설정 초기화
        switchLanguage.setChecked(LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO);
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            else LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            updateStrings();
        });
    }

    private void setupListeners() {
        // 프롬프트 학습 화면 이동
        btnLearnPrompting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EfficientPromptingActivity.class);
            startActivity(intent);
        });

        // 상세 화면 이동
        btnViewUsage.setOnClickListener(v -> {
            AiUsage usageToSend;
            if (latestUsageFromApi != null) {
                // 서버 데이터가 있으면 그거 사용
                usageToSend = latestUsageFromApi;
            } else {
                // 없으면 현재 로컬 변수로 객체 생성
                usageToSend = new AiUsage(
                        requests,
                        (int) tokens,
                        (double) timeSpentMs / 60000.0, // ms -> min
                        carbonFootprint
                );
            }
            // Helper 메서드 사용 (UsageDetailsActivity에 만들어둔 것)
            UsageDetailsActivity.start(MainActivity.this, usageToSend);
        });

        // 캘린더 화면 이동
        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        // 설정 (API Key 입력)
        btnSettings.setOnClickListener(v -> showApiKeyDialog());

        // [데모 기능] 나무 클릭 시 토큰 사용량 증가 시뮬레이션
        imgTreeState.setOnClickListener(v -> {
            // 1. Preference에 값 누적 저장
            preferenceHelper.addUsage(500, 1500); // 500토큰, 1.5초

            // 2. 화면 즉시 갱신 (저장된 값을 다시 불러옴)
            refreshDashboard();

            Toast.makeText(this, "+500 Tokens (Demo)", Toast.LENGTH_SHORT).show();
        });
    }

    // 저장소(Preference)에서 값을 불러와 변수에 넣고 UI 갱신
    private void refreshDashboard() {
        tokens = preferenceHelper.getTodayTokens();
        requests = preferenceHelper.getTodayRequests();
        timeSpentMs = preferenceHelper.getTodayTime();

        // 탄소 배출량 계산 (EcoCalculator가 있다면 사용, 여기선 간단 공식)
        // 1 token ~= 0.0002g CO2 (가정)
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

                    // 서버 값을 우선시하여 UI 변수 업데이트
                    requests = latestUsageFromApi.requests;
                    tokens = latestUsageFromApi.tokens;
                    // 서버가 분(min) 단위로 준다고 가정하고 밀리초(ms)로 변환
                    timeSpentMs = (long) (latestUsageFromApi.timeSpent * 60 * 1000);
                    carbonFootprint = latestUsageFromApi.carbonFootprint;

                    // ★ 주의: 실제 앱에서는 서버 값을 로컬 DB에 동기화하는 로직이 필요할 수 있음
                    // 여기서는 변수만 업데이트하고 화면을 그림
                    updateAiUsageUI();
                }
            }

            @Override
            public void onFailure(Call<AiUsage> call, Throwable t) {
                // 실패 시 로컬 데이터 유지 (Toast 생략 가능)
                // Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAiUsageUI() {
        // 1. 나무 체력 계산
        int treeHealth = calculateTreeHealth();

        // 2. 프로그레스바 및 메시지
        progressHealth.setProgress(treeHealth);
        tvTreeHealthPercent.setText(treeHealth + "%");
        tvHealthMessage.setText(getHealthMessage(treeHealth));

        // 3. 상단 배지 (언어 적용)
        badgeRequests.setText(requests + " " + LanguageManager.getInstance().t("main.requests"));
        badgeCarbon.setText(String.format("%.1fg %s", carbonFootprint, LanguageManager.getInstance().t("main.carbonFootprint")));

        // 4. 하단 통계
        tvStatRequests.setText(String.valueOf(requests));

        long minutes = timeSpentMs / 1000 / 60;
        tvStatTime.setText(minutes + LanguageManager.getInstance().t("main.minutes"));

        tvStatTokens.setText(String.format("%.1fk", tokens / 1000.0));

        // 5. 비주얼 업데이트
        applyBackgroundGradient(treeHealth);
        updateTreeImage(treeHealth);
    }

    private void updateStrings() {
        // 레이아웃 내 고정 텍스트들 업데이트 (ID 필요)
        TextView tvTodayUsage = findViewById(R.id.tvTodayUsageBadge);
        if (tvTodayUsage != null) tvTodayUsage.setText(LanguageManager.getInstance().t("main.todayUsage"));

        TextView tvTreeLabel = findViewById(R.id.tvTreeHealthLabel);
        if (tvTreeLabel != null) tvTreeLabel.setText(LanguageManager.getInstance().t("main.treeHealth"));

        TextView tvLabelRequests = findViewById(R.id.tvLabelRequests);
        if (tvLabelRequests != null) tvLabelRequests.setText(LanguageManager.getInstance().t("main.requests"));

        TextView tvLabelTime = findViewById(R.id.tvLabelTime);
        if (tvLabelTime != null) tvLabelTime.setText(LanguageManager.getInstance().t("main.time"));

        TextView tvLabelTokens = findViewById(R.id.tvLabelTokens);
        if (tvLabelTokens != null) tvLabelTokens.setText(LanguageManager.getInstance().t("main.tokens"));

        btnLearnPrompting.setText(LanguageManager.getInstance().t("main.learnPrompting"));
        btnViewUsage.setText(LanguageManager.getInstance().t("main.viewUsage"));

        updateAiUsageUI(); // 값에 붙은 단위 텍스트 등도 갱신
    }

    private int calculateTreeHealth() {
        int baseHealth = 100;
        // 로직: 요청 1회당 2점, 500토큰당 1점 감점 (예시)
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
        imgTreeState.setAlpha(1.0f); // 투명도 초기화

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
            // 싱싱한 초록
            startColor = 0xFFF8F9F5; midColor = 0xFFF0F5EA; endColor = 0xFFE8F2DF;
        } else if (treeHealth >= 60) {
            // 약간 연한 초록
            startColor = 0xFFF8F9F5; midColor = 0xFFF2F4EE; endColor = 0xFFEAEEE2;
        } else if (treeHealth >= 40) {
            // 노르스름 (가을 느낌)
            startColor = 0xFFF5F6F2; midColor = 0xFFF0F1ED; endColor = 0xFFE7E8E4;
        } else if (treeHealth >= 20) {
            // 갈색/회색 톤 (시듦)
            startColor = 0xFFF2F3F0; midColor = 0xFFEDedea; endColor = 0xFFE4E5E2;
        } else {
            // 잿빛 (위험)
            startColor = 0xFFEFEEEF; midColor = 0xFFE8E8E9; endColor = 0xFFE0E0E1;
        }

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, midColor, endColor});
        root.setBackground(gd);
    }

    private void showApiKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("API Key");
        builder.setMessage("Enter your OpenAI API Key");

        final EditText input = new EditText(this);
        input.setHint("sk-...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String key = input.getText().toString().trim();
            if (!key.isEmpty()) {
                preferenceHelper.setApiKey(key);
                fetchUsageFromServer(); // 키 저장 후 즉시 서버 데이터 요청
                Toast.makeText(MainActivity.this, "Key Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
package kr.co.example.treeplz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.example.treeplz.model.AiUsage;

public class UsageDetailsActivity extends AppCompatActivity {
    private PreferenceHelper preferenceHelper;
    public static final String EXTRA_AI_USAGE = "extra_data";

    public static void start(Context context, AiUsage usage) {
        Intent intent = new Intent(context, UsageDetailsActivity.class);
        intent.putExtra(EXTRA_AI_USAGE, usage);
        context.startActivity(intent);
    }

    private int requests;
    private long tokens;
    private long timeSpentMs;
    private double carbon;
    private TextView tvTitle;
    private ToggleButton switchLanguage;

    private TextView tvLabelRequests, tvValueRequests;
    private TextView tvLabelTime, tvValueTimeSpent;
    private TextView tvLabelTokens, tvValueTokens;
    private TextView tvLabelCarbon, tvValueCarbon;

    private TextView tvWeeklyTitle;
    private TextView tvTipsTitle;
    private TextView tvTip1Title, tvTip1Desc;
    private TextView tvTip2Title, tvTip2Desc;
    private TextView tvTip3Title, tvTip3Desc;

    private TextView tvEnvTitle, tvEnvDescription;
    private TextView tvEnvCarValue, tvEnvLightValue, tvEnvPhoneValue;

    private View barMonRequests;
    private TextView tvMonRequestsValue;

    private AiUsage aiUsage;

    @Override
    protected void onResume() {
        super.onResume();

        // ★ 화면 복귀 시도 항상 최신값으로 갱신
        loadTodayUsage();
        bindDataToViews();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_usage);

        preferenceHelper = PreferenceHelper.getInstance(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        initViews();
        setupLanguageToggle();

        loadTodayUsage();
        bindDataToViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            aiUsage = getIntent().getSerializableExtra(EXTRA_AI_USAGE, AiUsage.class);
        } else {
            aiUsage = (AiUsage) getIntent().getSerializableExtra(EXTRA_AI_USAGE);
        }

        if (aiUsage != null) {
            bindDataToViews();
        } else {
            // 더미 데이터
            aiUsage = new AiUsage(320, 162600, 744.0, 595.0);
            bindDataToViews();
        }

        // 초기 텍스트 로드 (LanguageManager 사용)
        updateStrings();
    }

    private void loadTodayUsage() {
        requests = preferenceHelper.getTodayRequests();
        tokens = preferenceHelper.getTodayTokens();
        timeSpentMs = preferenceHelper.getTodayTime();
        carbon = tokens * 0.0002;
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        switchLanguage = findViewById(R.id.switchLanguage);

        tvLabelRequests = findViewById(R.id.tvLabelRequests);
        tvValueRequests = findViewById(R.id.tvValueRequests);
        tvLabelTime = findViewById(R.id.tvLabelTimeSpent);
        tvValueTimeSpent = findViewById(R.id.tvValueTimeSpent);
        tvLabelTokens = findViewById(R.id.tvLabelTokens);
        tvValueTokens = findViewById(R.id.tvValueTokens);
        tvLabelCarbon = findViewById(R.id.tvLabelCarbon);
        tvValueCarbon = findViewById(R.id.tvValueCarbon);

        tvWeeklyTitle = findViewById(R.id.tvWeeklyTitle);
        tvTipsTitle = findViewById(R.id.tvTipsTitle);
        tvTip1Title = findViewById(R.id.tvTip1Title); tvTip1Desc = findViewById(R.id.tvTip1Desc);
        tvTip2Title = findViewById(R.id.tvTip2Title); tvTip2Desc = findViewById(R.id.tvTip2Desc);
        tvTip3Title = findViewById(R.id.tvTip3Title); tvTip3Desc = findViewById(R.id.tvTip3Desc);

        tvEnvTitle = findViewById(R.id.tvEnvTitle);
        tvEnvDescription = findViewById(R.id.tvEnvDescription);
        tvEnvCarValue = findViewById(R.id.tvEnvCarValue);
        tvEnvLightValue = findViewById(R.id.tvEnvLightValue);
        tvEnvPhoneValue = findViewById(R.id.tvEnvPhoneValue);

        barMonRequests = findViewById(R.id.barMonRequests);
        tvMonRequestsValue = findViewById(R.id.tvMonRequestsValue);
    }

    private void setupLanguageToggle() {
        boolean isKo = LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO;
        switchLanguage.setChecked(isKo);

        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                LanguageManager.getInstance().setLanguage(LanguageManager.Language.KO);
            } else {
                LanguageManager.getInstance().setLanguage(LanguageManager.Language.EN);
            }
            // 언어 변경 시 텍스트 즉시 갱신
            updateStrings();
        });
    }

    private void bindDataToViews() {
        // PreferenceHelper에서 업데이트된 값 사용
        tvValueRequests.setText(String.valueOf(requests));

        long minutes = timeSpentMs / 1000 / 60;
        tvValueTimeSpent.setText(minutes + "min");

        tvValueTokens.setText(String.format("%.1fk", tokens / 1000.0));
        tvValueCarbon.setText(String.format("%.1fg", carbon));

        updateEnvImpactValues();
    }


    private void updateEnvImpactValues() {
        // aiUsage.carbonFootprint → carbon 사용
        double c = carbon;

        boolean isKo = LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO;
        String unitCar = isKo ? "운전" : "Driving";
        String unitLight = isKo ? "전구" : "Light";
        String unitCharge = isKo ? "충전" : "Charges";

        tvEnvCarValue.setText(String.format("%.1fm %s", c * 6.0, unitCar));
        tvEnvLightValue.setText(String.format("%.1fh %s", c * 0.1, unitLight));
        tvEnvPhoneValue.setText(String.format("%.1f %s", c * 0.2, unitCharge));
    }


    // ★ 핵심 수정: LanguageManager를 사용하여 텍스트 설정
    private void updateStrings() {
        LanguageManager lm = LanguageManager.getInstance();
        boolean isKo = lm.getLanguage() == LanguageManager.Language.KO;

        // 1. 타이틀 & 요약 카드 라벨 (LanguageManager 키 사용)
        // (LanguageManager에 "Detailed Usage" 키가 없으므로 main.viewUsage를 재활용하거나 수동 처리)
        tvTitle.setText(isKo ? "상세 사용량" : "Detailed Usage");

        tvLabelRequests.setText(lm.t("main.requests"));
        tvLabelTime.setText(lm.t("main.time"));
        tvLabelTokens.setText(lm.t("main.tokens"));
        tvLabelCarbon.setText(lm.t("main.carbonFootprint"));

        // 2. 섹션 타이틀
        tvWeeklyTitle.setText(isKo ? "주간 트렌드" : "Weekly Trend");
        tvTipsTitle.setText(lm.t("prompting.title")); // "Prompting Tips" / "프롬프팅 팁"

        // 3. 팁 내용 (LanguageManager에 있는 실제 팁 키와 매핑)
        // Tip 1: Be Specific (구체적으로 말하기)
        tvTip1Title.setText(lm.t("prompting.beSpecific.title"));
        tvTip1Desc.setText(lm.t("prompting.beSpecific.description"));

        // Tip 2: Batch Requests (일괄 요청하기)
        tvTip2Title.setText(lm.t("prompting.batchRequests.title"));
        tvTip2Desc.setText(lm.t("prompting.batchRequests.description"));

        // Tip 3: Remove Greetings (인사말 생략하기) - "Check Daily" 대신 사용
        tvTip3Title.setText(lm.t("prompting.removeGreetings.title"));
        tvTip3Desc.setText(lm.t("prompting.removeGreetings.description"));

        // 4. 환경 영향 섹션
        tvEnvTitle.setText(isKo ? "환경적 영향" : "Environmental Impact");
        tvEnvDescription.setText(isKo ? "다음과 맞먹는 배출량입니다:" : "Equivalent to...");

        // 값 뒤에 붙는 단위(운전/Driving 등)도 언어에 맞춰 갱신
        updateEnvImpactValues();
    }
}
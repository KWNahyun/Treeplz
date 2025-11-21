package kr.co.example.treeplz;

import android.content.Context;
import android.content.Intent;
import android.os.Build; // 버전 체크를 위해 추가
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Serializable 경고가 뜬다면 무시해도 되는 어노테이션 (선택사항)
@SuppressWarnings("deprecation")
public class UsageDetailsActivity extends AppCompatActivity {

    // 다른 액티비티에서 사용량 데이터를 넘길 때 쓸 키
    public static final String EXTRA_AI_USAGE = "extra_ai_usage";

    // --------- 외부에서 쉽게 호출할 수 있는 helper 메서드 ----------
    public static void start(Context context, AiUsage usage) {
        Intent intent = new Intent(context, UsageDetailsActivity.class);
        intent.putExtra(EXTRA_AI_USAGE, usage);
        context.startActivity(intent);
    }

    // Header
    private ImageButton btnBack;
    private TextView tvTitle;
    private ImageView ivLanguageToggle;

    // Summary cards
    private TextView tvValueRequests;
    private TextView tvSubRequests;
    private TextView tvValueTimeSpent;
    private TextView tvSubTimeSpent;
    private TextView tvValueTokens;
    private TextView tvSubTokens;
    private TextView tvValueCarbon;
    private TextView tvSubCarbon;

    // Weekly trend (Mon 예시)
    private View barMonRequests;
    private View barMonCarbon;
    private TextView tvMonRequestsValue;
    private TextView tvMonCarbonValue;

    // Environmental impact
    private TextView tvEnvDescription;
    private TextView tvEnvCarValue;
    private TextView tvEnvLightValue;
    private TextView tvEnvPhoneValue;

    // 샘플 데이터 구조 (React 의 aiUsage 비슷하게)
    // 다른 액티비티에서도 만들 수 있게 public + Serializable
    public static class AiUsage implements java.io.Serializable {
        // 직렬화 버전 UID (경고 방지용 권장)
        private static final long serialVersionUID = 1L;

        public int requests;
        public int tokens;
        public double timeSpentMinutes;
        public int carbonFootprintGrams;

        public AiUsage(int requests, int tokens, double timeSpentMinutes, int carbonFootprintGrams) {
            this.requests = requests;
            this.tokens = tokens;
            this.timeSpentMinutes = timeSpentMinutes;
            this.carbonFootprintGrams = carbonFootprintGrams;
        }
    }

    private static class DayUsage {
        String dayLabel;  // "Mon" 등
        int requests;
        int carbon;

        DayUsage(String dayLabel, int requests, int carbon) {
            this.dayLabel = dayLabel;
            this.requests = requests;
            this.carbon = carbon;
        }
    }

    private AiUsage aiUsage;
    private DayUsage[] weeklyData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_usage);

        // 1. Intent 데이터 수신 (버전 분기 처리로 Deprecated 해결)
        Intent intent = getIntent();
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 안드로이드 13(API 33) 이상: 타입을 명시해야 함
                aiUsage = intent.getSerializableExtra(EXTRA_AI_USAGE, AiUsage.class);
            } else {
                // 구버전 안드로이드: 기존 방식 유지
                aiUsage = (AiUsage) intent.getSerializableExtra(EXTRA_AI_USAGE);
            }
        }

        // 2. 없으면 더미 데이터 사용 / 있으면 그 값으로 주간 데이터 구성
        if (aiUsage == null) {
            initMockData();
        } else {
            initWeeklyDataFromAiUsage();
        }

        // 3. 뷰 바인딩
        bindViews();

        // 4. 카드 / 텍스트 채우기
        bindSummaryCards();
        bindWeeklyTrend();
        bindEnvironmentalImpact();

        // 5. 리스너 설정
        setupListeners();
    }

    // 기본 더미 데이터 (테스트용)
    private void initMockData() {
        aiUsage = new AiUsage(
                320,    // requests
                162600, // tokens
                744,    // minutes
                595     // grams CO2
        );
        initWeeklyDataFromAiUsage();
    }

    // aiUsage 값을 기준으로 week 데이터 구성
    private void initWeeklyDataFromAiUsage() {
        weeklyData = new DayUsage[]{
                new DayUsage("Mon", 12, 15),
                new DayUsage("Tue", 8, 10),
                new DayUsage("Wed", 15, 18),
                new DayUsage("Thu", 6, 8),
                new DayUsage("Fri", 20, 25),
                new DayUsage("Sat", 4, 5),
                // 일요일은 항상 현재 aiUsage 사용량 (예시)
                new DayUsage("Sun", aiUsage.requests, aiUsage.carbonFootprintGrams)
        };
    }

    private void bindViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        ivLanguageToggle = findViewById(R.id.ivLanguageToggle);

        // Summary cards
        tvValueRequests = findViewById(R.id.tvValueRequests);
        tvSubRequests = findViewById(R.id.tvSubRequests);
        tvValueTimeSpent = findViewById(R.id.tvValueTimeSpent);
        tvSubTimeSpent = findViewById(R.id.tvSubTimeSpent);
        tvValueTokens = findViewById(R.id.tvValueTokens);
        tvSubTokens = findViewById(R.id.tvSubTokens);
        tvValueCarbon = findViewById(R.id.tvValueCarbon);
        tvSubCarbon = findViewById(R.id.tvSubCarbon);

        // Weekly trend
        barMonRequests = findViewById(R.id.barMonRequests);
        barMonCarbon = findViewById(R.id.barMonCarbon);
        tvMonRequestsValue = findViewById(R.id.tvMonRequestsValue);
        tvMonCarbonValue = findViewById(R.id.tvMonCarbonValue);

        // Environmental impact
        tvEnvDescription = findViewById(R.id.tvEnvDescription);
        tvEnvCarValue = findViewById(R.id.tvEnvCarValue);
        tvEnvLightValue = findViewById(R.id.tvEnvLightValue);
        tvEnvPhoneValue = findViewById(R.id.tvEnvPhoneValue);
    }

    private void bindSummaryCards() {
        // Requests
        tvValueRequests.setText(String.valueOf(aiUsage.requests));

        // Time spent
        String timeValue = (int) Math.round(aiUsage.timeSpentMinutes) + "min";
        tvValueTimeSpent.setText(timeValue);

        // Tokens (162.6k)
        double tokensK = aiUsage.tokens / 1000.0;
        String tokensText = String.format("%.1fk", tokensK);
        tvValueTokens.setText(tokensText);

        // Carbon
        String carbonText = aiUsage.carbonFootprintGrams + "g";
        tvValueCarbon.setText(carbonText);
    }

    private void bindWeeklyTrend() {
        // Mon 데이터만 일단 사용 (XML에 월요일만 있어서)
        if (weeklyData == null || weeklyData.length == 0) return;

        DayUsage monday = weeklyData[0];

        tvMonRequestsValue.setText(String.valueOf(monday.requests));
        tvMonCarbonValue.setText(String.valueOf(monday.carbon));

        // 최대값 계산해서 bar 비율 정하기
        int maxRequests = 0;
        int maxCarbon = 0;
        for (DayUsage d : weeklyData) {
            if (d.requests > maxRequests) maxRequests = d.requests;
            if (d.carbon > maxCarbon) maxCarbon = d.carbon;
        }
        final int finalMaxRequests = Math.max(maxRequests, 1);
        final int finalMaxCarbon = Math.max(maxCarbon, 1);

        // 레이아웃이 그려진 뒤에 부모 폭을 이용해서 bar width 조정
        barMonRequests.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        barMonRequests.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        View parentReq = (View) barMonRequests.getParent();
                        int trackWidth = parentReq.getWidth();

                        float ratioReq = (float) monday.requests / finalMaxRequests;
                        int barWidthReq = (int) (trackWidth * ratioReq);

                        LinearLayout.LayoutParams lpReq = (LinearLayout.LayoutParams) barMonRequests.getLayoutParams();
                        lpReq.width = barWidthReq;
                        barMonRequests.setLayoutParams(lpReq);

                        View parentCarbon = (View) barMonCarbon.getParent();
                        int trackWidthCarbon = parentCarbon.getWidth();

                        float ratioCarbon = (float) monday.carbon / finalMaxCarbon;
                        int barWidthCarbon = (int) (trackWidthCarbon * ratioCarbon);

                        LinearLayout.LayoutParams lpCarbon = (LinearLayout.LayoutParams) barMonCarbon.getLayoutParams();
                        lpCarbon.width = barWidthCarbon;
                        barMonCarbon.setLayoutParams(lpCarbon);
                    }
                });
    }

    private void bindEnvironmentalImpact() {
        // Equivalent to 595g CO₂
        // strings.xml에 usage_env_equivalent가 정의되어 있어야 함
        // 없다면 임시로 아래처럼 하드코딩 가능:
        // String desc = "Equivalent to " + aiUsage.carbonFootprintGrams + "g CO₂:";
        String desc = getString(R.string.usage_env_equivalent, aiUsage.carbonFootprintGrams);
        tvEnvDescription.setText(desc);

        double carMeters = aiUsage.carbonFootprintGrams * 0.006;
        double ledHours = aiUsage.carbonFootprintGrams * 0.1;
        double phoneCharges = aiUsage.carbonFootprintGrams * 0.2;

        tvEnvCarValue.setText(String.format("%.1fm", carMeters));
        tvEnvLightValue.setText(String.format("%.1fh", ledHours));
        tvEnvPhoneValue.setText(String.format("%.1f", phoneCharges));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        ivLanguageToggle.setOnClickListener(v -> {
            // 언어 토글 로직 구현 필요시 여기에 작성
        });
    }
}
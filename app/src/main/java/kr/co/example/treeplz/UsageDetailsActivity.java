package kr.co.example.treeplz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("deprecation")
public class UsageDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_AI_USAGE = "extra_ai_usage";

    public static void start(Context context, AiUsage usage) {
        Intent intent = new Intent(context, UsageDetailsActivity.class);
        intent.putExtra(EXTRA_AI_USAGE, usage);
        context.startActivity(intent);
    }

    private ImageButton btnBack;
    private TextView tvTitle;
    private ToggleButton tbLanguageToggle;

    private TextView tvValueRequests, tvSubRequests, tvLabelRequests;
    private TextView tvValueTimeSpent, tvSubTimeSpent, tvLabelTimeSpent;
    private TextView tvValueTokens, tvSubTokens, tvLabelTokens;
    private TextView tvValueCarbon, tvSubCarbon, tvLabelCarbon;

    private View barMonRequests, barMonCarbon;
    private TextView tvMonRequestsValue, tvMonCarbonValue;
    private TextView tvWeeklyTitle, tvWeeklySubtitle;

    private TextView tvEnvDescription, tvEnvTitle;
    private TextView tvEnvCarValue, tvEnvLightValue, tvEnvPhoneValue;
    private TextView tvEnvCarLabel, tvEnvLightLabel, tvEnvPhoneLabel;
    private TextView tvTipsTitle, tvTip1Title, tvTip1Desc, tvTip2Title, tvTip2Desc, tvTip3Title, tvTip3Desc;
    private TextView tvLabelMon, tvLabelToday;
    public static class AiUsage implements java.io.Serializable {
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
        String dayLabel;
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

        Intent intent = getIntent();
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                aiUsage = intent.getSerializableExtra(EXTRA_AI_USAGE, AiUsage.class);
            } else {
                aiUsage = (AiUsage) intent.getSerializableExtra(EXTRA_AI_USAGE);
            }
        }

        if (aiUsage == null) {
            initMockData();
        } else {
            initWeeklyDataFromAiUsage();
        }

        bindViews();
        updateTexts();
        bindSummaryCards();
        bindWeeklyTrend();
        setupListeners();
    }

    private void initMockData() {
        aiUsage = new AiUsage(320, 162600, 744, 595);
        initWeeklyDataFromAiUsage();
    }

    private void initWeeklyDataFromAiUsage() {
        weeklyData = new DayUsage[]{
                new DayUsage("Mon", 12, 15),
                new DayUsage("Tue", 8, 10),
                new DayUsage("Wed", 15, 18),
                new DayUsage("Thu", 6, 8),
                new DayUsage("Fri", 20, 25),
                new DayUsage("Sat", 4, 5),
                new DayUsage("Sun", aiUsage.requests, aiUsage.carbonFootprintGrams)
        };
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tbLanguageToggle = findViewById(R.id.ivLanguageToggle);

        tvValueRequests = findViewById(R.id.tvValueRequests);
        tvSubRequests = findViewById(R.id.tvSubRequests);
        tvLabelRequests = findViewById(R.id.tvLabelRequests);

        tvValueTimeSpent = findViewById(R.id.tvValueTimeSpent);
        tvSubTimeSpent = findViewById(R.id.tvSubTimeSpent);
        tvLabelTimeSpent = findViewById(R.id.tvLabelTimeSpent);

        tvValueTokens = findViewById(R.id.tvValueTokens);
        tvSubTokens = findViewById(R.id.tvSubTokens);
        tvLabelTokens = findViewById(R.id.tvLabelTokens);

        tvValueCarbon = findViewById(R.id.tvValueCarbon);
        tvSubCarbon = findViewById(R.id.tvSubCarbon);
        tvLabelCarbon = findViewById(R.id.tvLabelCarbon);

        barMonRequests = findViewById(R.id.barMonRequests);
        barMonCarbon = findViewById(R.id.barMonCarbon);
        tvMonRequestsValue = findViewById(R.id.tvMonRequestsValue);
        tvMonCarbonValue = findViewById(R.id.tvMonCarbonValue);
        tvWeeklyTitle = findViewById(R.id.tvWeeklyTitle);
        tvWeeklySubtitle = findViewById(R.id.tvWeeklySubtitle);

        tvEnvDescription = findViewById(R.id.tvEnvDescription);
        tvEnvCarValue = findViewById(R.id.tvEnvCarValue);
        tvEnvLightValue = findViewById(R.id.tvEnvLightValue);
        tvEnvPhoneValue = findViewById(R.id.tvEnvPhoneValue);

        tvEnvCarLabel = findViewById(R.id.tvEnvCarLabel);
        tvEnvLightLabel = findViewById(R.id.tvEnvLightLabel);
        tvEnvPhoneLabel = findViewById(R.id.tvEnvPhoneLabel);
        tvEnvTitle = findViewById(R.id.tvEnvTitle);

        tvTipsTitle = findViewById(R.id.tvTipsTitle);
        tvTip1Title = findViewById(R.id.tvTip1Title);
        tvTip1Desc = findViewById(R.id.tvTip1Desc);
        tvTip2Title = findViewById(R.id.tvTip2Title);
        tvTip2Desc = findViewById(R.id.tvTip2Desc);
        tvTip3Title = findViewById(R.id.tvTip3Title);
        tvTip3Desc = findViewById(R.id.tvTip3Desc);

        tvLabelMon = findViewById(R.id.tvLabelMon);
        tvLabelToday = findViewById(R.id.tvLabelToday);
    }

    private void updateTexts() {
        LanguageManager lm = LanguageManager.getInstance();
        tvTitle.setText(lm.t("usage.title"));
        tvLabelRequests.setText(lm.t("usage.requests"));
        tvSubRequests.setText(lm.t("main.todayUsage"));
        tvLabelTimeSpent.setText(lm.t("usage.time_spent"));
        tvSubTimeSpent.setText(lm.t("usage.minutes"));
        tvLabelTokens.setText(lm.t("usage.tokens"));
        tvSubTokens.setText(lm.t("main.tokens"));
        tvLabelCarbon.setText(lm.t("usage.carbon_footprint"));
        tvSubCarbon.setText(lm.t("main.carbonFootprint"));
        tvWeeklyTitle.setText(lm.t("usage.weekly_trend"));
        tvWeeklySubtitle.setText(lm.t("usage.carbon_with_unit"));
        tvEnvTitle.setText(lm.t("usage.environmental_impact"));
        tvEnvCarLabel.setText(lm.t("usage.envCar"));
        tvEnvLightLabel.setText(lm.t("usage.envLight"));
        tvEnvPhoneLabel.setText(lm.t("usage.envPhone"));

        tvLabelMon.setText(lm.t("usage.mon"));
        tvLabelToday.setText(lm.t("usage.today"));
        
        tvTipsTitle.setText(lm.t("usage.tips"));
        tvTip1Title.setText(lm.t("usage.tip1"));
        tvTip1Desc.setText(lm.t("usage.tip1_desc"));
        tvTip2Title.setText(lm.t("usage.tip2"));
        tvTip2Desc.setText(lm.t("usage.tip2_desc"));
        tvTip3Title.setText(lm.t("usage.tip3"));
        tvTip3Desc.setText(lm.t("usage.tip3_desc"));

        bindEnvironmentalImpact();
    }

    private void bindSummaryCards() {
        tvValueRequests.setText(String.valueOf(aiUsage.requests));
        String timeValue = (int) Math.round(aiUsage.timeSpentMinutes) + "min";
        tvValueTimeSpent.setText(timeValue);
        double tokensK = aiUsage.tokens / 1000.0;
        String tokensText = String.format("%.1fk", tokensK);
        tvValueTokens.setText(tokensText);
        String carbonText = aiUsage.carbonFootprintGrams + "g";
        tvValueCarbon.setText(carbonText);
    }

    private void bindWeeklyTrend() {
        if (weeklyData == null || weeklyData.length == 0) return;
        DayUsage monday = weeklyData[0];
        tvMonRequestsValue.setText(String.valueOf(monday.requests));
        tvMonCarbonValue.setText(String.valueOf(monday.carbon));

        int maxRequests = 0;
        int maxCarbon = 0;
        for (DayUsage d : weeklyData) {
            if (d.requests > maxRequests) maxRequests = d.requests;
            if (d.carbon > maxCarbon) maxCarbon = d.carbon;
        }
        final int finalMaxRequests = Math.max(maxRequests, 1);
        final int finalMaxCarbon = Math.max(maxCarbon, 1);

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
        String desc = String.format(LanguageManager.getInstance().t("usage.envDescription"), aiUsage.carbonFootprintGrams);
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

        tbLanguageToggle.setChecked(LanguageManager.getInstance().getLanguage() == LanguageManager.Language.KO);

        tbLanguageToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LanguageManager.getInstance().setLanguage(isChecked ? LanguageManager.Language.KO : LanguageManager.Language.EN);
            updateTexts();
        });
    }
}
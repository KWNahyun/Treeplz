package kr.co.example.treeplz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.example.treeplz.model.AiUsage;

public class UsageDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_AI_USAGE = "extra_data"; // 키 이름 통일

    // Helper method
    public static void start(Context context, AiUsage usage) {
        Intent intent = new Intent(context, UsageDetailsActivity.class);
        intent.putExtra(EXTRA_AI_USAGE, usage);
        context.startActivity(intent);
    }

    private TextView tvValueRequests;
    private TextView tvValueTimeSpent;
    private TextView tvValueTokens;
    private TextView tvValueCarbon;

    private TextView tvEnvDescription;
    private TextView tvEnvCarValue;
    private TextView tvEnvLightValue;
    private TextView tvEnvPhoneValue;

    private View barMonRequests;
    private View barMonCarbon;
    private TextView tvMonRequestsValue;
    private TextView tvMonCarbonValue;

    private AiUsage aiUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_usage);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 뷰 초기화
        initViews();

        // 1. 데이터 수신 (버전별 처리)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            aiUsage = getIntent().getSerializableExtra(EXTRA_AI_USAGE, AiUsage.class);
        } else {
            aiUsage = (AiUsage) getIntent().getSerializableExtra(EXTRA_AI_USAGE);
        }

        // 2. 데이터가 있으면 화면 갱신
        if (aiUsage != null) {
            bindDataToViews();
        } else {
            // 데이터 없으면 더미 데이터로 테스트 (선택사항)
            aiUsage = new AiUsage(320, 162600, 744.0, 595.0);
            bindDataToViews();
        }

        // 주간 트렌드 (더미)
        bindWeeklyTrend();
    }

    private void initViews() {
        tvValueRequests = findViewById(R.id.tvValueRequests);
        tvValueTimeSpent = findViewById(R.id.tvValueTimeSpent);
        tvValueTokens = findViewById(R.id.tvValueTokens);
        tvValueCarbon = findViewById(R.id.tvValueCarbon);

        tvEnvDescription = findViewById(R.id.tvEnvDescription);
        tvEnvCarValue = findViewById(R.id.tvEnvCarValue);
        tvEnvLightValue = findViewById(R.id.tvEnvLightValue);
        tvEnvPhoneValue = findViewById(R.id.tvEnvPhoneValue);

        barMonRequests = findViewById(R.id.barMonRequests);
        barMonCarbon = findViewById(R.id.barMonCarbon);
        tvMonRequestsValue = findViewById(R.id.tvMonRequestsValue);
        tvMonCarbonValue = findViewById(R.id.tvMonCarbonValue);
    }

    private void bindDataToViews() {
        // [수정] 변수 이름 불일치 해결: model/AiUsage의 필드명 사용

        // 1. Requests
        tvValueRequests.setText(String.valueOf(aiUsage.requests));

        // 2. Time (분 단위 가정)
        int minutes = (int) Math.round(aiUsage.timeSpent);
        tvValueTimeSpent.setText(minutes + "min");

        // 3. Tokens (1000단위 k)
        tvValueTokens.setText(String.format("%.1fk", aiUsage.tokens / 1000.0));

        // 4. Carbon
        tvValueCarbon.setText(String.format("%.1fg", aiUsage.carbonFootprint));


        // 5. Environmental Impact
        // (res/values/strings.xml에 usage_env_equivalent 없으면 하드코딩)
        String desc = "Equivalent to " + (int)aiUsage.carbonFootprint + "g CO₂:";
        tvEnvDescription.setText(desc);

        double carbon = aiUsage.carbonFootprint;
        tvEnvCarValue.setText(String.format("%.1fm", carbon * 6.0));
        tvEnvLightValue.setText(String.format("%.1fh", carbon * 0.1));
        tvEnvPhoneValue.setText(String.format("%.1f", carbon * 0.2));
    }

    // 주간 트렌드 (더미 데이터 표시용)
    private void bindWeeklyTrend() {
        // 만약 XML에 해당 뷰가 없으면(아까 수정한 XML에는 없을 수 있음) 앱이 죽지 않게 null 체크
        if (barMonRequests == null) return;

        tvMonRequestsValue.setText("12");
        tvMonCarbonValue.setText("15");

        // 간단하게 width 조정 (애니메이션 없이)
        // 복잡한 ViewTreeObserver 로직 대신 post 사용
        barMonRequests.post(() -> {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) barMonRequests.getLayoutParams();
            lp.width = 100; // 임의 값
            barMonRequests.setLayoutParams(lp);
        });
    }
}
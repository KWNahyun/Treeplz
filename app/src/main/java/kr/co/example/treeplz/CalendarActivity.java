package kr.co.example.treeplz;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView monthTitle;
    private GridLayout calendarGrid;
    private ImageButton btnPrevMonth, btnNextMonth;

    // Detail Panel Components
    private CardView detailPanel;
    private TextView detailDateTitle;
    private TextView detailRequests;
    private TextView detailTokens;
    private TextView detailCarbon;
    private TextView detailHealth;

    private Calendar currentMonth;
    private List<DayData> calendarData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar); // XML 파일명 확인

        initViews();

        currentMonth = Calendar.getInstance();
        refreshCalendar();
    }

    private void initViews() {
        monthTitle = findViewById(R.id.monthTitle);
        calendarGrid = findViewById(R.id.calendarGrid);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        detailPanel = findViewById(R.id.detailPanel);
        detailDateTitle = findViewById(R.id.detailDateTitle);
        detailRequests = findViewById(R.id.detailRequests);
        detailTokens = findViewById(R.id.detailTokens);
        detailCarbon = findViewById(R.id.detailCarbon);
        detailHealth = findViewById(R.id.detailHealth);

        // 월 이동 버튼 리스너
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            refreshCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            refreshCalendar();
        });
    }

    private void refreshCalendar() {
        // 1. 제목 설정 (Ex: November 2025)
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        monthTitle.setText(sdf.format(currentMonth.getTime()));

        // 2. 데이터 생성 (월이 바뀔 때마다 새로 생성한다고 가정)
        generateMockData();

        // 3. 그리드 그리기
        buildCalendarGrid();

        // 4. 패널 숨기기
        detailPanel.setVisibility(View.INVISIBLE);
    }

    private void generateMockData() {
        calendarData = new ArrayList<>();
        Calendar temp = (Calendar) currentMonth.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            boolean hasData = Math.random() > 0.3;
            if (hasData) {
                int requests = (int) (Math.random() * 50) + 5;
                int tokens = (int) (Math.random() * 10000) + 1000;
                double timeSpent = Math.random() * 60 + 5;
                int carbon = (int) (tokens * 0.02); // 임의 계산
                int health = Math.max(0, 100 - (requests + (int)(tokens/500.0)));

                calendarData.add(new DayData(i, true, requests, tokens, timeSpent, carbon, health));
            } else {
                calendarData.add(new DayData(i, false));
            }
        }
    }

    private void buildCalendarGrid() {
        calendarGrid.removeAllViews();

        Calendar temp = (Calendar) currentMonth.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);

        // 1일의 요일 (1:Sun ~ 7:Sat)
        int dayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
        int emptyCells = dayOfWeek - 1;

        // 빈 칸 채우기
        for (int i = 0; i < emptyCells; i++) {
            TextView empty = new TextView(this);
            calendarGrid.addView(empty, getGridParams());
        }

        // 날짜 칸 채우기
        for (DayData day : calendarData) {
            TextView dayView = new TextView(this);
            dayView.setText(String.valueOf(day.date));
            dayView.setTextSize(14);
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextColor(Color.DKGRAY);

            // 둥근 배경 만들기 (프로그래매틱 Drawable)
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL); // 혹은 RECTANGLE + setCornerRadius

            if (day.hasData) {
                bg.setColor(getHealthColor(day.healthLevel));
                // 텍스트 색상을 흰색으로 해야 잘 보임
                dayView.setTextColor(Color.WHITE);
            } else {
                bg.setColor(Color.TRANSPARENT); // 데이터 없으면 투명
            }

            // 사이즈 조정 (작은 원형)
            bg.setSize(100, 100);
            dayView.setBackground(bg);

            // 클릭 이벤트
            dayView.setOnClickListener(v -> showDayDetail(day));

            calendarGrid.addView(dayView, getGridParams());
        }
    }

    private GridLayout.LayoutParams getGridParams() {
        // 각 셀이 1:1 비율로 균등하게 퍼지도록 설정
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 120; // 높이 고정 (적절히 조절)
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        return params;
    }

    private void showDayDetail(DayData day) {
        if (!day.hasData) return;

        detailPanel.setVisibility(View.VISIBLE);

        // 날짜 표시
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        String monthName = sdf.format(currentMonth.getTime());
        detailDateTitle.setText(monthName + " " + day.date);

        detailRequests.setText(String.valueOf(day.requests));
        detailTokens.setText(String.format("%.1fk", day.tokens / 1000.0));
        detailCarbon.setText(day.carbonFootprint + "g");

        detailHealth.setText("Tree Health: " + day.healthLevel + "%");
        detailHealth.setTextColor(getHealthColor(day.healthLevel));
    }

    private int getHealthColor(int h) {
        // colors.xml에 있는 색상 코드 직접 사용 (Color.parseColor)
        if (h >= 80) return Color.parseColor("#2E7D32"); // seed_green
        if (h >= 60) return Color.parseColor("#4CAF50"); // seed_green_light
        if (h >= 40) return Color.parseColor("#81C784"); // accent_leaf
        if (h >= 20) return Color.parseColor("#FFB74D"); // 약간 경고색 (Orange)
        return Color.parseColor("#E57373"); // 위험 (Red)
    }

    // 데이터 모델 (기존과 동일)
    public static class DayData {
        int date;
        boolean hasData;
        int requests;
        int tokens;
        double timeSpent;
        int carbonFootprint;
        int healthLevel;

        public DayData(int date, boolean hasData) {
            this.date = date;
            this.hasData = hasData;
        }

        public DayData(int date, boolean hasData, int requests, int tokens,
                       double timeSpent, int carbonFootprint, int healthLevel) {
            this.date = date;
            this.hasData = hasData;
            this.requests = requests;
            this.tokens = tokens;
            this.timeSpent = timeSpent;
            this.carbonFootprint = carbonFootprint;
            this.healthLevel = healthLevel;
        }
    }
}
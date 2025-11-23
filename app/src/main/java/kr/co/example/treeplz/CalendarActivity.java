package kr.co.example.treeplz;

import android.content.res.ColorStateList;
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
    private TextView tvSelectDateHint; // [추가됨] 안내 문구
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
        setContentView(R.layout.calendar);

        initViews();

        currentMonth = Calendar.getInstance();
        refreshCalendar();
    }

    private void initViews() {
        monthTitle = findViewById(R.id.monthTitle);
        calendarGrid = findViewById(R.id.calendarGrid);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        // [추가됨] XML에 새로 만든 뷰 연결
        tvSelectDateHint = findViewById(R.id.tvSelectDateHint);

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

        // 2. 데이터 생성
        generateMockData();

        // 3. 그리드 그리기
        buildCalendarGrid();

        // 4. [변경] 패널은 숨기고, 안내 문구(Hint)를 보여줍니다.
        detailPanel.setVisibility(View.GONE);
        tvSelectDateHint.setVisibility(View.VISIBLE);
    }

    private void generateMockData() {
        calendarData = new ArrayList<>();
        Calendar temp = (Calendar) currentMonth.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            boolean hasData = Math.random() > 0.4; // 데이터가 있을 확률
            if (hasData) {
                int requests = (int) (Math.random() * 50) + 5;
                int tokens = (int) (Math.random() * 10000) + 1000;
                double timeSpent = Math.random() * 60 + 5;
                int carbon = (int) (tokens * 0.02);
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
            dayView.setTextColor(Color.parseColor("#555555")); // 기본 날짜 색

            // 둥근 배경 만들기
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);

            if (day.hasData) {
                bg.setColor(getHealthColor(day.healthLevel));
                dayView.setTextColor(Color.WHITE); // 데이터 있으면 흰색 글씨
            } else {
                bg.setColor(Color.TRANSPARENT);
            }

            bg.setSize(100, 100);
            dayView.setBackground(bg);

            dayView.setOnClickListener(v -> showDayDetail(day));

            calendarGrid.addView(dayView, getGridParams());
        }
    }

    private GridLayout.LayoutParams getGridParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 120; // 셀 높이
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        return params;
    }

    private void showDayDetail(DayData day) {
        if (!day.hasData) return;

        // [변경] 힌트는 숨기고 패널을 보여줌
        tvSelectDateHint.setVisibility(View.GONE);
        detailPanel.setVisibility(View.VISIBLE);

        // 날짜 포맷 (Ex: Nov 15)
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        String monthStr = monthFormat.format(currentMonth.getTime());
        detailDateTitle.setText(monthStr + " " + day.date);

        // 수치 설정
        detailRequests.setText(String.valueOf(day.requests));
        detailTokens.setText(String.format("%.1fk", day.tokens / 1000.0));
        detailCarbon.setText(day.carbonFootprint + "g");

        // 건강 상태 텍스트 및 배지 색상 변경
        detailHealth.setText(getHealthText(day.healthLevel));

        // [중요] 배지 배경색 변경 (Android Lollipop 이상)
        detailHealth.setBackgroundTintList(
                ColorStateList.valueOf(getHealthColor(day.healthLevel))
        );
    }

    private String getHealthText(int h) {
        if (h >= 80) return "Thriving 🌿";
        if (h >= 60) return "Healthy 🌱";
        if (h >= 40) return "Declining 🍂";
        if (h >= 20) return "Wilting 🥀";
        return "Critical ⚠️";
    }

    private int getHealthColor(int h) {
        if (h >= 80) return Color.parseColor("#2E7D32"); // 진한 초록
        if (h >= 60) return Color.parseColor("#4CAF50"); // 초록
        if (h >= 40) return Color.parseColor("#FFB74D"); // 주황
        if (h >= 20) return Color.parseColor("#FF8A65"); // 다홍
        return Color.parseColor("#E57373"); // 빨강
    }

    // 데이터 모델
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
package kr.co.example.treeplz;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private TextView monthTitle;
    private GridLayout calendarGrid;

    private View detailPanel;
    private TextView detailRequests;
    private TextView detailTokens;
    private TextView detailTime;
    private TextView detailCarbon;
    private TextView detailHealth;

    private Calendar currentMonth;
    private List<DayData> calendarData;
    private Integer selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        // View binding
        monthTitle = findViewById(R.id.monthTitle);
        calendarGrid = findViewById(R.id.calendarGrid);

        detailPanel = findViewById(R.id.detailPanel);
        detailRequests = findViewById(R.id.detailRequests);
        detailTokens = findViewById(R.id.detailTokens);
        detailTime = findViewById(R.id.detailTime);
        detailCarbon = findViewById(R.id.detailCarbon);
        detailHealth = findViewById(R.id.detailHealth);

        // 날짜 객체 생성
        currentMonth = Calendar.getInstance();

        generateMockData();
        buildCalendar();
    }

    /** 캘린더 날짜 데이터 생성 (React 코드와 동일 로직) */
    private void generateMockData() {
        calendarData = new ArrayList<>();

        Calendar temp = (Calendar) currentMonth.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            boolean hasData = Math.random() > 0.3; // 70% 확률로 데이터 존재

            if (hasData) {
                int requests = (int) (Math.random() * 25) + 1;
                int tokens = (int) (Math.random() * 8000) + 1000;
                double timeSpent = Math.random() * 60 + 5;
                int carbon = (int) (Math.random() * 40) + 5;
                int health = Math.max(0, 100 - (requests * 2 + tokens / 1000));

                calendarData.add(new DayData(i, true, requests, tokens, timeSpent, carbon, health));
            } else {
                calendarData.add(new DayData(i, false));
            }
        }
    }

    /** UI에 캘린더 표시 */
    private void buildCalendar() {
        monthTitle.setText(android.text.format.DateFormat.format("MMMM yyyy", currentMonth));

        calendarGrid.removeAllViews();
        calendarGrid.setColumnCount(7);

        Calendar temp = Calendar.getInstance();
        temp.set(currentMonth.get(Calendar.YEAR), currentMonth.get(Calendar.MONTH), 1);

        int offset = temp.get(Calendar.DAY_OF_WEEK) - 1;

        // 빈칸
        for (int i = 0; i < offset; i++) {
            TextView empty = new TextView(this);
            calendarGrid.addView(empty, getGridParams());
        }

        // 날짜 칸 생성
        for (DayData day : calendarData) {
            TextView tv = new TextView(this);
            tv.setText(String.valueOf(day.date));
            tv.setTextSize(16);
            tv.setPadding(10, 10, 10, 10);
            tv.setGravity(Gravity.CENTER);

            // 클릭 시 상세 패널 표시
            tv.setOnClickListener(v -> {
                selectedDate = day.date;
                showDayDetail(day);
            });

            // 데이터가 있으면 health 색으로 칠함
            if (day.hasData) {
                tv.setBackgroundColor(getHealthColor(day.healthLevel));
            }

            calendarGrid.addView(tv, getGridParams());
        }
    }

    private GridLayout.LayoutParams getGridParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(5, 5, 5, 5);
        return params;
    }

    /** 날짜 클릭 시 상세 정보 표시 */
    private void showDayDetail(DayData day) {
        detailPanel.setVisibility(View.VISIBLE);

        if (!day.hasData) {
            detailRequests.setText("No Data");
            detailTokens.setText("-");
            detailTime.setText("-");
            detailCarbon.setText("-");
            detailHealth.setText("-");
            return;
        }

        detailRequests.setText(
                getString(R.string.label_requests, day.requests)
        );
        detailTokens.setText(
                getString(R.string.label_tokens, day.tokens)
        );

        detailHealth.setTextColor(getHealthColor(day.healthLevel));
    }

    /** health 색상 계산 */
    private int getHealthColor(int h) {
        if (h >= 80) return Color.parseColor("#5a7c65");
        if (h >= 60) return Color.parseColor("#8b9677");
        if (h >= 40) return Color.parseColor("#c4b878");
        if (h >= 20) return Color.parseColor("#d4c892");
        return Color.parseColor("#8b7355");
    }

    /** 날짜 데이터 모델 */
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

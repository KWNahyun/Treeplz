package kr.co.example.treeplz;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreferenceHelper {
    private static final String PREF_NAME = "treeplz_prefs";
    private static PreferenceHelper instance;

    private final SharedPreferences prefs;
    private final SimpleDateFormat dateFormat;

    private PreferenceHelper(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    }

    public static synchronized PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    public void setApiKey(String key) {
        prefs.edit().putString("openai_api_key", key).apply();
    }

    public String getApiKey() {
        return prefs.getString("openai_api_key", null);
    }

    public void addUsage(long tokens, long timeMs) {
        checkAndResetDate();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("today_tokens", getTodayTokens() + tokens);
        editor.putInt("today_requests", getTodayRequests() + 1);
        editor.putLong("today_time", getTodayTime() + timeMs);
        editor.apply();
    }

    // [추가됨] 오늘 사용량 강제 초기화 (나무 회복용)
    public void clearTodayData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("today_tokens", 0);
        editor.putInt("today_requests", 0);
        editor.putLong("today_time", 0);
        editor.apply();
    }

    public long getTodayTokens() {
        checkAndResetDate();
        return prefs.getLong("today_tokens", 0);
    }

    public int getTodayRequests() {
        checkAndResetDate();
        return prefs.getInt("today_requests", 0);
    }

    public long getTodayTime() {
        checkAndResetDate();
        return prefs.getLong("today_time", 0);
    }

    private void checkAndResetDate() {
        String today = dateFormat.format(new Date());
        String lastSavedDate = prefs.getString("last_saved_date", "");

        if (!lastSavedDate.equals(today)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_saved_date", today);
            editor.putLong("today_tokens", 0);
            editor.putInt("today_requests", 0);
            editor.putLong("today_time", 0);
            editor.apply();
        }
    }
}
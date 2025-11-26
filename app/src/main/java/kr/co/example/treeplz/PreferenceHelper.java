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

    // --- [추가됨] 회원가입 & 로그인 관련 메서드 ---

    // 1. 회원가입 (정보 저장)
    public void registerUser(String email, String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        editor.apply();
    }

    // 2. 로그인 검증 (저장된 정보와 일치하는지 확인)
    public boolean validateUser(String email, String password) {
        String savedEmail = prefs.getString("user_email", "");
        String savedPassword = prefs.getString("user_password", "");

        // 저장된 정보가 없으면 false
        if (savedEmail.isEmpty()) return false;

        return savedEmail.equals(email) && savedPassword.equals(password);
    }

    // 3. 가입된 계정이 있는지 확인
    public boolean hasRegisteredUser() {
        return !prefs.getString("user_email", "").isEmpty();
    }

    // -------------------------------------------

    public void setApiKey(String key) {
        prefs.edit().putString("openai_api_key", key).apply();
    }

    public String getApiKey() {
        return prefs.getString("openai_api_key", null);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        prefs.edit().putBoolean("is_logged_in", isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("is_logged_in", false);
    }

    public void addUsage(long tokens, long timeMs) {
        checkAndResetDate();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("today_tokens", getTodayTokens() + tokens);
        editor.putInt("today_requests", getTodayRequests() + 1);
        editor.putLong("today_time", getTodayTime() + timeMs);
        editor.apply();
    }

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

    public void setTodayRequests(int value) {
        prefs.edit().putInt("today_requests", value).apply();
    }

    public void setTodayTokens(long value) {
        prefs.edit().putLong("today_tokens", value).apply();
    }

    public void setTodayTime(long value) {
        prefs.edit().putLong("today_time", value).apply();
    }

}
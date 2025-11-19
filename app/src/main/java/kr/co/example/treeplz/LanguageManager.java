package kr.co.example.treeplz;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    public enum Language {
        EN, KO
    }

    // 🔹 싱글톤 인스턴스
    private static final LanguageManager INSTANCE = new LanguageManager();

    public static LanguageManager getInstance() {
        return INSTANCE;
    }

    private Language language = Language.EN;
    private Map<String, String> en;
    private Map<String, String> ko;

    private LanguageManager() {
        initTranslations();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language lang) {
        this.language = lang;
    }

    public String t(String key) {
        String out = (language == Language.KO) ? ko.get(key) : en.get(key);
        return out != null ? out : key;
    }

    private void initTranslations() {
        en = new HashMap<>();
        ko = new HashMap<>();

        // --- Login (kept minimal) ---
        en.put("login.welcome", "Welcome to");
        en.put("login.subtitle", "Track your AI usage and see its environmental impact");
        en.put("login.googleButton", "Continue with Google");
        en.put("login.tutorial", "How it works");
        en.put("login.terms", "By continuing, you agree to our Terms of Service and Privacy Policy");

        ko.put("login.welcome", "환영합니다");
        ko.put("login.subtitle", "AI 사용량을 추적하고 환경에 미치는 영향을 확인하세요");
        ko.put("login.googleButton", "Google로 계속하기");
        ko.put("login.tutorial", "사용법 알아보기");
        ko.put("login.terms", "계속 진행하면 서비스 약관 및 개인정보 보호정책에 동의하는 것으로 간주됩니다");

        // --- Main screen ---
        en.put("main.todayUsage", "Today's Usage");
        en.put("main.treeHealth", "Tree Health");
        en.put("main.requests", "Requests");
        en.put("main.time", "Time");
        en.put("main.tokens", "Tokens");
        en.put("main.carbonFootprint", "CO₂");
        en.put("main.minutes", "m");
        en.put("main.learnPrompting", "Learn Efficient Prompting");
        en.put("main.viewUsage", "View Detailed Usage");

        en.put("main.thriving", "Thriving");
        en.put("main.healthy", "Healthy");
        en.put("main.declining", "Declining");
        en.put("main.wilting", "Wilting");
        en.put("main.critical", "Critical");

        en.put("main.healthMessage.thriving",
                "Your tree is flourishing! Great job keeping AI usage sustainable.");
        en.put("main.healthMessage.healthy",
                "Your tree looks healthy. Keep up the mindful AI usage!");
        en.put("main.healthMessage.declining",
                "Your tree is showing signs of stress. Consider more efficient prompting.");
        en.put("main.healthMessage.wilting",
                "Your tree needs attention. Time to reduce AI usage or improve efficiency.");
        en.put("main.healthMessage.critical",
                "Your tree is in critical condition. Please focus on sustainable AI practices.");

        // Korean
        ko.put("main.todayUsage", "오늘의 사용량");
        ko.put("main.treeHealth", "나무 건강도");
        ko.put("main.requests", "요청");
        ko.put("main.time", "시간");
        ko.put("main.tokens", "토큰");
        ko.put("main.carbonFootprint", "CO₂");
        ko.put("main.minutes", "분");
        ko.put("main.learnPrompting", "효율적인 프롬프팅 배우기");
        ko.put("main.viewUsage", "상세 사용량 보기");

        ko.put("main.thriving", "번영");
        ko.put("main.healthy", "건강");
        ko.put("main.declining", "쇠퇴");
        ko.put("main.wilting", "시들음");
        ko.put("main.critical", "위험");

        ko.put("main.healthMessage.thriving",
                "나무가 무성하게 자라고 있습니다! AI 사용을 지속 가능하게 유지하고 있어요.");
        ko.put("main.healthMessage.healthy",
                "나무가 건강해 보입니다. 신중한 AI 사용을 계속 유지하세요!");
        ko.put("main.healthMessage.declining",
                "나무가 스트레스를 받고 있습니다. 더 효율적인 프롬프팅을 고려해보세요.");
        ko.put("main.healthMessage.wilting",
                "나무가 관심이 필요합니다. AI 사용량을 줄이거나 효율성을 개선할 시간입니다.");
        ko.put("main.healthMessage.critical",
                "나무가 위험한 상태입니다. 지속 가능한 AI 사용에 집중해주세요.");
    }
}

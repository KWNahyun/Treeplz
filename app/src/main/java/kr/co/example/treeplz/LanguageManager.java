package kr.co.example.treeplz;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    public enum Language {
        EN, KO
    }

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
        en.put("main.healthMessage.thriving", "Your tree is flourishing! Great job keeping AI usage sustainable.");
        en.put("main.healthMessage.healthy", "Your tree looks healthy. Keep up the mindful AI usage!");
        en.put("main.healthMessage.declining", "Your tree is showing signs of stress. Consider more efficient prompting.");
        en.put("main.healthMessage.wilting", "Your tree needs attention. Time to reduce AI usage or improve efficiency.");
        en.put("main.healthMessage.critical", "Your tree is in critical condition. Please focus on sustainable AI practices.");

        ko.put("main.todayUsage", "오늘의 사용량");
        ko.put("main.treeHealth", "나무 건강도");
        ko.put("main.requests", "요청");
        ko.put("main.time", "시간");
        ko.put("main.tokens", "토큰");
        ko.put("main.carbonFootprint", "CO₂");
        ko.put("main.minutes", "분");
        ko.put("main.learnPrompting", "효율적인 프롬프팅 배우기");
        ko.put("main.viewUsage", "상세 사용량 보기");
        ko.put("main.healthMessage.thriving", "나무가 무성하게 자라고 있습니다! AI 사용을 지속 가능하게 유지하고 있어요.");
        ko.put("main.healthMessage.healthy", "나무가 건강해 보입니다. 신중한 AI 사용을 계속 유지하세요!");
        ko.put("main.healthMessage.declining", "나무가 스트레스를 받고 있습니다. 더 효율적인 프롬프팅을 고려해보세요.");
        ko.put("main.healthMessage.wilting", "나무가 관심이 필요합니다. AI 사용량을 줄이거나 효율성을 개선할 시간입니다.");
        ko.put("main.healthMessage.critical", "나무가 위험한 상태입니다. 지속 가능한 AI 사용에 집중해주세요.");

        // --- Usage Details Screen ---
        en.put("usage.title", "Detailed Usage");
        en.put("usage.requests", "AI Requests");
        en.put("usage.time_spent", "Time Spent");
        en.put("usage.tokens", "Tokens Used");
        en.put("usage.carbon_footprint", "Carbon Footprint");
        en.put("usage.minutes", "minutes");
        en.put("usage.weekly_trend", "Weekly Trend");
        en.put("usage.carbon_with_unit", "AI Requests & Carbon Footprint (grams CO₂)");
        en.put("usage.environmental_impact", "Environmental Impact");
        en.put("usage.envDescription", "Equivalent to %d g CO₂:");
        en.put("usage.envCar", "Driving a car");
        en.put("usage.envLight", "LED lamp");
        en.put("usage.envPhone", "Smartphone charges");
        en.put("usage.mon", "Monthly");
        en.put("usage.today", "Weekly");
        en.put("usage.tips", "Tips for Reduction");
        en.put("usage.tip1", "Use more specific prompts");
        en.put("usage.tip1_desc", "Vague prompts lead to long, irrelevant answers. Specify exactly what you want.");
        en.put("usage.tip2", "Batch similar requests");
        en.put("usage.tip2_desc", "Instead of 5 separate questions, combine them into one prompt with a list.");
        en.put("usage.tip3", "Avoid unnecessary conversations");
        en.put("usage.tip3_desc", "Skip polite phrases like \"Hello\" or \"Thank you\". The AI doesn't have feelings.");

        ko.put("usage.title", "상세 사용량");
        ko.put("usage.requests", "AI 요청");
        ko.put("usage.time_spent", "사용 시간");
        ko.put("usage.tokens", "사용된 토큰");
        ko.put("usage.carbon_footprint", "탄소 발자국");
        ko.put("usage.minutes", "분");
        ko.put("usage.weekly_trend", "주간 트렌드");
        ko.put("usage.carbon_with_unit", "AI 요청 및 탄소 발자국 (g CO₂)");
        ko.put("usage.environmental_impact", "환경 영향");
        ko.put("usage.envDescription", "%d g CO₂는 다음에 해당합니다:");
        ko.put("usage.envCar", "자동차 운전");
        ko.put("usage.envLight", "LED 램프");
        ko.put("usage.envPhone", "스마트폰 충전");
        ko.put("usage.tips", "사용량 절감 팁");
        ko.put("usage.mon", "월간");
        ko.put("usage.today", "주간");
        ko.put("usage.tip1", "더 구체적인 프롬프트 사용하기");
        ko.put("usage.tip1_desc", "모호한 질문은 길고 불필요한 답변을 유발합니다. 원하는 것을 정확히 명시하세요.");
        ko.put("usage.tip2", "유사한 요청 한번에 하기");
        ko.put("usage.tip2_desc", "5개의 개별 질문 대신, 목록을 사용하여 하나의 프롬프트로 결합하세요.");
        ko.put("usage.tip3", "불필요한 대화 피하기");
        ko.put("usage.tip3_desc", "\"안녕하세요\"나 \"감사합니다\" 같은 정중한 표현은 생략하세요. AI는 감정이 없습니다.");
    }
}
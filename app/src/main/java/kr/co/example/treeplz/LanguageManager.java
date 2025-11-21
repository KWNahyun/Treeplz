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

        // ==========================================
        // Prompting Tips (새로 추가된 부분)
        // ==========================================

        // 1. Header & Banner
        en.put("prompting.title", "Prompting Tips");
        en.put("prompting.subtitle", "Reduce Token Usage");
        en.put("prompting.potentialSavings", "Potential Token Savings");
        en.put("prompting.usingTechniques", "using these techniques");
        en.put("prompting.makeEveryTokenCount", "Make Every Token Count");
        en.put("prompting.description", "Using fewer tokens means faster responses and less environmental impact.");

        ko.put("prompting.title", "프롬프팅 팁");
        ko.put("prompting.subtitle", "토큰 사용량 줄이기");
        ko.put("prompting.potentialSavings", "절약 가능한 토큰");
        ko.put("prompting.usingTechniques", "이 기술들을 사용했을 때");
        ko.put("prompting.makeEveryTokenCount", "모든 토큰을 알뜰하게");
        ko.put("prompting.description", "토큰을 적게 쓰면 응답이 빨라지고 환경 보호에도 도움이 됩니다.");

        // 2. Tip Content
        // Tip 1: Remove Greetings
        en.put("prompting.removeGreetings.title", "Remove Greetings");
        en.put("prompting.removeGreetings.subtitle", "Cut the small talk");
        en.put("prompting.removeGreetings.description", "Skip polite phrases like 'Hello', 'Please', or 'Thank you'. The AI doesn't have feelings and understands direct instructions better.");
        en.put("prompting.removeGreetings.bad", "Bad: Hello AI, could you please help me write a python script?");
        en.put("prompting.removeGreetings.good", "Good: Write a python script to...");

        ko.put("prompting.removeGreetings.title", "인사말 생략하기");
        ko.put("prompting.removeGreetings.subtitle", "잡담은 줄이세요");
        ko.put("prompting.removeGreetings.description", "'안녕하세요', '부탁해요', '감사합니다' 같은 예의 바른 말은 생략하세요. AI는 감정이 없으며 직설적인 지시를 더 잘 이해합니다.");
        ko.put("prompting.removeGreetings.bad", "Bad: 안녕 AI야, 파이썬 스크립트 짜는 것 좀 도와줄 수 있니?");
        ko.put("prompting.removeGreetings.good", "Good: ~하는 파이썬 스크립트 작성해줘");

        // Tip 2: Be Specific
        en.put("prompting.beSpecific.title", "Be Specific");
        en.put("prompting.beSpecific.subtitle", "Clearer context = less refinement");
        en.put("prompting.beSpecific.description", "Vague prompts lead to long, irrelevant answers. Specify exactly what you want to avoid follow-up corrections.");
        en.put("prompting.beSpecific.bad", "Bad: Write about dogs.");
        en.put("prompting.beSpecific.good", "Good: Write a 50-word summary on Golden Retriever temperament.");

        ko.put("prompting.beSpecific.title", "구체적으로 말하기");
        ko.put("prompting.beSpecific.subtitle", "명확한 맥락 = 수정 최소화");
        ko.put("prompting.beSpecific.description", "모호한 질문은 길고 불필요한 답변을 유발합니다. 수정 질문을 또 하지 않도록 처음부터 정확히 요구하세요.");
        ko.put("prompting.beSpecific.bad", "Bad: 강아지에 대해 써줘.");
        ko.put("prompting.beSpecific.good", "Good: 골든 리트리버의 성격에 대해 50단어로 요약해줘.");

        // Tip 3: Define Format
        en.put("prompting.defineFormat.title", "Define Output Format");
        en.put("prompting.defineFormat.subtitle", "Ask for JSON, Lists, or Tables");
        en.put("prompting.defineFormat.description", "Asking for a specific format prevents the AI from adding unnecessary conversational filler text.");
        en.put("prompting.defineFormat.bad", "Bad: Tell me the capital cities of these countries.");
        en.put("prompting.defineFormat.good", "Good: List capital cities of [Countries] in JSON format: {country: capital}.");

        ko.put("prompting.defineFormat.title", "출력 형식 지정하기");
        ko.put("prompting.defineFormat.subtitle", "JSON, 리스트, 표 형식 요구");
        ko.put("prompting.defineFormat.description", "원하는 형식을 지정하면 AI가 불필요한 서술형 답변을 하는 것을 막을 수 있습니다.");
        ko.put("prompting.defineFormat.bad", "Bad: 이 나라들의 수도를 알려줘.");
        ko.put("prompting.defineFormat.good", "Good: [국가]들의 수도를 JSON 포맷 {country: capital} 으로 나열해줘.");

        // Tip 4: Set Constraints
        en.put("prompting.setConstraints.title", "Set Constraints");
        en.put("prompting.setConstraints.subtitle", "Limit length and scope");
        en.put("prompting.setConstraints.description", "Explicitly state limits like 'under 100 words' or 'no introduction' to keep the output concise.");
        en.put("prompting.setConstraints.bad", "Bad: Explain quantum physics.");
        en.put("prompting.setConstraints.good", "Good: Explain quantum physics in 2 sentences to a 5-year-old.");

        ko.put("prompting.setConstraints.title", "제약 조건 설정하기");
        ko.put("prompting.setConstraints.subtitle", "길이와 범위 제한");
        ko.put("prompting.setConstraints.description", "'100단어 이내', '서론 생략' 등 제약 조건을 명시하여 답변을 간결하게 만드세요.");
        ko.put("prompting.setConstraints.bad", "Bad: 양자 역학에 대해 설명해줘.");
        ko.put("prompting.setConstraints.good", "Good: 양자 역학을 5살 아이에게 2문장으로 설명해줘.");

        // Tip 5: Reusable Templates
        en.put("prompting.reusableTemplates.title", "Use Templates");
        en.put("prompting.reusableTemplates.subtitle", "Don't reinvent the wheel");
        en.put("prompting.reusableTemplates.description", "For recurring tasks, create a standard prompt structure with placeholders.");
        en.put("prompting.reusableTemplates.bad", "Bad: Writing a new email prompt from scratch every time.");
        en.put("prompting.reusableTemplates.good", "Good: Use: 'Draft a [Tone] email to [Recipient] about [Topic].'");

        ko.put("prompting.reusableTemplates.title", "템플릿 사용하기");
        ko.put("prompting.reusableTemplates.subtitle", "매번 새로 쓰지 마세요");
        ko.put("prompting.reusableTemplates.description", "반복되는 작업은 빈칸만 채우면 되는 표준 프롬프트 구조를 만들어두고 쓰세요.");
        ko.put("prompting.reusableTemplates.bad", "Bad: 매번 이메일 작성 프롬프트를 처음부터 다시 쓰기.");
        ko.put("prompting.reusableTemplates.good", "Good: '[수신자]에게 [주제]에 관한 [어조]의 이메일 초안 작성' 템플릿 사용.");

        // Tip 6: Minimal Context
        en.put("prompting.minimalContext.title", "Minimal Context");
        en.put("prompting.minimalContext.subtitle", "Only what's necessary");
        en.put("prompting.minimalContext.description", "Don't paste entire documents if you only need a summary of one section. Only provide relevant context.");
        en.put("prompting.minimalContext.bad", "Bad: (Pasting 10 pages of text) Summarize the last paragraph.");
        en.put("prompting.minimalContext.good", "Good: (Pasting only the last paragraph) Summarize this.");

        ko.put("prompting.minimalContext.title", "최소한의 문맥 제공");
        ko.put("prompting.minimalContext.subtitle", "필요한 것만 딱!");
        ko.put("prompting.minimalContext.description", "한 섹션의 요약만 필요하다면 전체 문서를 붙여넣지 마세요. 관련된 문맥만 제공하세요.");
        ko.put("prompting.minimalContext.bad", "Bad: (10페이지 텍스트 붙여넣기) 마지막 문단 요약해줘.");
        ko.put("prompting.minimalContext.good", "Good: (마지막 문단만 붙여넣기) 이거 요약해줘.");

        // Tip 7: Use Examples (Few-shot)
        en.put("prompting.useExamples.title", "Use Examples");
        en.put("prompting.useExamples.subtitle", "Show, don't just tell");
        en.put("prompting.useExamples.description", "Providing one or two examples of the desired output format helps the model understand faster than long explanations.");
        en.put("prompting.useExamples.bad", "Bad: Convert these names to uppercase.");
        en.put("prompting.useExamples.good", "Good: Convert to uppercase. Ex: apple -> APPLE. Input: banana");

        ko.put("prompting.useExamples.title", "예시 제공하기");
        ko.put("prompting.useExamples.subtitle", "말보다 예시 하나가 낫습니다");
        ko.put("prompting.useExamples.description", "원하는 출력 형식의 예시를 한두 개 보여주면 긴 설명보다 모델이 훨씬 빨리 이해합니다.");
        ko.put("prompting.useExamples.bad", "Bad: 이 이름들을 대문자로 바꿔줘.");
        ko.put("prompting.useExamples.good", "Good: 대문자로 바꿔줘. 예: apple -> APPLE. 입력: banana");

        // Tip 8: Batch Requests
        en.put("prompting.batchRequests.title", "Batch Requests");
        en.put("prompting.batchRequests.subtitle", "Combine multiple tasks");
        en.put("prompting.batchRequests.description", "Instead of asking 5 separate questions, combine them into one prompt to save on repeated context tokens.");
        en.put("prompting.batchRequests.bad", "Bad: 5 separate prompts for translating 5 words.");
        en.put("prompting.batchRequests.good", "Good: Translate these 5 words to Spanish: [List].");

        ko.put("prompting.batchRequests.title", "일괄 요청하기");
        ko.put("prompting.batchRequests.subtitle", "여러 작업을 한 번에");
        ko.put("prompting.batchRequests.description", "5개의 질문을 따로 하는 대신, 하나의 프롬프트로 묶어서 질문하면 반복되는 문맥 토큰을 아낄 수 있습니다.");
        ko.put("prompting.batchRequests.bad", "Bad: 단어 5개를 번역하기 위해 5번 따로 질문하기.");
        ko.put("prompting.batchRequests.good", "Good: 다음 5개 단어를 스페인어로 번역해줘: [목록].");
    }


}

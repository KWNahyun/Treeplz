package kr.co.example.treeplz;

public class EcoCalculator {
    // 변환 상수 (추정치)
    private static final double KWH_PER_TOKEN = 0.000003; // 1토큰당 전력
    private static final double CO2_PER_KWH = 475.0;      // 1kWh당 탄소(g)
    private static final double WATER_PER_KWH = 1500.0;   // 1kWh당 물(mL)

    // 데모용 증폭 계수 (발표 때 잘 보이게 하려면 100.0 등으로 설정)
    private static final double DEMO_MULTIPLIER = 1.0;

    public static EcoMetrics calculateImpact(long tokens) {
        double energyKwh = tokens * KWH_PER_TOKEN;

        double co2 = (energyKwh * CO2_PER_KWH) * DEMO_MULTIPLIER;
        double water = (energyKwh * WATER_PER_KWH) * DEMO_MULTIPLIER;
        double bulbHours = (energyKwh / 0.01) * DEMO_MULTIPLIER; // 10W 전구 기준

        return new EcoMetrics(tokens, co2, water, bulbHours);
    }

    // 결과를 담을 데이터 클래스 (DTO)
    public static class EcoMetrics {
        public long tokens;
        public double co2Grams;
        public double waterMl;
        public double bulbHours;

        public EcoMetrics(long tokens, double co2Grams, double waterMl, double bulbHours) {
            this.tokens = tokens;
            this.co2Grams = co2Grams;
            this.waterMl = waterMl;
            this.bulbHours = bulbHours;
        }
    }
}
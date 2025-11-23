package kr.co.example.treeplz.model;

import java.io.Serializable; // ★ 이거 필수!

// implements Serializable 추가
public class AiUsage implements Serializable {
    public int requests;
    public int tokens;
    public double timeSpent; // 서버에서 오는 이름 그대로 (시간 or 분)
    public double carbonFootprint; // 서버에서 오는 이름 그대로

    // 생성자 (필요하면 추가)
    public AiUsage(int requests, int tokens, double timeSpent, double carbonFootprint) {
        this.requests = requests;
        this.tokens = tokens;
        this.timeSpent = timeSpent;
        this.carbonFootprint = carbonFootprint;
    }
}
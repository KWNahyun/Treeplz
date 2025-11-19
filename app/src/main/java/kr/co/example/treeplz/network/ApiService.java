// kr/co/example/treeplz/network/ApiService.java
package kr.co.example.treeplz.network;

import kr.co.example.treeplz.model.AiUsage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiService {

    // 예: GET /usage/me 로 현재 사용자 사용량 조회
    @GET("usage/me")
    Call<AiUsage> getMyUsage(
            @Header("Authorization") String authHeader  // "Bearer KEY" 이런 형식
    );
}

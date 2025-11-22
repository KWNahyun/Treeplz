package kr.co.example.treeplz.network;

import kr.co.example.treeplz.model.AiUsage;
import kr.co.example.treeplz.model.ChatRequest;
import kr.co.example.treeplz.model.ChatResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @GET("/usage/me")
    Call<AiUsage> getMyUsage(@Header("Authorization") String authHeader);

    @POST("/chat")
    Call<ChatResponse> chat(
            @Header("Authorization") String authHeader,
            @Body ChatRequest body
    );
}

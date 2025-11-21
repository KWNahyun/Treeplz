package kr.co.example.treeplz.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 에뮬레이터 로컬 주소 (실제 폰이면 본인 PC IP ex: 192.168.x.x)
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    private static ApiClient instance;
    private Retrofit retrofit;

    private ApiClient() {
        // 1. 로그 인터셉터 (통신 내용을 로그캣에서 보기 위함)
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // 요청/응답 바디까지 다 봄

        // 2. OkHttpClient 설정 (타임아웃 연장)
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS) // 연결 타임아웃 60초
                .readTimeout(60, TimeUnit.SECONDS)    // 데이터 수신 타임아웃 60초 (AI 느릴 때 대비)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // 3. Retrofit 빌드
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // 위에서 만든 클라이언트 장착
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // 스레드 안전한 싱글톤 패턴
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
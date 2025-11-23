package kr.co.example.treeplz;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import kr.co.example.treeplz.model.ChatMessage;
import kr.co.example.treeplz.model.ChatRequest;
import kr.co.example.treeplz.model.ChatResponse;
import kr.co.example.treeplz.network.ApiClient;
import kr.co.example.treeplz.network.ApiService;
import kr.co.example.treeplz.ui.ChatAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private MaterialButton btnSend;

    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;

    private PreferenceHelper preferenceHelper;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        preferenceHelper = PreferenceHelper.getInstance(this);
        apiService = ApiClient.getInstance()
                .getRetrofit()
                .create(ApiService.class);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        String apiKey = preferenceHelper.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(this, "Main 화면의 설정 버튼에서 API Key를 먼저 저장해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) 유저 메시지 리스트에 추가
        ChatMessage userMsg = new ChatMessage(text, ChatMessage.Sender.USER);
        messages.add(userMsg);
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
        etMessage.setText("");

        // 2) 서버 /chat 호출
        String authHeader = "Bearer " + apiKey;
        ChatRequest body = new ChatRequest(text);

        btnSend.setEnabled(false);

        apiService.chat(authHeader, body).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                btnSend.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ChatActivity.this, "Chat 호출 실패", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatResponse chatRes = response.body();

                ChatMessage aiMsg = new ChatMessage(
                        chatRes.reply,
                        ChatMessage.Sender.AI
                );
                messages.add(aiMsg);
                adapter.notifyItemInserted(messages.size() - 1);
                rvChat.scrollToPosition(messages.size() - 1);

                // 사용량 갱신해서 메인 화면의 나무 상태를 최신으로 맞추고 싶으면
                // ChatActivity에서는 그냥 서버에 기록만 남기고,
                // MainActivity로 돌아갈 때 fetchUsageFromServer()가 다시 호출되도록 하는 지금 구조면 충분함.
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(ChatActivity.this,
                        "네트워크 오류: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

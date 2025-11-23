package kr.co.example.treeplz;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ImageButton btnSend;

    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;

    private PreferenceHelper preferenceHelper;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        preferenceHelper = PreferenceHelper.getInstance(this);
        apiService = ApiClient.getInstance().getRetrofit().create(ApiService.class);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // 뒤로가기 버튼 연결 (XML에 ID가 btnBack으로 있다고 가정)
        if (findViewById(R.id.btnBack) != null) {
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        }

        adapter = new ChatAdapter(messages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        String apiKey = preferenceHelper.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(this, "API Key is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // [수정 1] 유저 메시지 추가 (true 대신 Enum 사용)
        messages.add(new ChatMessage(text, ChatMessage.Sender.USER));

        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.smoothScrollToPosition(messages.size() - 1);
        etMessage.setText("");

        String authHeader = "Bearer " + apiKey;
        ChatRequest body = new ChatRequest(text);

        btnSend.setEnabled(false);
        btnSend.setAlpha(0.5f);

        apiService.chat(authHeader, body).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                btnSend.setEnabled(true);
                btnSend.setAlpha(1.0f);

                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body().reply;

                    // [수정 2] AI 응답 추가 (false 대신 Enum 사용)
                    messages.add(new ChatMessage(reply, ChatMessage.Sender.AI));

                    adapter.notifyItemInserted(messages.size() - 1);
                    rvChat.smoothScrollToPosition(messages.size() - 1);
                } else {
                    Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                btnSend.setEnabled(true);
                btnSend.setAlpha(1.0f);
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
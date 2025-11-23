package kr.co.example.treeplz.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.example.treeplz.R;
import kr.co.example.treeplz.model.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);

        if (msg.sender == ChatMessage.Sender.USER) {
            // [유저일 때]
            holder.tvUserMessage.setVisibility(View.VISIBLE);
            holder.tvUserMessage.setText(msg.text);

            // AI 관련 숨김
            holder.tvAiMessage.setVisibility(View.GONE);
            holder.ivAiProfile.setVisibility(View.GONE);

        } else {
            // [AI일 때]
            holder.tvAiMessage.setVisibility(View.VISIBLE);
            holder.tvAiMessage.setText(msg.text);
            holder.ivAiProfile.setVisibility(View.VISIBLE);

            // 유저 관련 숨김
            holder.tvUserMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ★ 에러가 났던 부분: 변수 선언과 사용 이름을 정확히 일치시켰습니다.
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        // 1. 변수 선언
        TextView tvUserMessage;
        TextView tvAiMessage;
        ImageView ivAiProfile;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // 2. 초기화 (변수명 = XML ID)
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvAiMessage = itemView.findViewById(R.id.tvAiMessage);
            ivAiProfile = itemView.findViewById(R.id.ivAiProfile);
        }
    }
}
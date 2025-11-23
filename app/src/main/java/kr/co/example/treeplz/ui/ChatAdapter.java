package kr.co.example.treeplz.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            holder.tvUser.setVisibility(View.VISIBLE);
            holder.tvAi.setVisibility(View.GONE);
            holder.tvUser.setText(msg.text);
        } else {
            holder.tvAi.setVisibility(View.VISIBLE);
            holder.tvUser.setVisibility(View.GONE);
            holder.tvAi.setText(msg.text);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser;
        TextView tvAi;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUserMessage);
            tvAi = itemView.findViewById(R.id.tvAiMessage);
        }
    }
}

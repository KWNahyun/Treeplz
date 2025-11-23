package kr.co.example.treeplz.model;

public class ChatMessage {

    public enum Sender {
        USER,
        AI
    }

    public String text;
    public Sender sender;

    public ChatMessage(String text, Sender sender) {
        this.text = text;
        this.sender = sender;
    }
}

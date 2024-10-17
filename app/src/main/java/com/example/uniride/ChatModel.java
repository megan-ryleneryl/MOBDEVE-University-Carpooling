package com.example.uniride;

public class ChatModel {
    private String chatId;
    private String name;
    private String userType;    // driver or passenger
    private String bookingDate;
    private String lastMessage;
    private String timestamp;   // timestamp of when the message was last read
    private boolean isRead;

    public ChatModel(String chatId, String name, String userType, String bookingDate, String lastMessage, String timestamp, boolean isRead) {
        this.chatId = chatId;
        this.name = name;
        this.userType = userType;
        this.bookingDate = bookingDate;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters
    public String getChatId() { return chatId; }
    public String getUserName() { return name; }
    public String getUserType() { return userType; }
    public String getBookingDate() { return bookingDate; }
    public String getLastMessage() { return lastMessage; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
}

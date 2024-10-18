package com.example.uniride;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageModel implements Serializable {
    private int messageID;
    private UserModel sender; // TODO: Change to userID in mco3, same for recipient
    private UserModel recipient;
    private String message;
    private LocalDateTime date;

    public MessageModel() {

    }

    public MessageModel(int messageID, UserModel sender, UserModel recipient, String message, LocalDateTime date) {
        this.messageID = messageID;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.date = date;
    }

    // Getters
    public int getMessageID() {
        return messageID;
    }
    public UserModel getSender() {
        return sender;
    }
    public UserModel getRecipient() {
        return recipient;
    }
    public String getMessage() {
        return message;
    }
    public LocalDateTime getDate() {
        return date;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }
}

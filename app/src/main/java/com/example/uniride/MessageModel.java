package com.example.uniride;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class MessageModel implements Serializable {
    private int chatID;
    private UserModel sender; // TODO: Change to userID in mco3, same for recipient
    private UserModel recipient;
    private String message;
    private Date date;

    public MessageModel() {

    }

    public MessageModel(int chatID, UserModel sender, UserModel recipient, String message, Date date) {
        this.chatID = chatID;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.date = date;
    }

    // Getters
    public int getChatID() {
        return chatID;
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
    public Date getDate() {
        return date;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }
}

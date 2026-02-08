package com.example.sagivproject.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ForumMessage implements Serializable, Idable {
    private String id;
    private String fullName;
    private String email;
    private String message;
    private long timestamp;
    private String userId;
    private boolean sentByAdmin;

    public ForumMessage() {
    }

    public ForumMessage(String id, String fullName, String email, String message, long timestamp, String userId, boolean sentByAdmin) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.message = message;
        this.timestamp = timestamp;
        this.userId = userId;
        this.sentByAdmin = sentByAdmin;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String messageId) {
        this.id = messageId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSentByAdmin() {
        return sentByAdmin;
    }

    public void setSentByAdmin(boolean sentByAdmin) {
        this.sentByAdmin = sentByAdmin;
    }

    @NonNull
    @Override
    public String toString() {
        return "ForumMessage{" +
                "messageId='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", isUserAdmin=" + sentByAdmin +
                '}';
    }
}
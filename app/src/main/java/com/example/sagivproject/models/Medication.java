package com.example.sagivproject.models;

import com.example.sagivproject.models.enums.MedicationType;

import java.io.Serializable;
import java.util.List;

public class Medication implements Serializable, Idable {
    private String id;
    private String userId;
    private String name;
    private String details;
    private MedicationType type;
    private List<String> reminderHours;

    public Medication() {
    }

    public Medication(String id, String name, String details, MedicationType type, List<String> reminderHours, String userId) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.type = type;
        this.reminderHours = reminderHours;
        this.userId = userId;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String messageId) {
        this.id = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public MedicationType getType() {
        return type;
    }

    public void setType(MedicationType type) {
        this.type = type;
    }

    public List<String> getReminderHours() {
        return reminderHours;
    }

    public void setReminderHours(List<String> reminderHours) {
        this.reminderHours = reminderHours;
    }
}
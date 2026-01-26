package com.example.sagivproject.models;

import com.example.sagivproject.models.enums.MedicationType;
import com.google.firebase.database.Exclude;

import java.util.Date;

public class Medication {
    private String id;
    private String name;
    private String details;
    private MedicationType type;
    @Exclude
    private Date date;

    public Medication() {}

    public Medication(String name, String details, MedicationType type, Date date, String userId) {
        this.name = name;
        this.details = details;
        this.type = type;
        this.date = date;
        this.id = userId;
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public String getDetails() { return this.details; }
    public void setDetails(String details) { this.details = details; }
    public MedicationType getType() { return type; }
    public void setType(MedicationType type) { this.type = type; }

    @Exclude
    public Date getDate() { return this.date; }
    @Exclude
    public void setDate(Date date) { this.date = date; }

    public long getDateTimestamp() { return date != null ? date.getTime() : 0; }
    public void setDateTimestamp(long timestamp) { this.date = new Date(timestamp); }
}
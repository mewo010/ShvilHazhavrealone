package com.example.sagivproject.models;

import java.io.Serializable;

public class ForumCategory implements Serializable, Idable {
    private String id;
    private String name;
    private long orderTimestamp;

    public ForumCategory() {
    }

    public ForumCategory(String id, String name) {
        this.id = id;
        this.name = name;
        this.orderTimestamp = -System.currentTimeMillis(); // Set negative value for descending order
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(long orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }
}
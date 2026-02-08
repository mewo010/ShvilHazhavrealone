package com.example.sagivproject.models;

import java.io.Serializable;

public class ForumCategory implements Serializable, Idable {
    private String id;
    private String name;
    private long creationDate;

    public ForumCategory() {
    }

    public ForumCategory(String id, String name) {
        this.id = id;
        this.name = name;
        this.creationDate = System.currentTimeMillis();
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

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
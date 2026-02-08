package com.example.sagivproject.models;

import java.io.Serializable;

public class Card implements Serializable, Idable {
    private String id;
    private String base64Content;
    private boolean isRevealed = false;
    private boolean isMatched = false;
    private boolean wasRevealed;

    public Card() {
    }

    public Card(String id, String base64Content) {
        this.id = id;
        this.base64Content = base64Content;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }

    public boolean getIsRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    public boolean getIsMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public boolean getWasRevealed() {
        return wasRevealed;
    }

    public void setWasRevealed(boolean wasRevealed) {
        this.wasRevealed = wasRevealed;
    }
}
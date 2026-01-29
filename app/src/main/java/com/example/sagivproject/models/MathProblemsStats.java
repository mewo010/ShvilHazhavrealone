package com.example.sagivproject.models;

import java.io.Serializable;

public class MathProblemsStats implements Serializable {
    private int correctAnswers;
    private int wrongAnswers;

    public MathProblemsStats() {
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
    }

    public MathProblemsStats(int correctAnswers, int wrongAnswers) {
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
}
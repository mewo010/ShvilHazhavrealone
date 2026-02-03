package com.example.sagivproject.services;

import androidx.annotation.NonNull;

import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IStatsService;

import javax.inject.Inject;

public class StatsService implements IStatsService {
    private final IDatabaseService databaseService;

    @Inject
    public StatsService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void addCorrectAnswer(String uid) {
        databaseService.addCorrectAnswer(uid);
    }

    @Override
    public void addWrongAnswer(String uid) {
        databaseService.addWrongAnswer(uid);
    }

    @Override
    public void resetMathStats(@NonNull String uid) {
        databaseService.resetMathStats(uid);
    }
}

package com.example.sagivproject.services;

import androidx.annotation.NonNull;

import com.example.sagivproject.services.interfaces.IStatsService;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

public class StatsService implements IStatsService {

    private static final String USERS_PATH = "users";
    private final DatabaseReference databaseReference;

    @Inject
    public StatsService(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(USERS_PATH);
    }

    @Override
    public void addCorrectAnswer(String uid) {
        databaseReference.child(uid).child("mathProblemsStats").child("correctAnswers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer current = task.getResult().getValue(Integer.class);
                if (current == null) current = 0;
                databaseReference.child(uid).child("mathProblemsStats").child("correctAnswers").setValue(current + 1);
            }
        });
    }

    @Override
    public void addWrongAnswer(String uid) {
        databaseReference.child(uid).child("mathProblemsStats").child("wrongAnswers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer current = task.getResult().getValue(Integer.class);
                if (current == null) current = 0;
                databaseReference.child(uid).child("mathProblemsStats").child("wrongAnswers").setValue(current + 1);
            }
        });
    }

    @Override
    public void resetMathStats(@NonNull String uid) {
        databaseReference.child(uid).child("mathProblemsStats").child("correctAnswers").setValue(0);
        databaseReference.child(uid).child("mathProblemsStats").child("wrongAnswers").setValue(0);
    }
}

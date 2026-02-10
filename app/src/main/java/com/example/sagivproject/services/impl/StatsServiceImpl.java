package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.services.IStatsService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import javax.inject.Inject;

public class StatsServiceImpl implements IStatsService {
    private static final String USERS_PATH = "users";
    private final DatabaseReference databaseReference;

    @Inject
    public StatsServiceImpl(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(USERS_PATH);
    }

    private void addAnswer(String uid, String key) {
        if (uid == null) {
            return;
        }
        databaseReference.child(uid).child("mathProblemsStats").child(key).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer current = currentData.getValue(Integer.class);
                if (current == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(current + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Transaction completed
            }
        });
    }

    @Override
    public void addCorrectAnswer(String uid) {
        addAnswer(uid, "correctAnswers");
    }

    @Override
    public void addWrongAnswer(String uid) {
        addAnswer(uid, "wrongAnswers");
    }

    @Override
    public void resetMathStats(@NonNull String uid) {
        databaseReference.child(uid).child("mathProblemsStats").child("correctAnswers").setValue(0);
        databaseReference.child(uid).child("mathProblemsStats").child("wrongAnswers").setValue(0);
    }
}

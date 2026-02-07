package com.example.sagivproject.services;

import androidx.annotation.NonNull;

public interface IStatsService {
    /**
     * add a correct answer to the user's stats
     *
     * @param uid the UID of the user
     */
    void addCorrectAnswer(String uid);

    /**
     * add a wrong answer to the user's stats
     *
     * @param uid the UID of the user
     */
    void addWrongAnswer(String uid);

    /**
     * reset math problems statistics for a user
     *
     * @param uid the UID of the user
     */
    void resetMathStats(@NonNull String uid);
}

package com.example.sagivproject.services;

import com.example.sagivproject.bases.BaseTable;
import com.example.sagivproject.models.Medication;
import com.example.sagivproject.models.User;
import com.google.android.gms.games.Game;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseService {
    private static DatabaseService instance;

    public final BaseTable<User> users;
    public final BaseTable<Medication> medications;
    public final BaseTable<Game> games;

    private DatabaseService() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        users = new BaseTable<User>(db.getReference("Users"), User.class) {};
        medications = new BaseTable<Medication>(db.getReference("Medications"), Medication.class) {};
        games = new BaseTable<Game>(db.getReference("Games"), Game.class) {};
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
}
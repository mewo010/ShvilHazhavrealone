package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.services.interfaces.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IMedicationService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MedicationService implements IMedicationService {

    private static final String USERS_PATH = "users";
    private final DatabaseReference databaseReference;

    @Inject
    public MedicationService(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(USERS_PATH);
    }

    @Override
    public void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(uid).child("medications").child(medication.getId()).setValue(medication).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void getUserMedicationList(@NonNull String uid, @NonNull DatabaseCallback<List<Medication>> callback) {
        databaseReference.child(uid).child("medications").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Medication> medicationList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Medication medication = snapshot.getValue(Medication.class);
                    medicationList.add(medication);
                }
                callback.onCompleted(medicationList);
            } else {
                callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public String generateMedicationId(@NonNull String uid) {
        return databaseReference.child(uid).child("medications").push().getKey();
    }

    @Override
    public void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(uid).child("medications").child(medicationId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void updateMedication(String uid, Medication medication, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(uid).child("medications").child(medication.getId()).setValue(medication).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }
}

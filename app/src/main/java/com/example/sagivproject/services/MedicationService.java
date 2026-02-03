package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IMedicationService;

import java.util.List;

import javax.inject.Inject;

public class MedicationService implements IMedicationService {
    private final IDatabaseService databaseService;

    @Inject
    public MedicationService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable DatabaseCallback<Void> callback) {
        databaseService.createNewMedication(uid, medication, callback);
    }

    @Override
    public void getUserMedicationList(@NonNull String uid, @NonNull DatabaseCallback<List<Medication>> callback) {
        databaseService.getUserMedicationList(uid, callback);
    }

    @Override
    public String generateMedicationId(@NonNull String uid) {
        return databaseService.generateMedicationId(uid);
    }

    @Override
    public void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable DatabaseCallback<Void> callback) {
        databaseService.deleteMedication(uid, medicationId, callback);
    }

    @Override
    public void updateMedication(String uid, Medication medication, @Nullable DatabaseCallback<Void> callback) {
        databaseService.updateMedication(uid, medication, callback);
    }
}

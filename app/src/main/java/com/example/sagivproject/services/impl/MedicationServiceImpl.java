package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.IMedicationService;

import java.util.List;
import java.util.function.UnaryOperator;

import javax.inject.Inject;

public class MedicationServiceImpl extends BaseDatabaseService<Medication> implements IMedicationService {
    private static final String USERS_PATH = "users";
    private static final String MEDICATIONS_PATH = "medications";

    @Inject
    public MedicationServiceImpl() {
        super("", Medication.class);
    }

    private String getMedicationPath(String uid) {
        return USERS_PATH + "/" + uid + "/" + MEDICATIONS_PATH;
    }

    @Override
    public String generateMedicationId(String uid) {
        return readData(getMedicationPath(uid)).push().getKey();
    }

    @Override
    public void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable DatabaseCallback<Void> callback) {
        writeData(getMedicationPath(uid) + "/" + medication.getId(), medication, callback);
    }

    @Override
    public void getUserMedicationList(@NonNull String uid, @NonNull DatabaseCallback<List<Medication>> callback) {
        getDataList(getMedicationPath(uid), callback);
    }

    @Override
    public void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable DatabaseCallback<Void> callback) {
        deleteData(getMedicationPath(uid) + "/" + medicationId, callback);
    }

    @Override
    public void updateMedication(String uid, Medication medication, @Nullable DatabaseCallback<Void> callback) {
        UnaryOperator<Medication> updateFunction = oldMedication -> medication;
        runTransaction(getMedicationPath(uid) + "/" + medication.getId(), updateFunction, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Medication result) {
                if (callback != null) {
                    callback.onCompleted(null);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onFailed(e);
                }
            }
        });
    }
}

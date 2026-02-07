package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.services.DatabaseCallback;
import com.example.sagivproject.services.IMedicationService;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import javax.inject.Inject;

public class MedicationServiceImpl extends BaseDatabaseService<Medication> implements IMedicationService {

    private static final String USERS_PATH = "users";
    private static final String MEDICATIONS_PATH = "medications";
    private final DatabaseReference usersRef;

    @Inject
    public MedicationServiceImpl(DatabaseReference databaseReference) {
        super(databaseReference);
        this.usersRef = databaseReference.child(USERS_PATH);
    }

    @Override
    public void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable DatabaseCallback<Void> callback) {
        DatabaseReference medicationRef = usersRef.child(uid).child(MEDICATIONS_PATH);
        super.create(medicationRef, medication.getId(), medication, callback);
    }

    @Override
    public void getUserMedicationList(@NonNull String uid, @NonNull DatabaseCallback<List<Medication>> callback) {
        DatabaseReference medicationRef = usersRef.child(uid).child(MEDICATIONS_PATH);
        super.getAll(medicationRef, Medication.class, callback);
    }

    @Override
    public String generateMedicationId(@NonNull String uid) {
        DatabaseReference medicationRef = usersRef.child(uid).child(MEDICATIONS_PATH);
        return super.generateId(medicationRef);
    }

    @Override
    public void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable DatabaseCallback<Void> callback) {
        DatabaseReference medicationRef = usersRef.child(uid).child(MEDICATIONS_PATH);
        super.delete(medicationRef, medicationId, callback);
    }

    @Override
    public void updateMedication(String uid, Medication medication, @Nullable DatabaseCallback<Void> callback) {
        DatabaseReference medicationRef = usersRef.child(uid).child(MEDICATIONS_PATH);
        super.update(medicationRef, medication.getId(), medication, callback);
    }
}

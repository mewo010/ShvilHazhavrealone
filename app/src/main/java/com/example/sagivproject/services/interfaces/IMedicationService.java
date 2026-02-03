package com.example.sagivproject.services.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Medication;

import java.util.List;

public interface IMedicationService {
    /**
     * create a new medication in the database
     *
     * @param uid        the id of the user
     * @param medication the medication object to create
     * @param callback   the callback to call when the operation is completed
     */
    void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable IDatabaseService.DatabaseCallback<Void> callback);

    /**
     * get all the medications of a specific user
     *
     * @param uid      the id of the user
     * @param callback the callback
     */
    void getUserMedicationList(@NonNull String uid, @NonNull IDatabaseService.DatabaseCallback<List<Medication>> callback);

    /**
     * generate a new id for a medication under a specific user
     *
     * @param uid the id of the user
     * @return a new id for the medication
     */
    String generateMedicationId(@NonNull String uid);

    /**
     * delete a medication from the database
     *
     * @param uid          user id
     * @param medicationId id to delete
     * @param callback     callback
     */
    void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable IDatabaseService.DatabaseCallback<Void> callback);

    /**
     * update a medication in the database
     *
     * @param uid        user id
     * @param medication medication to update
     * @param callback   callback
     */
    void updateMedication(String uid, Medication medication, @Nullable IDatabaseService.DatabaseCallback<Void> callback);
}

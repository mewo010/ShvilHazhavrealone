package com.example.sagivproject.screens;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.MedicationListAdapter;
import com.example.sagivproject.adapters.diffUtils.MedicationDiffCallback;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.Medication;
import com.example.sagivproject.models.User;
import com.example.sagivproject.screens.dialogs.MedicationDialog;
import com.example.sagivproject.services.interfaces.DatabaseCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MedicationListActivity extends BaseActivity {
    private final ArrayList<Medication> medications = new ArrayList<>();
    private final ArrayList<Medication> filteredMedications = new ArrayList<>();
    private MedicationListAdapter adapter;
    private User user;
    private String uid;
    private EditText editSearch;
    private Spinner spinnerSearchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medication_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.medicationListPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = sharedPreferencesUtil.getUser();
        uid = Objects.requireNonNull(user).getUid();

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        findViewById(R.id.btn_MedicationList_add_medication).setOnClickListener(view -> openMedicationDialog(null));

        RecyclerView recyclerViewMedications = findViewById(R.id.recyclerView_medications);
        recyclerViewMedications.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MedicationListAdapter(this, filteredMedications, new MedicationListAdapter.OnMedicationActionListener() {
            @Override
            public void onEdit(int position) {
                openMedicationDialog(filteredMedications.get(position));
            }

            @Override
            public void onDelete(int position) {
                deleteMedicationById(filteredMedications.get(position).getId());
            }
        });
        recyclerViewMedications.setAdapter(adapter);

        editSearch = findViewById(R.id.edit_Medication_search);
        spinnerSearchType = findViewById(R.id.spinner_Medication_search_type);

        ArrayAdapter<String> spinnerAdapter = getStringArrayAdapter();

        spinnerSearchType.setAdapter(spinnerAdapter);

        editSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMedications(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        spinnerSearchType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterMedications(editSearch.getText().toString());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearchType.setAdapter(spinnerAdapter);

        loadMedicationsFromCache();
        fetchMedicationsFromServer();
    }

    private void loadMedicationsFromCache() {
        if (user.getMedications() != null) {
            List<Medication> cachedList = new ArrayList<>(user.getMedications().values());
            updateMedicationList(cachedList);
        }
    }

    private void fetchMedicationsFromServer() {
        databaseService.medications().getUserMedicationList(uid, new DatabaseCallback<>() {
            @Override
            public void onCompleted(List<Medication> list) {
                processServerResponse(list);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationListActivity.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processServerResponse(List<Medication> serverList) {
        List<Medication> validMedications = serverList.stream()
                .filter(med -> med.getDate() == null || !isExpired(med.getDate()))
                .collect(Collectors.toList());

        List<String> expiredIds = serverList.stream()
                .filter(med -> med.getDate() != null && isExpired(med.getDate()))
                .map(Medication::getId)
                .collect(Collectors.toList());

        if (!expiredIds.isEmpty()) {
            deleteExpiredMedications(expiredIds);
        }

        updateMedicationList(validMedications);
        updateUserCache(validMedications);
    }

    private boolean isExpired(Date date) {
        Calendar expiryCal = Calendar.getInstance();
        expiryCal.setTime(date);
        expiryCal.add(Calendar.DAY_OF_YEAR, 1);
        return new Date().after(expiryCal.getTime());
    }

    private void deleteExpiredMedications(List<String> expiredIds) {
        for (String id : expiredIds) {
            databaseService.medications().deleteMedication(uid, id, null);
        }
        Toast.makeText(MedicationListActivity.this, "נמחקו תרופות שפגו תוקפן", Toast.LENGTH_SHORT).show();
    }

    private void updateUserCache(List<Medication> medicationList) {
        HashMap<String, Medication> updatedMedicationsMap = new HashMap<>();
        for (Medication med : medicationList) {
            updatedMedicationsMap.put(med.getId(), med);
        }
        user.setMedications(updatedMedicationsMap);
        sharedPreferencesUtil.saveUser(user);
    }

    private void updateMedicationList(List<Medication> medicationList) {
        medications.clear();
        medications.addAll(medicationList);
        medications.sort(Comparator.comparing(Medication::getDate));
        filterMedications(editSearch.getText().toString());
    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        String[] searchOptions = {"שם תרופה", "סוג תרופה", "הכל"};

        return new ArrayAdapter<>(MedicationListActivity.this, android.R.layout.simple_spinner_item, searchOptions) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTypeface(ResourcesCompat.getFont(MedicationListActivity.this, R.font.text_hebrew));
                tv.setTextSize(22);
                tv.setTextColor(getColor(R.color.text_color));
                tv.setPadding(24, 24, 24, 24);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTypeface(ResourcesCompat.getFont(MedicationListActivity.this, R.font.text_hebrew));
                tv.setTextSize(22);
                tv.setTextColor(getColor(R.color.text_color));
                tv.setBackgroundColor(
                        getColor(R.color.background_color_buttons)
                );
                tv.setPadding(24, 24, 24, 24);
                return tv;
            }
        };
    }

    private void saveMedication(Medication medication) {
        medication.setId(databaseService.medications().generateMedicationId(uid));
        databaseService.medications().createNewMedication(uid, medication, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                fetchMedicationsFromServer(); // Refresh list from server
                Toast.makeText(MedicationListActivity.this, "התרופה נוספה", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationListActivity.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMedication(Medication med) {
        databaseService.medications().updateMedication(uid, med, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                fetchMedicationsFromServer(); // Refresh list from server
                Toast.makeText(MedicationListActivity.this, "התרופה עודכנה", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationListActivity.this, "שגיאה בעדכון", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMedicationById(String id) {
        databaseService.medications().deleteMedication(uid, id, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                fetchMedicationsFromServer(); // Refresh list from server
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationListActivity.this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMedicationDialog(Medication medToEdit) {
        new MedicationDialog(this, medToEdit, uid, new MedicationDialog.OnMedicationSubmitListener() {
            @Override
            public void onAdd(Medication medication) {
                saveMedication(medication);
            }

            @Override
            public void onEdit(Medication medication) {
                updateMedication(medication);
            }
        }).show();
    }

    private void filterMedications(String query) {
        List<Medication> oldList = new ArrayList<>(filteredMedications);
        filteredMedications.clear();
        String selectedType = spinnerSearchType.getSelectedItem().toString();

        if (query.isEmpty() && selectedType.equals("הכל")) {
            filteredMedications.addAll(medications);
        } else {
            for (Medication med : medications) {
                boolean matches = true;
                if (!query.isEmpty()) {
                    if (selectedType.equals("שם תרופה") && !med.getName().toLowerCase().contains(query.toLowerCase())) {
                        matches = false;
                    }
                    if (selectedType.equals("סוג תרופה") && (med.getType() == null || !med.getType().getDisplayName().toLowerCase().contains(query.toLowerCase()))) {
                        matches = false;
                    }
                }
                if (matches) {
                    filteredMedications.add(med);
                }
            }
        }
        MedicationDiffCallback diffCallback = new MedicationDiffCallback(oldList, filteredMedications);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(adapter);
    }
}

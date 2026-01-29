package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.adapters.MedicationListAdapter;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.Medication;
import com.example.sagivproject.models.User;
import com.example.sagivproject.screens.dialogs.MedicationDialog;
import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MedicationListActivity extends BaseActivity {
    private final ArrayList<Medication> medications = new ArrayList<>();
    private MedicationListAdapter adapter;
    private User user;
    private String uid;
    private final ArrayList<Medication> filteredMedications = new ArrayList<>();
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

        user = SharedPreferencesUtil.getUser(this);
        uid = Objects.requireNonNull(user).getUid();

        Button btnToMain = findViewById(R.id.btn_MedicationList_to_main);
        Button btnToContact = findViewById(R.id.btn_MedicationList_to_contact);
        Button btnToDetailsAboutUser = findViewById(R.id.btn_MedicationList_to_DetailsAboutUser);
        Button btnAddMedication = findViewById(R.id.btn_MedicationList_add_medication);
        Button btnToExit = findViewById(R.id.btn_MedicationList_to_exit);
        ImageButton btnToSettings = findViewById(R.id.btn_MedicationList_to_settings);

        btnToMain.setOnClickListener(view -> startActivity(new Intent(MedicationListActivity.this, MainActivity.class)));
        btnToContact.setOnClickListener(view -> startActivity(new Intent(MedicationListActivity.this, ContactActivity.class)));
        btnToDetailsAboutUser.setOnClickListener(view -> startActivity(new Intent(MedicationListActivity.this, DetailsAboutUserActivity.class)));
        btnAddMedication.setOnClickListener(view -> openMedicationDialog(null));
        btnToExit.setOnClickListener(view -> logout());
        btnToSettings.setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));

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

        String[] searchOptions = {"שם תרופה", "סוג תרופה", "הכל"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                searchOptions
        ) {
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

        loadMedications();
    }

    private void loadMedications() {
        if (user.getMedications() != null) {
            List<Medication> cachedList = new ArrayList<>(user.getMedications().values());

            medications.clear();
            medications.addAll(cachedList);

            medications.sort(Comparator.comparing(Medication::getDate));
            adapter.notifyDataSetChanged();
        }

        databaseService.getUserMedicationList(uid, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<Medication> list) {
                medications.clear();

                HashMap<String, Medication> updatedMedicationsMap = new HashMap<>();

                Date today = new Date();
                List<String> expiredIds = new ArrayList<>();

                for (Medication med : list) {
                    if (med.getDate() != null) {
                        Calendar expiryCal = Calendar.getInstance();
                        expiryCal.setTime(med.getDate());
                        expiryCal.add(Calendar.DAY_OF_YEAR, 1);

                        if (today.after(expiryCal.getTime())) {
                            expiredIds.add(med.getId());
                        } else {
                            medications.add(med);
                            updatedMedicationsMap.put(med.getId(), med);
                        }
                    } else {
                        medications.add(med);
                        updatedMedicationsMap.put(med.getId(), med);
                    }
                }

                //מחיקה של פגי תוקף
                for (String id : expiredIds) {
                    databaseService.deleteMedication(uid, id, null);
                }

                //אם נמחקו תרופות, להציג Toast
                if (!expiredIds.isEmpty()) {
                    Toast.makeText(MedicationListActivity.this, "נמחקו תרופות שפגו תוקפן", Toast.LENGTH_SHORT).show();
                }

                // סידור רשימת התרופות התקינות
                medications.sort(Comparator.comparing(Medication::getDate));

                adapter.notifyDataSetChanged();

                filterMedications(editSearch.getText().toString());

                user.setMedications(updatedMedicationsMap);
                SharedPreferencesUtil.saveUser(MedicationListActivity.this, user);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationListActivity.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMedication(Medication medication) {
        medication.setId(databaseService.generateMedicationId(uid));
        databaseService.createNewMedication(uid, medication, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                HashMap<String, Medication> medsMap = user.getMedications();
                if (medsMap == null) {
                    medsMap = new HashMap<>();
                }
                medsMap.put(medication.getId(), medication);
                user.setMedications(medsMap);
                SharedPreferencesUtil.saveUser(MedicationListActivity.this, user);
                Toast.makeText(MedicationListActivity.this, "התרופה נוספה", Toast.LENGTH_SHORT).show();
                loadMedications();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationListActivity.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMedication(Medication med) {
        databaseService.updateMedication(uid, med, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                HashMap<String, Medication> medsMap = user.getMedications();
                if (medsMap != null) {
                    medsMap.put(med.getId(), med);
                    user.setMedications(medsMap);
                    SharedPreferencesUtil.saveUser(MedicationListActivity.this, user);
                }
                Toast.makeText(MedicationListActivity.this, "התרופה עודכנה", Toast.LENGTH_SHORT).show();
                loadMedications();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MedicationListActivity.this, "שגיאה בעדכון", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMedicationById(String id) {
        databaseService.deleteMedication(uid, id, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                HashMap<String, Medication> medsMap = user.getMedications();
                if (medsMap != null) {
                    medsMap.remove(id);
                    user.setMedications(medsMap);
                    SharedPreferencesUtil.saveUser(MedicationListActivity.this, user);
                }
                loadMedications();
                Toast.makeText(MedicationListActivity.this, "התרופה נמחקה בהצלחה", Toast.LENGTH_SHORT).show();
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
        filteredMedications.clear();
        String lowerQuery = query.toLowerCase().trim();
        String searchType = spinnerSearchType.getSelectedItem().toString();

        for (Medication med : medications) {
            boolean matches = false;
            String medName = med.getName() != null ? med.getName().toLowerCase() : "";
            String medType = med.getType() != null ? med.getType().getDisplayName().toLowerCase() : "";

            switch (searchType) {
                case "שם תרופה":
                    if (medName.contains(lowerQuery)) matches = true;
                    break;
                case "סוג תרופה":
                    if (medType.contains(lowerQuery)) matches = true;
                    break;
                default: // "הכל"
                    if (medName.contains(lowerQuery) || medType.contains(lowerQuery))
                        matches = true;
                    break;
            }
            if (matches) filteredMedications.add(med);
        }

        filteredMedications.sort(Comparator.comparing(Medication::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
        adapter.notifyDataSetChanged();
    }
}
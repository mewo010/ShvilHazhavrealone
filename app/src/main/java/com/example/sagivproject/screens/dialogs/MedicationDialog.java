package com.example.sagivproject.screens.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.models.Medication;
import com.example.sagivproject.models.enums.MedicationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MedicationDialog {
    private final Context context;
    private final Medication medToEdit;
    private final OnMedicationSubmitListener listener;
    private final ArrayList<Integer> selectedHours = new ArrayList<>();
    private TextView tvSelectedHours;

    public MedicationDialog(Context context, Medication medToEdit, OnMedicationSubmitListener listener) {
        this.context = context;
        this.medToEdit = medToEdit;
        this.listener = listener;
    }

    public void show() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_medication);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        EditText edtName = dialog.findViewById(R.id.edt_medication_name);
        AutoCompleteTextView spinnerType = dialog.findViewById(R.id.spinner_medication_type);
        EditText edtDetails = dialog.findViewById(R.id.edt_medication_details);
        Button btnSelectHours = dialog.findViewById(R.id.btn_select_hours);
        tvSelectedHours = dialog.findViewById(R.id.tv_selected_hours);
        Button btnConfirm = dialog.findViewById(R.id.btn_add_medication_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_add_medication_cancel);

        List<String> typeNames = new ArrayList<>();
        for (MedicationType type : MedicationType.values()) {
            typeNames.add(type.getDisplayName());
        }

        spinnerType.setAdapter(createMedicationTypeAdapter(typeNames));

        if (medToEdit != null) {
            edtName.setText(medToEdit.getName());
            edtDetails.setText(medToEdit.getDetails());
            if (medToEdit.getType() != null) {
                spinnerType.setText(medToEdit.getType().getDisplayName(), false);
            }
            if (medToEdit.getReminderHours() != null && !medToEdit.getReminderHours().isEmpty()) {
                selectedHours.clear();
                for (String hour : medToEdit.getReminderHours()) {
                    selectedHours.add(Integer.parseInt(hour.split(":")[0]));
                }
                updateSelectedHoursText();
            }
        }

        btnSelectHours.setOnClickListener(v -> showHourPicker());

        btnConfirm.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String typeString = spinnerType.getText().toString();
            String details = edtDetails.getText().toString().trim();

            if (name.isEmpty() || typeString.isEmpty() || details.isEmpty() || selectedHours.isEmpty()) {
                Toast.makeText(context, "אנא מלא את כל השדות ובחר לפחות שעת תזכורת אחת", Toast.LENGTH_SHORT).show();
                return;
            }

            MedicationType selectedType = null;
            for (MedicationType type : MedicationType.values()) {
                if (type.getDisplayName().equals(typeString)) {
                    selectedType = type;
                    break;
                }
            }

            if (selectedType == null) {
                Toast.makeText(context, "אנא בחר סוג תרופה", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> reminderHours = selectedHours.stream()
                    .map(hour -> String.format("%02d:00", hour))
                    .collect(Collectors.toList());

            Medication medicationData = new Medication();
            medicationData.setName(name);
            medicationData.setDetails(details);
            medicationData.setType(selectedType);
            medicationData.setReminderHours(reminderHours);

            if (medToEdit == null) {
                listener.onAdd(medicationData);
            } else {
                medicationData.setId(medToEdit.getId());
                listener.onEdit(medicationData);
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showHourPicker() {
        String[] hours = new String[24];
        boolean[] checkedHours = new boolean[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d:00", i);
            checkedHours[i] = selectedHours.contains(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("בחר שעות תזכורת");
        builder.setMultiChoiceItems(hours, checkedHours, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (!selectedHours.contains(which)) {
                    selectedHours.add(which);
                }
            } else if (selectedHours.contains(which)) {
                selectedHours.remove(Integer.valueOf(which));
            }
        });

        builder.setPositiveButton("אישור", (dialog, which) -> updateSelectedHoursText());
        builder.setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void updateSelectedHoursText() {
        Collections.sort(selectedHours);
        StringBuilder sb = new StringBuilder();
        for (int hour : selectedHours) {
            sb.append(String.format("%02d:00", hour)).append("  ");
        }
        if (sb.length() > 0) {
            tvSelectedHours.setText(String.format("שעות נבחרות: %s", sb));
        } else {
            tvSelectedHours.setText("לא נבחרו שעות");
        }
    }

    private ArrayAdapter<String> createMedicationTypeAdapter(List<String> typeNames) {
        return new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                typeNames
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                styleTextView(tv, false);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                styleTextView(tv, true);
                return tv;
            }
        };
    }

    private void styleTextView(TextView tv, boolean isDropdown) {
        tv.setTypeface(ResourcesCompat.getFont(context, R.font.text_hebrew));
        tv.setTextSize(22);
        tv.setTextColor(context.getColor(R.color.text_color));
        tv.setPadding(24, 24, 24, 24);

        if (isDropdown) {
            tv.setBackgroundColor(
                    context.getColor(R.color.background_color_buttons)
            );
        }
    }

    public interface OnMedicationSubmitListener {
        void onAdd(Medication medication);

        void onEdit(Medication medication);
    }
}

package com.example.sagivproject.screens.dialogs;

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
import com.example.sagivproject.utils.CalendarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MedicationDialog {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private final Context context;
    private final Medication medToEdit;
    private final OnMedicationSubmitListener listener;

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
        EditText edtDate = dialog.findViewById(R.id.edt_medication_date);
        Button btnConfirm = dialog.findViewById(R.id.btn_add_medication_confirm);
        Button btnCancel = dialog.findViewById(R.id.btn_add_medication_cancel);

        List<String> typeNames = new ArrayList<>();
        for (MedicationType type : MedicationType.values()) {
            typeNames.add(type.getDisplayName());
        }

        spinnerType.setAdapter(createMedicationTypeAdapter(typeNames));

        long initialDateMillis = -1;
        if (medToEdit != null) {
            edtName.setText(medToEdit.getName());
            edtDetails.setText(medToEdit.getDetails());
            if (medToEdit.getType() != null) {
                spinnerType.setText(medToEdit.getType().getDisplayName(), false);
            }
            if (medToEdit.getDate() != null) {
                initialDateMillis = medToEdit.getDate().getTime();
                edtDate.setText(CalendarUtil.formatDate(initialDateMillis, DATE_FORMAT));
            }
        }

        final long finalInitialDateMillis = initialDateMillis;
        edtDate.setOnClickListener(v -> CalendarUtil.openDatePicker(context, finalInitialDateMillis, (millis, dateStr) -> edtDate.setText(dateStr), true, DATE_FORMAT));

        btnConfirm.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String typeString = spinnerType.getText().toString();
            String details = edtDetails.getText().toString().trim();
            String dateString = edtDate.getText().toString().trim();

            if (name.isEmpty() || typeString.isEmpty() || details.isEmpty() || dateString.isEmpty()) {
                Toast.makeText(context, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
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

            try {
                Date date = new SimpleDateFormat(DATE_FORMAT, Locale.US).parse(dateString);

                Medication medicationData = new Medication();
                medicationData.setName(name);
                medicationData.setDetails(details);
                medicationData.setType(selectedType);
                medicationData.setDate(date);

                if (medToEdit == null) {
                    listener.onAdd(medicationData);
                } else {
                    medicationData.setId(medToEdit.getId());
                    listener.onEdit(medicationData);
                }

                dialog.dismiss();

            } catch (ParseException e) {
                Toast.makeText(context, "תאריך לא תקין", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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

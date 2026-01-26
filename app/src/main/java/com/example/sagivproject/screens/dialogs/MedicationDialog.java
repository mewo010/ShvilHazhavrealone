package com.example.sagivproject.screens.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sagivproject.R;
import com.example.sagivproject.models.Medication;
import com.example.sagivproject.models.enums.MedicationType;
import com.example.sagivproject.utils.CalendarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicationDialog {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private final Context context;
    private final Medication medToEdit;
    private final String uid;
    private final OnMedicationSubmitListener listener;
    public MedicationDialog(Context context, Medication medToEdit, String uid, OnMedicationSubmitListener listener) {
        this.context = context;
        this.medToEdit = medToEdit;
        this.uid = uid;
        this.listener = listener;
    }

    public void show() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_medication);

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, typeNames);
        spinnerType.setAdapter(adapter);

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
                Date date = new SimpleDateFormat(DATE_FORMAT).parse(dateString);

                if (medToEdit == null) {
                    listener.onAdd(new Medication(name, details, selectedType, date, uid));
                } else {
                    medToEdit.setName(name);
                    medToEdit.setDetails(details);
                    medToEdit.setType(selectedType);
                    medToEdit.setDate(date);
                    listener.onEdit(medToEdit);
                }

                dialog.dismiss();

            } catch (ParseException e) {
                Toast.makeText(context, "תאריך לא תקין", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public interface OnMedicationSubmitListener {
        void onAdd(Medication medication);

        void onEdit(Medication medication);
    }
}
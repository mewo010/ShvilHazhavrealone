package com.example.sagivproject.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sagivproject.R;
import com.example.sagivproject.models.Medication;
import com.example.sagivproject.ui.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.MedicationViewHolder> {
    private final Context context;
    private final ArrayList<Medication> medications;
    private final OnMedicationActionListener listener;

    public MedicationListAdapter(Context context, ArrayList<Medication> medications, OnMedicationActionListener listener) {
        this.context = context;
        this.medications = medications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication med = medications.get(position);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.text_hebrew);

        if (typeface != null) {
            SpannableString nameSpannable = new SpannableString(med.getName());
            nameSpannable.setSpan(new CustomTypefaceSpan("", typeface), 0, nameSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.txtMedicationName.setText(nameSpannable);
        } else {
            holder.txtMedicationName.setText(med.getName());
        }

        if (med.getType() != null) {
            holder.txtMedicationType.setText(med.getType().getDisplayName());
            holder.txtMedicationType.setVisibility(View.VISIBLE);
        } else {
            holder.txtMedicationType.setVisibility(View.GONE);
        }

        holder.txtMedicationDetails.setText(med.getDetails());
        List<String> reminderHours = med.getReminderHours();
        if (reminderHours != null && !reminderHours.isEmpty()) {
            holder.txtMedicationHours.setText(String.format("שעות: %s", TextUtils.join(", ", reminderHours)));
            holder.txtMedicationHours.setVisibility(View.VISIBLE);
        } else {
            holder.txtMedicationHours.setVisibility(View.GONE);
        }

        // Assuming you have a date field in your Medication model
        // holder.txtMedicationDate.setText(String.format("תאריך: %s", med.getDate()));

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(context, v);
            menu.inflate(R.menu.menu_medication_item);

            if (typeface != null) {
                for (int i = 0; i < menu.getMenu().size(); i++) {
                    MenuItem item = menu.getMenu().getItem(i);
                    SpannableString s = new SpannableString(item.getTitle());

                    s.setSpan(new CustomTypefaceSpan("", typeface), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new AbsoluteSizeSpan(20, true), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    item.setTitle(s);
                }
            }

            menu.setOnMenuItemClickListener(item -> {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos == RecyclerView.NO_POSITION) return false;

                if (item.getItemId() == R.id.action_edit) {
                    listener.onEdit(currentPos);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    listener.onDelete(currentPos);
                    return true;
                }
                return false;
            });

            menu.show();
        });
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public interface OnMedicationActionListener {
        void onEdit(int position);

        void onDelete(int position);
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        final TextView txtMedicationName, txtMedicationType, txtMedicationDetails, txtMedicationHours;
        final ImageButton btnMenu;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMedicationName = itemView.findViewById(R.id.txt_MedicationRow_Name);
            txtMedicationType = itemView.findViewById(R.id.txt_MedicationRow_Type);
            txtMedicationDetails = itemView.findViewById(R.id.txt_MedicationRow_Details);
            txtMedicationHours = itemView.findViewById(R.id.txt_MedicationRow_Hours);
            btnMenu = itemView.findViewById(R.id.btn_MedicationRow_Menu);
        }
    }
}

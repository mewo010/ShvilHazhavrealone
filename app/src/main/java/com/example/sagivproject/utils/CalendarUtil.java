package com.example.sagivproject.utils;

import android.app.DatePickerDialog;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtil {

    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    public static void openDatePicker(Context context, long initialMillis, OnDateSelectedListener listener) {
        openDatePicker(context, initialMillis, listener, false, DEFAULT_DATE_FORMAT);
    }

    public static void openDatePicker(Context context, long initialMillis, OnDateSelectedListener listener, boolean futureOnly, String format) {
        final Calendar calendar = Calendar.getInstance();
        if (initialMillis > 0) {
            calendar.setTimeInMillis(initialMillis);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, day) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, day, 0, 0, 0);
                    selectedCal.set(Calendar.MILLISECOND, 0);

                    long selectedMillis = selectedCal.getTimeInMillis();
                    String formattedDate = formatDate(selectedMillis, format);

                    if (listener != null) {
                        listener.onDateSelected(selectedMillis, formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        if (futureOnly) {
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }

        dialog.show();
    }

    public static String formatDate(long millis) {
        return formatDate(millis, DEFAULT_DATE_FORMAT);
    }

    public static String formatDate(long millis, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    public interface OnDateSelectedListener {
        void onDateSelected(long dateMillis, String formattedDate);
    }
}
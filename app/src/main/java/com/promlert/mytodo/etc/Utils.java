package com.promlert.mytodo.etc;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

public class Utils {

    public static void setupDatePicker(Context context, Calendar calendar, EditText editText) {
        editText.setOnClickListener(v -> {
            final DatePickerDialog.OnDateSetListener dateSetListener =
                    (view, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String formatDate = DateFormatConverter.formatForUi(calendar.getTime());
                        editText.setText(formatDate);
                    };
            new DatePickerDialog(
                    context,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }
}

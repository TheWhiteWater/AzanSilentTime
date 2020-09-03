package nz.co.redice.azansilenttime.ui.presentation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private final DatePickerDialog.OnDateSetListener mListener;
    private final Context mContext;

    public DatePickerFragment(Context context, DatePickerDialog.OnDateSetListener listener) {
        mListener = listener;
        mContext = context;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(mContext, mListener, year, month, day);
    }



}

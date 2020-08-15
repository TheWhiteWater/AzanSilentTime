package nz.co.redice.azansilenttime.view.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {


    private final TimePickerDialog.OnTimeSetListener mListener;
    private final Context mContext;

    public TimePickerFragment (Context context, TimePickerDialog.OnTimeSetListener listener) {
        mListener = listener;
        mContext = context;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(mContext, AlertDialog.THEME_HOLO_LIGHT, mListener, hour, minute,DateFormat.is24HourFormat(mContext));
    }


}

package nz.co.redice.azansilenttime.view.presentation;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import nz.co.redice.azansilenttime.R;

public class TimePickerFragment extends DialogFragment {


    private final TimePickerDialog.OnTimeSetListener mListener;
    private final Context mContext;

    public TimePickerFragment (Context context, TimePickerDialog.OnTimeSetListener listener) {
        mListener = listener;
        mContext = context;
    }
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(mContext, R.style.HoloDialogTheme, mListener, hour, minute,DateFormat.is24HourFormat(mContext));
    }


}

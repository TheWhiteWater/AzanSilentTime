package nz.co.redice.demoservice.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.ZoneId;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.databinding.HomeFragmentBinding;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.utils.Converters;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.utils.ServiceHelper;
import nz.co.redice.demoservice.view.presentation.DatePickerFragment;
import nz.co.redice.demoservice.view.presentation.TimeTable;
import nz.co.redice.demoservice.viewmodel.HomeScreenViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    @Inject Repository mRepository;
    @Inject PreferencesHelper mPreferencesHelper;
    private HomeScreenViewModel mViewModel;
    private HomeFragmentBinding mViewBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = mViewBinding.getRoot();
        mViewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setting date picker
        mViewBinding.dateView.setOnClickListener(this::showDatePickerDialog);

        // setting timings for current day
        Long currentDayEpoch = LocalDate.now().atStartOfDay(ZoneId.of(mPreferencesHelper.getTimeZone())).toEpochSecond();
        getTimingsForSelectedDate(currentDayEpoch);
    }


    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(getContext(), this);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDestroyView() {
        mViewBinding = null;
        super.onDestroyView();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate selectedDate = LocalDate.of(year, ++month, dayOfMonth);
        Long selectedEpoch = selectedDate.atStartOfDay(ZoneId.of(mPreferencesHelper.getTimeZone())).toEpochSecond();
        getTimingsForSelectedDate(selectedEpoch);
    }


    private void getTimingsForSelectedDate(Long date) {
        mViewModel.getTimesForSelectedDate(date).observe(getViewLifecycleOwner(), selected -> {
            TimeTable timeTable = new TimeTable();
            timeTable.setDate(Converters.convertLongValueOfLocalDateIntoString(selected.getDate(), selected.getTimeZone(), mPreferencesHelper.getLocalDateFormatPattern()));
            timeTable.setFajr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getFajr(), selected.getTimeZone(), mPreferencesHelper.getLocalTimeFormatPattern()));
            timeTable.setAsr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getAsr(), selected.getTimeZone(), mPreferencesHelper.getLocalTimeFormatPattern()));
            timeTable.setDhuhr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getDhuhr(), selected.getTimeZone(), mPreferencesHelper.getLocalTimeFormatPattern()));
            timeTable.setIsha(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getIsha(), selected.getTimeZone(), mPreferencesHelper.getLocalTimeFormatPattern()));
            timeTable.setMaghrib(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getMaghrib(), selected.getTimeZone(), mPreferencesHelper.getLocalTimeFormatPattern()));
            mViewBinding.setTimeTable(timeTable);
        });


    }
}
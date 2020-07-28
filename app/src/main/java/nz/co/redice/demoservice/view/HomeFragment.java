package nz.co.redice.demoservice.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
import nz.co.redice.demoservice.utils.PrefHelper;
import nz.co.redice.demoservice.view.presentation.DatePickerFragment;
import nz.co.redice.demoservice.view.presentation.TimeTable;
import nz.co.redice.demoservice.viewmodel.HomeScreenViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    @Inject Repository mRepository;
    @Inject PrefHelper mPrefHelper;
    private HomeScreenViewModel mViewModel;
    private HomeFragmentBinding mBinding;
    private TimeTable mTimeTable;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        mViewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);
        mTimeTable = new TimeTable();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setting date picker
        mBinding.dateTv.setOnClickListener(this::showDatePickerDialog);

        // setting timings for current day
        Long currentDayEpoch = LocalDate.now().atStartOfDay(ZoneId.of(mPrefHelper.getTimeZone())).toEpochSecond();
        setDataBindingByStringValue(currentDayEpoch);
    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(getContext(), this);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




        // TODO: 27.07.2020
//        mPrefHelper.setDatabaseUpdateStatus(false);
        Log.d("App", "onCreate: " + mPrefHelper.getDatabaseUpdateStatus());
        if (!mPrefHelper.getDatabaseUpdateStatus()) {
            mRepository.requestAnnualCalendar(-40.3596, 175.61);
            mPrefHelper.setDatabaseUpdateStatus(true);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate newDate = LocalDate.of(year, ++month, dayOfMonth);
        Long longDate = newDate.atStartOfDay(ZoneId.of(mPrefHelper.getTimeZone())).toEpochSecond();
        Log.d("App", "onDateSet: " + longDate);
        setDataBindingByStringValue(longDate);
    }


    private void setDataBindingByStringValue(Long date) {
        mViewModel.getTimesForSelectedDate(date).observe(getViewLifecycleOwner(), selected -> {
            mTimeTable.setDate(Converters.convertLongValueOfLocalDateIntoString(selected.getDate(), selected.getTimeZone(), mPrefHelper.getLocalDateFormatPattern()));
            mTimeTable.setFajr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getFajr(), selected.getTimeZone(), mPrefHelper.getLocalTimeFormatPattern()));
            mTimeTable.setAsr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getAsr(), selected.getTimeZone(), mPrefHelper.getLocalTimeFormatPattern()));
            mTimeTable.setDhuhr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getDhuhr(), selected.getTimeZone(), mPrefHelper.getLocalTimeFormatPattern()));
            mTimeTable.setIsha(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getIsha(), selected.getTimeZone(), mPrefHelper.getLocalTimeFormatPattern()));
            mTimeTable.setMaghrib(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getMaghrib(), selected.getTimeZone(), mPrefHelper.getLocalTimeFormatPattern()));
            mBinding.setTimeTable(mTimeTable);
        });


    }
}
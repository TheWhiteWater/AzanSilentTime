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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.ZoneId;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.databinding.HomeFragmentBinding;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.utils.Converters;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.view.presentation.DatePickerFragment;
import nz.co.redice.demoservice.view.presentation.ReadableTimings;
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

        mViewBinding.asrBtn.setOnClickListener(this::onClick);
        mViewBinding.fajrBtn.setOnClickListener(this::onClick);
        mViewBinding.ishaBtn.setOnClickListener(this::onClick);
        mViewBinding.maghribBtn.setOnClickListener(this::onClick);
        mViewBinding.dhuhrBtn.setOnClickListener(this::onClick);

        // setting timings for current day
        Long currentDayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        getTimingsForSelectedDate(currentDayEpoch);

        mRepository.getDatabaseSize().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                mRepository.requestStandardAnnualCalendar(-40.3596, 175.61);
            } else {
                Log.d("App", "onChanged: database size: " + integer);
            }
        });
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
        Long selectedEpoch = selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        getTimingsForSelectedDate(selectedEpoch);
    }


    private void getTimingsForSelectedDate(Long date) {
        mViewModel.getTimesForSelectedDate(date).observe(getViewLifecycleOwner(), new Observer<EntryModel>() {
            @Override
            public void onChanged(EntryModel selected) {
                ReadableTimings result = new ReadableTimings();
                result.setDate(Converters.convertLongValueOfLocalDateIntoString(selected.getDate(), mPreferencesHelper.getLocalDateFormatPattern()));
                result.setFajr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getFajr(), mPreferencesHelper.getLocalTimeFormatPattern()));
                result.setAsr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getAsr(), mPreferencesHelper.getLocalTimeFormatPattern()));
                result.setDhuhr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getDhuhr(), mPreferencesHelper.getLocalTimeFormatPattern()));
                result.setIsha(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getIsha(), mPreferencesHelper.getLocalTimeFormatPattern()));
                result.setMaghrib(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getMaghrib(), mPreferencesHelper.getLocalTimeFormatPattern()));
                mViewBinding.setReadableTimings(result);
            }
        });



    }

    public void onClick(View view) {

    }
}
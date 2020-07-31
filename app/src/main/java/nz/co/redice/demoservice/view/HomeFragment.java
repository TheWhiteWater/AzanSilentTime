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
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.view.presentation.DatePickerFragment;
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
        Long currentDayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Log.d("App", "onViewCreated: epochSecond " + currentDayEpoch);
        getTimingsForSelectedDate(currentDayEpoch);
        mViewBinding.asrBtn.setOnClickListener(this::onClick);
        mViewBinding.fajrBtn.setOnClickListener(this::onClick);
        mViewBinding.ishaBtn.setOnClickListener(this::onClick);
        mViewBinding.maghribBtn.setOnClickListener(this::onClick);
        mViewBinding.dhuhrBtn.setOnClickListener(this::onClick);
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
        Long selectedEpoch = selectedDate.atStartOfDay(ZoneId.of("Pacific/Auckland")).toEpochSecond();
        getTimingsForSelectedDate(selectedEpoch);
    }


    private void getTimingsForSelectedDate(Long date) {
            mViewModel.getTimesForSelectedDate(date).observe(getViewLifecycleOwner(), selected -> {
                mViewBinding.setTimeTable(selected);
                Log.d("App", "getTimingsForSelectedDate: "  + selected.getDate());
            });

    }

    public void onClick(View view) {

    }
}
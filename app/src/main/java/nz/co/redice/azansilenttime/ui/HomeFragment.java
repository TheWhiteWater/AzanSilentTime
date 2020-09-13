package nz.co.redice.azansilenttime.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.databinding.FragmentHomeBinding;
import nz.co.redice.azansilenttime.repo.local.entity.AlarmSchedule;
import nz.co.redice.azansilenttime.repo.local.entity.AzanTimings;
import nz.co.redice.azansilenttime.ui.presentation.DatePickerFragment;
import nz.co.redice.azansilenttime.ui.presentation.TimePickerFragment;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;
import nz.co.redice.azansilenttime.viewmodel.HomeViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    @Inject SharedPreferencesHelper mSharedPreferencesHelper;
    private HomeViewModel mViewModel;
    private FragmentHomeBinding mViewBinding;
    private AzanTimings mAzanTimings;
    private AlarmSchedule mAlarmSchedule;
    private int mTimePickerCalledView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.settings_id) {
            NavHostFragment.findNavController(this).navigate(R.id.fromHomeToSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = mViewBinding.getRoot();
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLayoutWidgets();

        if (mSharedPreferencesHelper.isAzanTimingsToBePopulated())
            mViewModel.populateAzanTimings();
        else
            mViewModel.selectAzanTimings(LocalDate.now());

        if (mSharedPreferencesHelper.isAlarmSchedulesToBeCreated())
            mViewModel.createAlarmSchedules();
        else
            mViewModel.getAlarmSchedule();

        bindAzanTimings();
        bindAlarmSchedule();

    }

    private void bindAzanTimings() {
        mViewModel.getAzanTimingsLiveData().observe(getViewLifecycleOwner(), timings -> {
            if (timings != null) {
                mAzanTimings = timings;
                mViewBinding.invalidateAll();
                mViewBinding.setAzanTimings(timings);
            }
        });
    }


    private void bindAlarmSchedule() {
        mViewModel.getAlarmSchedules().observe(getViewLifecycleOwner(),
                schedule -> {
                    if (schedule != null) {
                        registerSwitchListeners(false);
                        setAlarmSwitches(schedule);
                        mAlarmSchedule = schedule;
                        mViewBinding.invalidateAll();
                        mViewBinding.setAlarmSchedule(schedule);
                        registerSwitchListeners(true);
                    }
                });


    }

    private void setLayoutWidgets() {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        //setting date picker
        mViewBinding.dateView.setOnClickListener(this::showDatePickerDialog);

        //time pickers
        mViewBinding.fridayDisplayTime.setOnClickListener(this::showTimePickerDialog);
    }

    private void registerSwitchListeners(boolean flag) {
        mViewBinding.fajrSwitch.setOnCheckedChangeListener(flag ? this : null);
        mViewBinding.dhuhrSwitch.setOnCheckedChangeListener(flag ? this : null);
        mViewBinding.maghribSwitch.setOnCheckedChangeListener(flag ? this : null);
        mViewBinding.asrSwitch.setOnCheckedChangeListener(flag ? this : null);
        mViewBinding.ishaSwitch.setOnCheckedChangeListener(flag ? this : null);
        mViewBinding.fridaySwitch.setOnCheckedChangeListener(flag ? this : null);
    }

    private void showTimePickerDialog(View view) {
        mTimePickerCalledView = view.getId();
        DialogFragment newFragment = new TimePickerFragment(getContext(), this);
        newFragment.show(requireActivity().getSupportFragmentManager(), getString(R.string.fragment_tag_time_picker));
    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(getContext(), this);
        newFragment.show(requireActivity().getSupportFragmentManager(), getString(R.string.fragment_tag_date_picker));
    }

    @Override
    public void onDestroyView() {
        mViewBinding.unbind();
        mViewBinding = null;
        mViewModel.getAzanTimingsLiveData().removeObservers(getViewLifecycleOwner());
        mViewModel.getAlarmSchedules().removeObservers(getViewLifecycleOwner());
        super.onDestroyView();
    }


    public void setAlarmSwitches(AlarmSchedule alarmSchedule) {
        mViewBinding.fajrSwitch.setChecked(alarmSchedule.isFajrMute());
        mViewBinding.dhuhrSwitch.setChecked(alarmSchedule.isDhuhrMute());
        mViewBinding.asrSwitch.setChecked(alarmSchedule.isAsrMute());
        mViewBinding.maghribSwitch.setChecked(alarmSchedule.isMaghribMute());
        mViewBinding.ishaSwitch.setChecked(alarmSchedule.isIshaMute());
        mViewBinding.fridaySwitch.setChecked(alarmSchedule.isFridayMute());

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        switch (mTimePickerCalledView) {
            case R.id.friday_alarm_period:
                mViewModel.saveFridayAlarmSchedule(hourOfDay, minute);

        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        LocalDate selectedDate = LocalDate.of(currentYear, ++month, dayOfMonth);
        mViewModel.selectAzanTimings(selectedDate);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {
            case R.id.fajr_switch:
                mAlarmSchedule.setFajrMute(!mAlarmSchedule.isFajrMute());
                mViewModel.updateAlarmSchedule(mAlarmSchedule);
                mViewBinding.invalidateAll();
                break;
            case R.id.dhuhr_switch:
                mAlarmSchedule.setDhuhrMute(!mAlarmSchedule.isDhuhrMute());
                mViewModel.updateAlarmSchedule(mAlarmSchedule);
                mViewBinding.invalidateAll();
                break;
            case R.id.maghrib_switch:
                mAlarmSchedule.setMaghribMute(!mAlarmSchedule.isMaghribMute());
                mViewModel.updateAlarmSchedule(mAlarmSchedule);
                mViewBinding.invalidateAll();
                break;
            case R.id.asr_switch:
                mAlarmSchedule.setAsrMute(!mAlarmSchedule.isAsrMute());
                mViewModel.updateAlarmSchedule(mAlarmSchedule);
                mViewBinding.invalidateAll();
                break;
            case R.id.isha_switch:
                mAlarmSchedule.setIshaMute(!mAlarmSchedule.isIshaMute());
                mViewModel.updateAlarmSchedule(mAlarmSchedule);
                mViewBinding.invalidateAll();
                break;
            case R.id.friday_switch:
                mAlarmSchedule.setFridayMute(!mAlarmSchedule.isFridayMute());
                mViewModel.updateAlarmSchedule(mAlarmSchedule);
                mViewBinding.invalidateAll();
                break;

        }

    }


}
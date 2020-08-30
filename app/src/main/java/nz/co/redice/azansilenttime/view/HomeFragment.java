package nz.co.redice.azansilenttime.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;
import nz.co.redice.azansilenttime.view.presentation.DatePickerFragment;
import nz.co.redice.azansilenttime.view.presentation.TimePickerFragment;
import nz.co.redice.azansilenttime.viewmodel.HomeViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "App HomeFragment";

    @Inject SharedPreferencesHelper mSharedPreferencesHelper;
    private HomeViewModel mViewModel;
    private FragmentHomeBinding mViewBinding;
    private RegularEntry mRegularEntry;
    private FridayEntry mFridayEntry;

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
        switch (menuItemId) {
            case R.id.settings_id:
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

        if (mSharedPreferencesHelper.isRegularTableToBePopulated())
            mViewModel.populateRegularTable();
        else
            mViewModel.selectNewRegularEntry(LocalDate.now());

        if (mSharedPreferencesHelper.isFridayTableToBePopulated())
            mViewModel.populateFridayTable();
        else
            mViewModel.selectNewFridayEntry(LocalDate.now());

        bindRegularEntry();
        bindFridayEntry();

    }

    private void bindRegularEntry() {
        mViewModel.getRegularObservable().observe(getViewLifecycleOwner(), entry -> {
            if (entry != null) {
                Log.d(TAG, "setLiveDataObserver: setting livedata");
                registerSwitchListeners(false);
                setRegularSwitches(entry);
                mRegularEntry = entry;
                mViewBinding.invalidateAll();
                mViewBinding.setEntry(entry);
                registerSwitchListeners(true);
            }
        });
    }


    private void bindFridayEntry() {
        mViewModel.getFridayEntry().observe(getViewLifecycleOwner(),
                fridayEntry -> {
                    if (fridayEntry != null) {
                        registerSwitchListeners(false);
                        setFridaySwitch(fridayEntry);
                        mFridayEntry = fridayEntry;
                        mViewBinding.invalidateAll();
                        mViewBinding.setFriday(fridayEntry);
                        registerSwitchListeners(true);
                    }
                });


    }

    private void setLayoutWidgets() {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        mViewBinding.setPrefs(mSharedPreferencesHelper);
        mViewBinding.checkbox.setOnCheckedChangeListener(this);

        //setting date picker
        mViewBinding.dateView.setOnClickListener(this::showDatePickerDialog);
        mViewBinding.fridayCard.setOnClickListener(this::showTimePickerDialog);
    }

    private void registerSwitchListeners(boolean isGood2Go) {
        mViewBinding.fajrSwitch.setOnCheckedChangeListener(isGood2Go ? this : null);
        mViewBinding.dhuhrSwitch.setOnCheckedChangeListener(isGood2Go ? this : null);
        mViewBinding.maghribSwitch.setOnCheckedChangeListener(isGood2Go ? this : null);
        mViewBinding.asrSwitch.setOnCheckedChangeListener(isGood2Go ? this : null);
        mViewBinding.ishaSwitch.setOnCheckedChangeListener(isGood2Go ? this : null);
        mViewBinding.fridaySwitch.setOnCheckedChangeListener(isGood2Go ? this : null);
    }

    private void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment(getContext(), this);
        newFragment.show(requireActivity().getSupportFragmentManager(), "timePicker");
    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(getContext(), this);
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDestroyView() {
        mViewBinding.unbind();
        mViewBinding = null;
        mViewModel.getRegularObservable().removeObservers(getViewLifecycleOwner());
        mViewModel.getFridayEntry().removeObservers(getViewLifecycleOwner());
        super.onDestroyView();
    }


    public void setRegularSwitches(RegularEntry regularEntry) {
        mViewBinding.fajrSwitch.setChecked(regularEntry.getFajrSilent());
        mViewBinding.dhuhrSwitch.setChecked(regularEntry.getDhuhrSilent());
        mViewBinding.asrSwitch.setChecked(regularEntry.getAsrSilent());
        mViewBinding.maghribSwitch.setChecked(regularEntry.getMaghribSilent());
        mViewBinding.ishaSwitch.setChecked(regularEntry.getIshaSilent());
    }

    public void setFridaySwitch(FridayEntry regularEntry) {
        mViewBinding.fridaySwitch.setChecked(regularEntry.getSilent());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mViewModel.updateFridaysTable(hourOfDay, minute);
        mViewModel.selectNewFridayEntry(mFridayEntry.getDate());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        LocalDate selectedDate = LocalDate.of(currentYear, ++month, dayOfMonth);
        if (!mSharedPreferencesHelper.isDndForFridaysOnly())
            mViewModel.selectNewRegularEntry(selectedDate);
        else
            mViewModel.selectNewFridayEntry(selectedDate);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {


        switch (compoundButton.getId()) {
            case R.id.fajr_switch:
                mRegularEntry.setFajrSilent(!mRegularEntry.getFajrSilent());
                mViewModel.updateEntry(mRegularEntry);
                mViewBinding.invalidateAll();
                break;
            case R.id.dhuhr_switch:
                mRegularEntry.setDhuhrSilent(!mRegularEntry.getDhuhrSilent());
                mViewModel.updateEntry(mRegularEntry);
                mViewBinding.invalidateAll();
                break;
            case R.id.maghrib_switch:
                mRegularEntry.setMaghribSilent(!mRegularEntry.getMaghribSilent());
                mViewModel.updateEntry(mRegularEntry);
                mViewBinding.invalidateAll();
                break;
            case R.id.asr_switch:
                mRegularEntry.setAsrSilent(!mRegularEntry.getAsrSilent());
                mViewModel.updateEntry(mRegularEntry);
                mViewBinding.invalidateAll();
                break;
            case R.id.isha_switch:
                mRegularEntry.setIshaSilent(!mRegularEntry.getIshaSilent());
                mViewModel.updateEntry(mRegularEntry);
                mViewBinding.invalidateAll();
                break;
            case R.id.friday_switch:
                mFridayEntry.setSilent(!mFridayEntry.getSilent());
                mViewModel.updateFridayEntry(mFridayEntry);
                mViewBinding.invalidateAll();
                break;
            case R.id.checkbox:
                mSharedPreferencesHelper.setDndForFridaysOnly(isChecked);
                mViewBinding.invalidateAll();
                break;
        }

    }


}
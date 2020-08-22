package nz.co.redice.azansilenttime.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.TransitionDrawable;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.databinding.FragmentHomeBinding;
import nz.co.redice.azansilenttime.repo.local.entity.EntryModel;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.utils.PrefHelper;
import nz.co.redice.azansilenttime.view.presentation.DatePickerFragment;
import nz.co.redice.azansilenttime.view.presentation.TimePickerFragment;
import nz.co.redice.azansilenttime.viewmodel.HomeViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "App HomeFragment";
    @Inject PrefHelper mPrefHelper;
    private HomeViewModel mViewModel;
    private FragmentHomeBinding mViewBinding;
    private EntryModel mEntryModel;
    private FridayEntry mFridayEntry;

    private TransitionDrawable transitionFajr;
    private TransitionDrawable transitionDhuhr;
    private TransitionDrawable transitionMaghrib;
    private TransitionDrawable transitionAsr;
    private TransitionDrawable transitionIsha;
    private TransitionDrawable transitionFriday;

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

        mViewModel.getRegularDatabaseSize().observe(getViewLifecycleOwner(), integer -> {
            if (integer >= 365) {
                bindRegularEntry(LocalDate.from(LocalDateTime.now()));
            } else {
                mViewModel.requestPrayerCalendar();
            }
        });


        mViewModel.getFridayTableCount().observe(getViewLifecycleOwner(), integer -> {
            if (integer > 0) {
                bindFridayEntry(LocalDate.now());
            } else {
                mViewModel.populateFridayTable();
            }
        });


    }


    private void setLayoutWidgets() {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        mViewBinding.setPrefs(mPrefHelper);
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
        mViewBinding = null;
        super.onDestroyView();
    }

    private void bindFridayEntry(LocalDate selectedDate) {
        mViewModel.getNextFridayEntry(selectedDate).observe(getViewLifecycleOwner(),
                fridayEntry -> {
                    if (fridayEntry != null) {
                        mFridayEntry = fridayEntry;
                        mViewBinding.setFriday(fridayEntry);
                    }
                });
    }

    private void bindRegularEntry(LocalDate date) {
        mViewModel.getSelectedEntry(date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()).observe(getViewLifecycleOwner(), selected -> {
            if (selected != null) {
                Log.d(TAG, "bindRegularEntry: " + selected.getDateText());
                mEntryModel = selected;
                registerSwitchListeners(false);
                mViewBinding.setEntry(mEntryModel);
                setSwitchesStates(mEntryModel);
                registerSwitchListeners(true);
            }

        });
    }


    public synchronized void setSwitchesStates(EntryModel model) {
        mViewBinding.fajrSwitch.setChecked(model.getFajrSilent());
        mViewBinding.dhuhrSwitch.setChecked(model.getDhuhrSilent());
        mViewBinding.asrSwitch.setChecked(model.getAsrSilent());
        mViewBinding.maghribSwitch.setChecked(model.getMaghribSilent());
        mViewBinding.ishaSwitch.setChecked(model.getIshaSilent());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mViewModel.updateFridaysTable(hourOfDay, minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        LocalDate selectedDate = LocalDate.of(currentYear, ++month, dayOfMonth);
        if (!mPrefHelper.getDndOnFridaysOnly()) {
            Log.d(TAG, "onDateSet: " + selectedDate);
            bindRegularEntry(selectedDate);
        } else {
            Log.d("App", "onDateSet:  picked date = " + selectedDate);
            bindFridayEntry(selectedDate);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.fajr_switch:
                if (mEntryModel != null) {
                    mEntryModel.setFajrSilent(isChecked);
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.dhuhr_switch:
                if (mEntryModel != null) {
                    mEntryModel.setDhuhrSilent(isChecked);
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.maghrib_switch:
                if (mEntryModel != null) {
                    mEntryModel.setMaghribSilent(isChecked);
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.asr_switch:
                if (mEntryModel != null) {
                    mEntryModel.setAsrSilent(isChecked);
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.isha_switch:
                if (mEntryModel != null) {
                    mEntryModel.setIshaSilent(isChecked);
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.friday_switch:
                mFridayEntry.setSilent(isChecked);
                mViewModel.updateFridayEntry(mFridayEntry);
                break;
            case R.id.checkbox:
                mPrefHelper.setDndForFridaysOnly(isChecked);
                mViewBinding.invalidateAll();
                break;
        }

    }

//    private synchronized void launchTransition(TransitionDrawable transition, boolean isChecked) {
//        if (isChecked)
//            transition.startTransition(400);
//        else
//            transition.reverseTransition(400);
//    }
}
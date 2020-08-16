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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.databinding.FragmentHomeBinding;
import nz.co.redice.azansilenttime.repo.local.entity.EntryModel;
import nz.co.redice.azansilenttime.utils.PrefHelper;
import nz.co.redice.azansilenttime.view.presentation.DatePickerFragment;
import nz.co.redice.azansilenttime.view.presentation.TimePickerFragment;
import nz.co.redice.azansilenttime.viewmodel.HomeViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "App HomeFragment";
    @Inject PrefHelper mPrefHelper;
    private HomeViewModel mViewModel;
    private FragmentHomeBinding mViewBinding;
    private EntryModel mEntryModel;

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
        setViewListeners();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        mViewBinding.checkbox.setChecked(mPrefHelper.getDndOnFridaysOnly());


        mViewModel.getRegularDatabaseSize().observe(getViewLifecycleOwner(), integer -> {
            if (integer >= 365) {
                displaySelectedEntry(LocalDate.from(ZonedDateTime.now(ZoneId.systemDefault())));
            } else {
                mViewModel.requestPrayerCalendar();
            }
        });

            mViewModel.getFridayTableCount().observe(getViewLifecycleOwner(), integer -> {
                if (integer > 0) {
                    displayNextFridayEntry(LocalDate.now(ZoneId.systemDefault()));
                } else {
                    mViewModel.populateFridayTable();
                }
            });
    }


    private void setViewListeners() {
        //setting date picker
        mViewBinding.dateView.setOnClickListener(this::showDatePickerDialog);
        mViewBinding.fridayDateView.setOnClickListener(this::showDatePickerDialog);

        mViewBinding.fridayTime.setOnClickListener(this::showTimePickerDialog);

        mViewBinding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {

                    mPrefHelper.setDndOnFridaysOnly(true);

                    mViewBinding.dateView.setVisibility(View.GONE);
                    mViewBinding.fajrBtn.setVisibility(View.GONE);
                    mViewBinding.fajrTime.setVisibility(View.GONE);
                    mViewBinding.fajrName.setVisibility(View.GONE);

                    mViewBinding.dhuhrBtn.setVisibility(View.GONE);
                    mViewBinding.dhuhrTime.setVisibility(View.GONE);
                    mViewBinding.dhuhrName.setVisibility(View.GONE);

                    mViewBinding.asrBtn.setVisibility(View.GONE);
                    mViewBinding.asrTime.setVisibility(View.GONE);
                    mViewBinding.asrName.setVisibility(View.GONE);

                    mViewBinding.maghribBtn.setVisibility(View.GONE);
                    mViewBinding.maghribTime.setVisibility(View.GONE);
                    mViewBinding.maghribName.setVisibility(View.GONE);

                    mViewBinding.ishaBtn.setVisibility(View.GONE);
                    mViewBinding.ishaTime.setVisibility(View.GONE);
                    mViewBinding.ishaName.setVisibility(View.GONE);

                    mViewBinding.fridayDateView.setVisibility(View.VISIBLE);
                    mViewBinding.fridayName.setVisibility(View.VISIBLE);
                    mViewBinding.fridayTime.setVisibility(View.VISIBLE);

                } else {

                    mPrefHelper.setDndOnFridaysOnly(false);

                    mViewBinding.dateView.setVisibility(View.VISIBLE);

                    mViewBinding.fajrBtn.setVisibility(View.VISIBLE);
                    mViewBinding.fajrTime.setVisibility(View.VISIBLE);
                    mViewBinding.fajrName.setVisibility(View.VISIBLE);

                    mViewBinding.dhuhrBtn.setVisibility(View.VISIBLE);
                    mViewBinding.dhuhrTime.setVisibility(View.VISIBLE);
                    mViewBinding.dhuhrName.setVisibility(View.VISIBLE);

                    mViewBinding.asrBtn.setVisibility(View.VISIBLE);
                    mViewBinding.asrTime.setVisibility(View.VISIBLE);
                    mViewBinding.asrName.setVisibility(View.VISIBLE);

                    mViewBinding.maghribBtn.setVisibility(View.VISIBLE);
                    mViewBinding.maghribTime.setVisibility(View.VISIBLE);
                    mViewBinding.maghribName.setVisibility(View.VISIBLE);

                    mViewBinding.ishaBtn.setVisibility(View.VISIBLE);
                    mViewBinding.ishaTime.setVisibility(View.VISIBLE);
                    mViewBinding.ishaName.setVisibility(View.VISIBLE);

                    mViewBinding.fridayDateView.setVisibility(View.GONE);
                    mViewBinding.fridayName.setVisibility(View.GONE);
                    mViewBinding.fridayTime.setVisibility(View.GONE);
                }
            }
        });

        //setting mute buttons
        mViewBinding.asrBtn.setOnClickListener(this::onClick);
        mViewBinding.fajrBtn.setOnClickListener(this::onClick);
        mViewBinding.ishaBtn.setOnClickListener(this::onClick);
        mViewBinding.maghribBtn.setOnClickListener(this::onClick);
        mViewBinding.dhuhrBtn.setOnClickListener(this::onClick);
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

    private void displayNextFridayEntry(LocalDate selectedDate) {
        mViewModel.getNextFridayEntry(selectedDate).observe(getViewLifecycleOwner(),
                fridayEntry -> {
                    if (fridayEntry != null) {
                        mViewBinding.setFriday(fridayEntry);
                    }
                });
    }

    private void displaySelectedEntry(LocalDate date) {

        mViewModel.getSelectedEntry(date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()).observe(getViewLifecycleOwner(), selected -> {
            if (selected != null) {
                mEntryModel = selected;
                mViewBinding.setEntry(mEntryModel);
            } else {
                mViewModel.requestPrayerCalendar();
            }

        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dhuhr_btn:
                if (mEntryModel != null) {
                    mEntryModel.setDhuhrSilent(!mEntryModel.getDhuhrSilent());
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.asr_btn:
                if (mEntryModel != null) {
                    mEntryModel.setAsrSilent(!mEntryModel.getAsrSilent());
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.fajr_btn:
                if (mEntryModel != null) {
                    mEntryModel.setFajrSilent(!mEntryModel.getFajrSilent());
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.maghrib_btn:
                if (mEntryModel != null) {
                    mEntryModel.setMaghribSilent(!mEntryModel.getMaghribSilent());
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
            case R.id.isha_btn:
            default:
                if (mEntryModel != null) {
                    mEntryModel.setIshaSilent(!mEntryModel.getIshaSilent());
                    mViewModel.updateRegularEntry(mEntryModel);
                }
                break;
        }
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
            displaySelectedEntry(selectedDate);
        } else {
            Log.d("App", "onDateSet:  picked date = " + selectedDate);
            displayNextFridayEntry(selectedDate);

        }
    }

}
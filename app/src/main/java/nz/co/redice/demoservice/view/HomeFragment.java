package nz.co.redice.demoservice.view;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
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
import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.databinding.HomeFragmentBinding;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.view.presentation.DatePickerFragment;
import nz.co.redice.demoservice.viewmodel.HomeScreenViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    @Inject Repository mRepository;
    @Inject PreferencesHelper mPreferencesHelper;
    private HomeScreenViewModel mViewModel;
    private HomeFragmentBinding mViewBinding;
    private EntryModel mEntryModel;
    private boolean mFragmentCreated = false;


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
        setViewListeners();

        mViewModel.getDatabaseSize().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer >= 365 ) {
                    // setting up current day
                    Long currentDayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
                    Log.d("App", "onViewCreated: " + currentDayEpoch);
                    getTimingsForSelectedDate(currentDayEpoch);
                } else {
                    mViewModel.fillUpDatabase();
                }
            }
        });
    }

    private void setViewListeners() {
        //setting date picker
        mViewBinding.dateView.setOnClickListener(this::showDatePickerDialog);

        //setting mute buttons
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
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        LocalDate selectedDate = LocalDate.of(currentYear, ++month, dayOfMonth);
        Long selectedEpoch = selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        getTimingsForSelectedDate(selectedEpoch);
    }


    private void getTimingsForSelectedDate(Long date) {
        mViewModel.getTimesForSelectedDate(date).observe(getViewLifecycleOwner(), selected -> {
            if (selected != null) {
                Log.d("App", "getTimingsForSelectedDate: " + selected.getDateString());
                mEntryModel = selected;
                mViewBinding.setEntry(mEntryModel);

            } else {
                mViewModel.fillUpDatabase();
            }

        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dhuhr_btn:
                mEntryModel.setDhuhrMuteOn(!mEntryModel.getDhuhrMuteOn());
                mViewModel.updateEntry(mEntryModel);
                break;
            case R.id.asr_btn:
                mEntryModel.setAsrMuteOn(!mEntryModel.getAsrMuteOn());
                mViewModel.updateEntry(mEntryModel);
                break;
            case R.id.fajr_btn:
                mEntryModel.setFajrMuteOn(!mEntryModel.getFajrMuteOn());
                mViewModel.updateEntry(mEntryModel);
                break;
            case R.id.maghrib_btn:
                mEntryModel.setMaghribMuteOn(!mEntryModel.getMaghribMuteOn());
                mViewModel.updateEntry(mEntryModel);
                break;
            case R.id.isha_btn:
            default:
                mEntryModel.setIshaMuteOn(!mEntryModel.getIshaMuteOn());
                mViewModel.updateEntry(mEntryModel);
                break;
        }
    }
}
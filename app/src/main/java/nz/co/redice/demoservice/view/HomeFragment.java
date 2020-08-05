package nz.co.redice.demoservice.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.databinding.FragmentHomeBinding;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.view.presentation.DatePickerFragment;
import nz.co.redice.demoservice.viewmodel.HomeViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

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
        //Override it for handling items
        int menuItemId = item.getItemId();
        switch (menuItemId) {
            case R.id.settings_id:
                NavHostFragment.findNavController(this).navigate(R.id.fromHomeToSettings);
                return true; //return true, if is handled
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

        mViewModel.getDatabaseSize().observe(getViewLifecycleOwner(), integer -> {
            if (integer >= 365) {
                // setting up current day
                Long currentDayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
                getTimingsForSelectedDate(currentDayEpoch);
            } else {
                mViewModel.fillUpDaBase();
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
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
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
                mEntryModel = selected;
                mViewBinding.setEntry(mEntryModel);
            } else {
                mViewModel.fillUpDaBase();
            }

        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dhuhr_btn:
                if (mEntryModel != null) {
                    mEntryModel.setDhuhrMuteOn(!mEntryModel.getDhuhrMuteOn());
                    mViewModel.updateEntry(mEntryModel);
                }
                break;
            case R.id.asr_btn:
                if (mEntryModel != null) {
                    mEntryModel.setAsrMuteOn(!mEntryModel.getAsrMuteOn());
                    mViewModel.updateEntry(mEntryModel);
                }
                break;
            case R.id.fajr_btn:
                if (mEntryModel != null) {
                    mEntryModel.setFajrMuteOn(!mEntryModel.getFajrMuteOn());
                    mViewModel.updateEntry(mEntryModel);
                }
                break;
            case R.id.maghrib_btn:
                if (mEntryModel != null) {
                    mEntryModel.setMaghribMuteOn(!mEntryModel.getMaghribMuteOn());
                    mViewModel.updateEntry(mEntryModel);
                }
                break;
            case R.id.isha_btn:
            default:
                if (mEntryModel != null) {
                    mEntryModel.setIshaMuteOn(!mEntryModel.getIshaMuteOn());
                    mViewModel.updateEntry(mEntryModel);
                }
                break;
        }
    }

}
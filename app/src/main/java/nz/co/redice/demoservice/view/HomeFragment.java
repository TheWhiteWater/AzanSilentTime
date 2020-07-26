package nz.co.redice.demoservice.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.databinding.HomeFragmentBinding;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.models.Azan;
import nz.co.redice.demoservice.utils.PrefHelper;
import nz.co.redice.demoservice.view.presentation.DatePickerFragment;
import nz.co.redice.demoservice.viewmodel.HomeScreenViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private HomeScreenViewModel mViewModel;
    @Inject
    Repository mRepository;
    @Inject
    PrefHelper mPrefHelper;
    private HomeFragmentBinding mBinding;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        mBinding.setCurrentDate(DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

//        Intent intent = new Intent(this, Service.class);
//        startService(intent);
//        mPrefHelper.setDatabaseUpdateStatus(false);

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(getContext(), this);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);

        Log.d("App", "onCreate: " + mPrefHelper.getDatabaseUpdateStatus());
        if (!mPrefHelper.getDatabaseUpdateStatus()) {
            mRepository.requestAnnualCalendar(-40.3596, 175.61);
            mPrefHelper.setDatabaseUpdateStatus(true);
        }

        mViewModel.getAnnualCalendar().observe(getViewLifecycleOwner(), new Observer<List<Azan>>() {
            @Override
            public void onChanged(List<Azan> azans) {
                if (azans != null)
                    Log.d("App", "onChanged: " + azans.size());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mBinding.setCurrentDate(DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime()));
    }

}
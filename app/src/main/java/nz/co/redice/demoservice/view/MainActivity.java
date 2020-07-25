package nz.co.redice.demoservice.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.utils.Converters;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.service.Service;
import nz.co.redice.demoservice.viewmodel.HomeScreenViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private HomeScreenViewModel mViewModel;

    @Inject
    Repository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, Service.class);
        startService(intent);
        mViewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);
        mRepository.requestAnnualCalendar(-40.3596, 175.61);

//        mViewModel.getAnnualCalendar().observe(this, new Observer<List<AzanDay>>() {
//            @Override
//            public void onChanged(List<AzanDay> azanDays) {
//                Log.d("App", "onChanged: " + azanDays.size());
//            }
//        });

    }
}
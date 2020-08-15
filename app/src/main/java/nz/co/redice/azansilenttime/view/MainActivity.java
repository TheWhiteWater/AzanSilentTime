package nz.co.redice.azansilenttime.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.databinding.ActivityMainBinding;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.utils.PrefHelper;
import nz.co.redice.azansilenttime.utils.ServiceHelper;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity  {

    @Inject PrefHelper mPrefHelper;
    @Inject Repository mRepository;
    @Inject ServiceHelper mServiceHelper;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mServiceHelper.startService(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mServiceHelper.doBindService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mServiceHelper.doUnbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (navController.getCurrentDestination().getLabel().equals("HomeFragment") ||
                navController.getCurrentDestination().getLabel().equals("LocationFragment")) {
            // do nothing
        } else {
            super.onBackPressed();
        }
    }


}
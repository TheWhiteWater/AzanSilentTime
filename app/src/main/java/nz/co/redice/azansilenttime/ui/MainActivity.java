package nz.co.redice.azansilenttime.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.databinding.ActivityMainBinding;
import nz.co.redice.azansilenttime.services.foreground_service.BindService;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject BindService mBindService;
    private ActivityMainBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        setSupportActionBar(mViewBinding.toolbar);
        mBindService.startService(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBindService.doBindService(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBindService.doUnbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewBinding = null;
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (!Objects.equals(Objects.requireNonNull(navController.getCurrentDestination()).getLabel(), "HomeFragment") &&
                !Objects.equals(navController.getCurrentDestination().getLabel(), "LocationFragment")) {
            super.onBackPressed();
        }

    }


}
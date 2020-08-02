package nz.co.redice.demoservice.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.databinding.ActivityMainBinding;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.utils.PrefHelper;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject PrefHelper mPrefHelper;
    @Inject Repository mRepository;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}
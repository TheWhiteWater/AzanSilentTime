package nz.co.redice.demoservice.view;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import nz.co.redice.demoservice.databinding.ActivityMainBinding;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.view.presentation.Category;
import nz.co.redice.demoservice.view.presentation.PagerAdapter;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject PreferencesHelper mPreferencesHelper;
    @Inject Repository mRepository;
    private PagerAdapter mPagerAdapter;
    private ViewPager2 mViewPager;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        List<Category> categories = Arrays.asList(
                Category.HOME,
                Category.SETTINGS);

        mPagerAdapter = new PagerAdapter(this, categories);
        mViewPager = mBinding.viewpager;
        mViewPager.setAdapter(mPagerAdapter);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}
package nz.co.redice.demoservice.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.databinding.ActivityMainBinding;
import nz.co.redice.demoservice.services.ForegroundService;
import nz.co.redice.demoservice.view.presentation.Category;
import nz.co.redice.demoservice.view.presentation.PagerAdapter;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

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

        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
    }


}
package nz.co.redice.demoservice.view.presentation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import nz.co.redice.demoservice.view.HomeFragment;
import nz.co.redice.demoservice.view.SettingsFragment;


public class PagerAdapter extends FragmentStateAdapter {

    private List<Category> mTabs;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> mTabs) {
        super(fragmentActivity);
        this.mTabs = mTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            default:
                return new HomeFragment();
            case 1:
                return new SettingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return mTabs.size();
    }

}

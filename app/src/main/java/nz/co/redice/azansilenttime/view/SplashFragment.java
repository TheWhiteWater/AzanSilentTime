package nz.co.redice.azansilenttime.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.utils.PrefHelper;

@AndroidEntryPoint
public class SplashFragment extends Fragment {
    @Inject PrefHelper mPrefHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())).hide();
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        Observable.timer(1200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> shiftToNextScreen(),
                        error -> Log.d("App", "onCreateView rxtimer error: " + error.getMessage() ));
        return view;
    }

    private void shiftToNextScreen() {
        if (!mPrefHelper.getLocationStatus()) {
            NavHostFragment.findNavController(this).navigate(R.id.fromSplashToLocation);
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.fromSplashScreenToHome);
        }
    }
}
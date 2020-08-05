package nz.co.redice.demoservice.view;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.databinding.FragmentLocationBinding;
import nz.co.redice.demoservice.utils.LocationHelper;
import nz.co.redice.demoservice.utils.PermissionHelper;
import nz.co.redice.demoservice.utils.PrefHelper;
import nz.co.redice.demoservice.viewmodel.AutoLocationViewModel;

@AndroidEntryPoint
public class LocationFragment extends Fragment implements View.OnClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 34;
    @Inject PermissionHelper mPermissionHelper;
    @Inject PrefHelper mPrefHelper;
    @Inject LocationHelper mLocationHelper;
    private FragmentLocationBinding mViewBinding;
    private AutoLocationViewModel mViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewBinding = FragmentLocationBinding.inflate(inflater, container, false);
        View view = mViewBinding.getRoot();
        mViewModel = new ViewModelProvider(this).get(AutoLocationViewModel.class);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check that the user hasn't revoked permissions by going to Settings.
        if (!mPrefHelper.getLocationPermissionStatus()) {
            if (!mPermissionHelper.isLocationPermissionGranted()) {
                requestLocationPermissions();
            }
        }
        mViewBinding.autoLocateBtn.setOnClickListener(this);
        mViewBinding.saveLocationBtn.setOnClickListener(this);
        getLastKnownAddress();
    }


    private void requestLocationPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("App", "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    mViewBinding.autoLocationLayout,
                    "Location permission is needed for core functionality",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                        mPrefHelper.setLocationPermissionStatus(true);
                    }).show();
        } else {
            Log.i("App", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.auto_locate_btn:
                getLastKnownAddress();
                break;
            case R.id.save_location_btn:
            default:
                mViewModel.setLocation();
                NavHostFragment.findNavController(this).navigate(R.id.fromLocationToHome);
                break;
        }
    }

    private void getLastKnownAddress() {
        mViewModel.getLastAddress().observe(getViewLifecycleOwner(), s -> mViewBinding.locationTv.setText(s));
    }


}

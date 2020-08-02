package nz.co.redice.demoservice.view;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.databinding.FragmentAutoLocationBinding;
import nz.co.redice.demoservice.utils.PermissionHelper;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.viewmodel.AutoLocationViewModel;

@AndroidEntryPoint
public class AutoLocationFragment extends Fragment implements View.OnClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 34;
    @Inject PermissionHelper mPermissionHelper;
    @Inject PreferencesHelper mPreferencesHelper;
    private FragmentAutoLocationBinding mViewBinding;
    private AutoLocationViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewBinding = FragmentAutoLocationBinding.inflate(inflater, container, false);
        View view = mViewBinding.getRoot();
        mViewModel = new ViewModelProvider(this).get(AutoLocationViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check that the user hasn't revoked permissions by going to Settings.
        if (!mPreferencesHelper.getAppsLocationPermissionStatus()) {
            if (!mPermissionHelper.isLocationPermissionGranted()) {
                requestLocationPermissions();
            }
        }

        mViewBinding.autoLocateBtn.setOnClickListener(this);
        mViewBinding.saveLocationBtn.setOnClickListener(this);
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
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
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
                break;
            case R.id.save_location_btn:
            default:
                break;
        }
    }
}

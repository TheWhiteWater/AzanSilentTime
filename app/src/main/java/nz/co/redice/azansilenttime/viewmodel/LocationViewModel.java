package nz.co.redice.azansilenttime.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.utils.LocationHelper;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;

public class LocationViewModel extends AndroidViewModel {

    private static final String TAG = "App LocationViewModel";
    private LocationHelper mLocationHelper;
    private SharedPreferencesHelper mSharedPreferencesHelper;

    private Location mLocation;
    private LocationCallback mLocationCallback;

    private MutableLiveData<String> position = new MutableLiveData<>();
    public LiveData<String> lastKnownPosition = position;


    @SuppressLint("MissingPermission")
    @ViewModelInject
    public LocationViewModel(@NonNull Application application, LocationHelper locationHelper,
                             Repository repository,
                             @Assisted SavedStateHandle savedStateHandle, SharedPreferencesHelper sharedPreferencesHelper) {
        super(application);
        mLocationHelper = locationHelper;
        mSharedPreferencesHelper = sharedPreferencesHelper;

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult: receiving location result from LocationHelper");
                if (locationResult != null) {
                    Log.d(TAG, "onLocationResult: locationResult is not null" );
                    mLocation = locationResult.getLastLocation();
                    String locationText = mLocationHelper.locationToArea(locationResult.getLastLocation());
                    if (!locationText.isEmpty()) {
                        position.setValue(locationText);
                        Log.d(TAG, "onLocationResult: address: " +  locationText);
                    }
                } else {
                    Log.d(TAG, "onLocationResult: result is null");
                }
            }
        };

        mLocationHelper.getAdminArea(mLocationCallback);
    }

    @SuppressLint("MissingPermission")
    public void getAdminArea() {
        mLocationHelper.getAdminArea(mLocationCallback);
        Log.d(TAG, "getAdminArea: requesting LocationHelper");
    }


    public void saveLocationInPrefs() {
        mSharedPreferencesHelper.setLocationStatus(true);
        mSharedPreferencesHelper.setLocationText(position.getValue());
        mSharedPreferencesHelper.setLatitude((float) mLocation.getLatitude());
        mSharedPreferencesHelper.setLongitude((float) mLocation.getLongitude());
    }


    public void removeLocationRequest() {
        mLocationHelper.removeLocationUpdates(mLocationCallback);
    }

}

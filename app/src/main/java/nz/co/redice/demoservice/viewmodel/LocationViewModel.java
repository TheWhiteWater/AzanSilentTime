package nz.co.redice.demoservice.viewmodel;

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

import nz.co.redice.demoservice.utils.LocationHelper;
import nz.co.redice.demoservice.utils.PrefHelper;

public class LocationViewModel extends AndroidViewModel {

    private static final String TAG = "App LocationViewModel";
    private final SavedStateHandle savedStateHandle;
    private LocationHelper mLocationHelper;
    private PrefHelper mPrefHelper;

    private Location mLocation;
    private LocationCallback mLocationCallback;

    private MutableLiveData<String> position = new MutableLiveData<>();
    public LiveData<String> lastKnownPosition = position;


    @SuppressLint("MissingPermission")
    @ViewModelInject
    public LocationViewModel(@NonNull Application application, LocationHelper locationHelper,
                             @Assisted SavedStateHandle savedStateHandle, PrefHelper prefHelper) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        mLocationHelper = locationHelper;
        mPrefHelper = prefHelper;



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
        mPrefHelper.setLocationStatus(true);
        mPrefHelper.setLocationText(position.getValue());
        mPrefHelper.setLatitude((float) mLocation.getLatitude());
        mPrefHelper.setLongitude((float) mLocation.getLongitude());
    }


    public void removeLocationRequest() {
        mLocationHelper.removeLocationUpdates(mLocationCallback);
    }
}

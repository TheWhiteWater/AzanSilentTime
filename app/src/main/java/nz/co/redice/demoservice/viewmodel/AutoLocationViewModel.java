package nz.co.redice.demoservice.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Locale;

import nz.co.redice.demoservice.utils.LocationHelper;
import nz.co.redice.demoservice.utils.PrefHelper;

public class AutoLocationViewModel extends AndroidViewModel {


    private final SavedStateHandle savedStateHandle;
    private Context mContext;
    private LocationHelper mLocationHelper;
    private PrefHelper mPrefHelper;


    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;


    private MutableLiveData<String> position = new MutableLiveData<>();
    public LiveData<String> lastKnownPosition = position;


    @SuppressLint("MissingPermission")
    @ViewModelInject
    public AutoLocationViewModel(@NonNull Application application, LocationHelper locationHelper,
                                 @Assisted SavedStateHandle savedStateHandle, PrefHelper prefHelper) {
        super(application);
        mContext = application;
        this.savedStateHandle = savedStateHandle;
        mLocationHelper = locationHelper;
        mPrefHelper = prefHelper;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application);

        mLocationRequest = new LocationRequest()
                .setInterval(1)
                .setFastestInterval(1)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    mLocation = locationResult.getLastLocation();
                    String locationText = getAdminArea(locationResult.getLastLocation());
                    if (!locationText.isEmpty()) {
                        position.setValue(locationText);
                    }
                }
            }
        };

        getLastKnownAddress();
    }

    @SuppressLint("MissingPermission")
    public void getLastKnownAddress() {
        mFusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.getMainLooper());
    }


    private String getAdminArea(Location location) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String addressText = "";
        try {
            if (location != null) {
                Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                addressText = address.getLocality() + ",\n " + address.getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressText;
    }

    public void setLocation() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        mPrefHelper.setLocationPermissionStatus(true);
        mPrefHelper.setLatitude((float) mLocation.getLatitude());
        mPrefHelper.setLongitude((float) mLocation.getLongitude());
    }


}

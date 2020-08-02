package nz.co.redice.demoservice.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private MutableLiveData<String> _lastAddress = new MutableLiveData<>();
    private LiveData<String> mLastAddress = _lastAddress;

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
        getLastKnownAddress();
    }

    @SuppressLint("MissingPermission")
    public void getLastKnownAddress() {
        mFusedLocationProviderClient
                .getLastLocation()
                .addOnSuccessListener(location ->
                {
                    mLocation = location;
                    _lastAddress.setValue(getAdminArea(location));
                });
    }

    public LiveData<String> getLastAddress() {
        getLastKnownAddress();
        return mLastAddress;
    }

    private String getAdminArea(Location location) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "Cannot find Address.");
            e.printStackTrace();
        }
        return addresses.get(0) == null ? "Address is not found" :
                addresses.get(0).getLocality() + ",\n " + addresses.get(0).getCountryName();
    }

    public void setLocation() {
        mPrefHelper.setLocationPermissionStatus(true);
        mPrefHelper.setLatitude((float) mLocation.getLatitude());
        mPrefHelper.setLongitude((float) mLocation.getLongitude());
    }
}

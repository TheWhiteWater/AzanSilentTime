package nz.co.redice.azansilenttime.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class LocationHelper {
    private static final String TAG = "App LocationHelper";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    @Inject PermissionHelper mPermissionHelper;
    @Inject PrefHelper mPrefHelper;
    private Context mContext;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;


    @Inject
    public LocationHelper(@ApplicationContext Context context) {
        mContext = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        createLocationRequest();
    }

    @SuppressLint("MissingPermission")
    public void getAdminArea(LocationCallback callback) {
        mFusedLocationClient
                .requestLocationUpdates(mLocationRequest, callback, Looper.getMainLooper());
        Log.d(TAG, "getLastKnownAddress: requesting location updates ...");
    }


    public String locationToArea(Location location) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String addressText = "";
        try {
            if (location != null) {
                Log.d(TAG, "locationToArea: location is not null");
                Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                if (address.getLocality() != null)
                    return address.getLocality() + ", " + address.getCountryName();
                if (address.getAdminArea() != null)
                    return address.getAdminArea() + ", " + address.getCountryName();
                else
                    return address.getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressText;
    }

    public void removeLocationUpdates(LocationCallback callback) {
        mFusedLocationClient.removeLocationUpdates(callback);
        Log.d(TAG, "removeLocationUpdates: removing location updates");
    }

    public String locationToAddress(Location location) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "Cannot find Address.");
            e.printStackTrace();
        }
        return addresses.get(0) == null ? "Check your internet connection" : String.valueOf(addresses.get(0).getAddressLine(0));
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

}

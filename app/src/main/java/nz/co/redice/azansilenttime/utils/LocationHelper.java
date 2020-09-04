package nz.co.redice.azansilenttime.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.azansilenttime.R;

@Singleton
public class LocationHelper {
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 500;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    @Inject PermissionHelper mPermissionHelper;
    @Inject SharedPreferencesHelper mSharedPreferencesHelper;
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
    }


    public String locationToArea(Location location) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String addressText = mContext.getString(R.string.empty_string);
        try {
            if (location != null) {
                Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                if (address.getLocality() != null)
                    return String.format(mContext.getString(R.string.location_to_area_text_format), address.getLocality(), address.getCountryName());
                if (address.getAdminArea() != null)
                    return String.format(mContext.getString(R.string.location_to_area_text_format), address.getAdminArea(), address.getCountryName());
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
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

}

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.utils.LocationHelper;
import nz.co.redice.azansilenttime.utils.PrefHelper;

public class LocationViewModel extends AndroidViewModel {

    private static final String TAG = "App LocationViewModel";
    private final SavedStateHandle savedStateHandle;
    private LocationHelper mLocationHelper;
    private PrefHelper mPrefHelper;
    private Repository mRepository;

    private Location mLocation;
    private LocationCallback mLocationCallback;

    private MutableLiveData<String> position = new MutableLiveData<>();
    public LiveData<String> lastKnownPosition = position;


    @SuppressLint("MissingPermission")
    @ViewModelInject
    public LocationViewModel(@NonNull Application application, LocationHelper locationHelper,
                             Repository repository,
                             @Assisted SavedStateHandle savedStateHandle, PrefHelper prefHelper) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        mLocationHelper = locationHelper;
        mPrefHelper = prefHelper;
        mRepository = repository;



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


    public void requestInitialDataBase() {
        mRepository.requestPrayerCalendar();
    }
}

package nz.co.redice.azansilenttime.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.utils.LocationHelper;
import nz.co.redice.azansilenttime.utils.PrefHelper;
import nz.co.redice.azansilenttime.utils.ServiceHelper;

@AndroidEntryPoint
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private static final String TAG = "App Pref";
    private static final String LOCATION = "location";
    private static final String MUTE_PERIOD = "mute_period";
    private static final String CALCULATION_METHOD = "calculation_method";
    private static final String SCHOOL = "school";
    private static final String MIDNIGHT_MODE = "midnight_mode";
    @Inject PrefHelper mPrefHelper;
    @Inject LocationHelper mLocationHelper;
    @Inject Repository mRepository;
    @Inject ServiceHelper mServiceHelper;
    private boolean isDatabaseUpdateRequired;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDatabaseUpdateRequired) {
                    mRepository.deletePrayerCalendar();
                    mPrefHelper.setDatabaseNeedsUpdate(true);
                }
                getBackToHomeScreen();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getBackToHomeScreen() {
        NavHostFragment.findNavController(this).navigate(R.id.fromSettingsToHome);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Preference locationPreference = findPreference(LOCATION);
        if (locationPreference != null) {
            locationPreference.setSummary(mPrefHelper.getLocationText());
            locationPreference.setOnPreferenceClickListener(this);
        }

        Preference mutePeriodPreference = findPreference(MUTE_PERIOD);
        if (mutePeriodPreference != null) {
            mutePeriodPreference.setSummaryProvider(preference -> mPrefHelper.getDndPeriod() + " min");
            mutePeriodPreference.setOnPreferenceChangeListener(this);
        }

        Preference calculationMethodPreference = findPreference(CALCULATION_METHOD);
        if (calculationMethodPreference != null) {
            calculationMethodPreference.setSummaryProvider(preference -> {
                // TODO: 24.08.2020 summary correction through method or smth one item is missing
                String[] values = getResources().getStringArray(R.array.calculation_methods_entries);
                return values[mPrefHelper.getCalculationMethod()];
            });
            calculationMethodPreference.setOnPreferenceChangeListener(this);
        }

        Preference calculationSchoolPreference = findPreference(SCHOOL);
        if (calculationSchoolPreference != null) {
            calculationSchoolPreference.setSummaryProvider(preference -> {
                String[] values = getResources().getStringArray(R.array.school_entries);
                return values[mPrefHelper.getCalculationSchool()];
            });
            calculationSchoolPreference.setOnPreferenceChangeListener(this);
        }

        Preference midnightModePreference = findPreference(MIDNIGHT_MODE);
        if (midnightModePreference != null) {
            midnightModePreference.setSummaryProvider(preference -> {
                String[] values = getResources().getStringArray(R.array.midnight_mode_entries);
                return values[mPrefHelper.getMidnightMode()];
            });
            midnightModePreference.setOnPreferenceChangeListener(this);
        }


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case MUTE_PERIOD:
                mPrefHelper.setDndPeriod((String) newValue);
                isDatabaseUpdateRequired = false;
                Log.d(TAG, "onCreatePreferences: DndPeriod " + mPrefHelper.getDndPeriod());
                break;
            case CALCULATION_METHOD:
                mPrefHelper.setCalculationMethod((String) newValue);
                isDatabaseUpdateRequired = true;

                Log.d(TAG, "onCreatePreferences: CalculationMethod " + mPrefHelper.getCalculationMethod());
                break;
            case SCHOOL:
                mPrefHelper.setCalculationSchool((String) newValue);
                isDatabaseUpdateRequired = true;

                Log.d(TAG, "onCreatePreferences: CalculationSchool " + mPrefHelper.getCalculationSchool());
                break;
            case MIDNIGHT_MODE:
                mPrefHelper.setMidnightMode((String) newValue);
                isDatabaseUpdateRequired = true;

                Log.d(TAG, "onCreatePreferences: MidnightMode " + mPrefHelper.getMidnightMode());
                break;
        }


        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case LOCATION:
            default:
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Log.d(TAG, "onLocationResult: receiving location result from LocationHelper");
                        if (locationResult != null) {
                            Log.d(TAG, "onLocationResult: locationResult is not null");
                            String locationText = mLocationHelper.locationToArea(locationResult.getLastLocation());
                            if (!locationText.isEmpty()) {
                                preference.setSummary(locationText);
                                mPrefHelper.setLocationText(locationText);
                                mPrefHelper.setLongitude((float) locationResult.getLastLocation().getLongitude());
                                mPrefHelper.setLatitude((float) locationResult.getLastLocation().getLatitude());
                                mLocationHelper.removeLocationUpdates(this);
                                isDatabaseUpdateRequired = true;
                                Log.d(TAG, "onLocationResult: address: " + locationText);
                            }
                        } else {
                            Log.d(TAG, "onLocationResult: result is null");
                        }
                    }
                };

                mLocationHelper.getAdminArea(locationCallback);
                break;


        }
        return true;
    }


}
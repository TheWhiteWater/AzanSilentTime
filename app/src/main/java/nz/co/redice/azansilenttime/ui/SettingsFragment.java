package nz.co.redice.azansilenttime.ui;

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
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;
import nz.co.redice.azansilenttime.services.foreground_service.BindService;

@AndroidEntryPoint
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private static final String TAG = "App Pref";
    private static final String LOCATION = "location";
    private static final String MUTE_PERIOD = "mute_period";
    private static final String CALCULATION_METHOD = "calculation_method";
    private static final String SCHOOL = "school";
    private static final String MIDNIGHT_MODE = "midnight_mode";
    @Inject SharedPreferencesHelper mSharedPreferencesHelper;
    @Inject LocationHelper mLocationHelper;
    @Inject Repository mRepository;
    @Inject BindService mBindService;
    private boolean isDatabaseUpdateRequired;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDatabaseUpdateRequired) {
                    mRepository.deletePrayerCalendar();
                    mSharedPreferencesHelper.setRegularTableToBePopulated(true);
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
            locationPreference.setSummary(mSharedPreferencesHelper.getLocationText());
            locationPreference.setOnPreferenceClickListener(this);
        }

        Preference mutePeriodPreference = findPreference(MUTE_PERIOD);
        if (mutePeriodPreference != null) {
            mutePeriodPreference.setSummaryProvider(preference -> {
                int prefValue = mSharedPreferencesHelper.getDndPeriod();
                return prefValue == 60 ? getString(R.string.one_hour) : String.format("%d %s", prefValue, getString(R.string.min));
            });
            mutePeriodPreference.setOnPreferenceChangeListener(this);
        }

        Preference calculationMethodPreference = findPreference(CALCULATION_METHOD);
        if (calculationMethodPreference != null) {
            calculationMethodPreference.setSummaryProvider(preference -> {
                String[] values = getResources().getStringArray(R.array.calculation_methods_entries);
                return values[getCorrectedCalculationMethod(mSharedPreferencesHelper.getCalculationMethod())];
            });
            calculationMethodPreference.setOnPreferenceChangeListener(this);
        }


        Preference calculationSchoolPreference = findPreference(SCHOOL);
        if (calculationSchoolPreference != null) {
            calculationSchoolPreference.setSummaryProvider(preference -> {
                String[] values = getResources().getStringArray(R.array.school_entries);
                return values[mSharedPreferencesHelper.getCalculationSchool()];
            });
            calculationSchoolPreference.setOnPreferenceChangeListener(this);
        }

        Preference midnightModePreference = findPreference(MIDNIGHT_MODE);
        if (midnightModePreference != null) {
            midnightModePreference.setSummaryProvider(preference -> {
                String[] values = getResources().getStringArray(R.array.midnight_mode_entries);
                return values[mSharedPreferencesHelper.getMidnightMode()];
            });
            midnightModePreference.setOnPreferenceChangeListener(this);
        }


    }

    private int getCorrectedCalculationMethod(int intValue) {
        return intValue <= 5 ? intValue : --intValue;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case MUTE_PERIOD:
                mSharedPreferencesHelper.setDndPeriod((String) newValue);
                isDatabaseUpdateRequired = false;
                break;
            case CALCULATION_METHOD:
                mSharedPreferencesHelper.setCalculationMethod((String) newValue);
                isDatabaseUpdateRequired = true;
                break;
            case SCHOOL:
                mSharedPreferencesHelper.setCalculationSchool((String) newValue);
                isDatabaseUpdateRequired = true;

                break;
            case MIDNIGHT_MODE:
                mSharedPreferencesHelper.setMidnightMode((String) newValue);
                isDatabaseUpdateRequired = true;
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
                        if (locationResult != null) {
                            String locationText = mLocationHelper.locationToArea(locationResult.getLastLocation());
                            if (!locationText.isEmpty()) {
                                preference.setSummary(locationText);
                                mSharedPreferencesHelper.setLocationText(locationText);
                                mSharedPreferencesHelper.setLongitude((float) locationResult.getLastLocation().getLongitude());
                                mSharedPreferencesHelper.setLatitude((float) locationResult.getLastLocation().getLatitude());
                                mLocationHelper.removeLocationUpdates(this);
                                isDatabaseUpdateRequired = true;
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
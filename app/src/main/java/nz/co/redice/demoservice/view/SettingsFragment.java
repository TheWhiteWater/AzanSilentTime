package nz.co.redice.demoservice.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import nz.co.redice.demoservice.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
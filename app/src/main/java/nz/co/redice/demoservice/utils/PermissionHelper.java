package nz.co.redice.demoservice.utils;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.view.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

@Singleton
public class PermissionHelper {

    private static final String TAG = PermissionHelper.class.getSimpleName();
    private Context mContext;

    @Inject
    public PermissionHelper(@ApplicationContext Context context) {
        this.mContext = context;
    }

    public void getDNDPermission() {
        NotificationManager n = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !n.isNotificationPolicyAccessGranted()) {
            // Ask the user to grant access
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public boolean isLocationPermissionGranted() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }



}



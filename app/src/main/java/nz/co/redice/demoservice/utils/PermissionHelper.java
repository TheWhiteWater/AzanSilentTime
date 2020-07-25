package nz.co.redice.demoservice.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class PermissionHelper {

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


}

package nz.co.redice.azansilenttime.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.azansilenttime.services.ForegroundService;

@Singleton
public class ServiceHelper {

    private ForegroundService mService = null;
    private boolean isBound = false;
    private boolean mShouldUnbind;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = ((ForegroundService.LocalBinder) service).getService();
            Log.d("App", "onServiceConnected: connected to the ForegroundService");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    @Inject
    public ServiceHelper(@ApplicationContext Context context) {
    }

    public void startService(Context context) {
        Intent intent = new Intent(context, ForegroundService.class);
        context.startService(intent);
    }

    public void doBindService(Context context) {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (context.bindService(new Intent(context, ForegroundService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        } else {
            Log.e("App", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    public void doUnbindService(Context context) {
        if (mShouldUnbind) {
            context.unbindService(mServiceConnection);
            mShouldUnbind = false;
        }
    }




}

package nz.co.redice.azansilenttime.services.foreground_service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class BindService {

    private ForegroundService mService = null;
    private boolean mShouldUnbind;

    @Inject
    public BindService() {
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = ((ForegroundService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    public void startService(Context context) {
        Intent intent = new Intent(context, ForegroundService.class);
        context.startService(intent);
    }

    public void doBindService(Context context) {
        if (context.bindService(new Intent(context, ForegroundService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        }
    }

    public void doUnbindService(Context context) {
        if (mShouldUnbind) {
            context.unbindService(mServiceConnection);
            mShouldUnbind = false;
        }
    }




}

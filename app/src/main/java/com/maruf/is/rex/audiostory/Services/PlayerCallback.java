package com.maruf.is.rex.audiostory.Services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class PlayerCallback {
    private AudioPlayerServices mService;
    private boolean mBound = false;
    private Context context;


    public PlayerCallback(Context context) {
        this.context = context;
    }


    public AudioPlayerServices getmService() {
        return mService;
    }

    public boolean ismBound() {
        return mBound;
    }



    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void unBindService(){
        context.unbindService(serviceConnection);
        mBound= false;
    }

    public void bindService(){
        Intent intent = new Intent(context, AudioPlayerServices.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AudioPlayerServices.LocalBinder binder = (AudioPlayerServices.LocalBinder) iBinder;
            mService = binder.getService();

            mBound = true;

            Log.d("yes", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;

            Log.d("yes", " not connected");

        }
    };
}

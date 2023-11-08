package com.start.apps.pheezee.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class MyService extends Service {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // Clear SharedPreferences when the app is removed from the recent apps list
        clearSharedPreferences();
    }

    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data
        editor.apply();
        Log.e("1111111111233333333333333333333333333333444444444444444444444","Cleared");

    }
}

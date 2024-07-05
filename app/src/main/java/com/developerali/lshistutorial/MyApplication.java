package com.developerali.lshistutorial;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (DatabaseException e) {
            Log.d("Firebase", "Persistence already enabled");
        }
    }
}

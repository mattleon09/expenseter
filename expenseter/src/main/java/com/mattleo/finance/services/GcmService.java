package com.mattleo.finance.services;

import android.app.IntentService;
import android.content.Intent;

import com.mattleo.finance.App;
import com.mattleo.finance.api.Api;
import com.mattleo.finance.receivers.GcmBroadcastReceiver;

import javax.inject.Inject;

public class GcmService extends IntentService {
    @Inject Api api;

    public GcmService() {
        super(GcmService.class.getSimpleName());
    }

    @Override public void onCreate() {
        super.onCreate();
        App.with(getApplicationContext()).inject(this);
    }

    @Override protected void onHandleIntent(Intent intent) {
        api.sync();

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}

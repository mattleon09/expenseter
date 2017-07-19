package com.mattleo.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.mattleo.finance.App;
import com.mattleo.finance.api.Api;
import com.mattleo.finance.api.GcmRegistration;
import com.mattleo.finance.api.User;
import com.mattleo.finance.api.currencies.CurrenciesApi;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.utils.preferences.GeneralPrefs;

import javax.inject.Inject;

public class StartupService extends IntentService {
    @Inject User user;
    @Inject GcmRegistration gcmRegistration;
    @Inject Api api;
    @Inject CurrenciesApi currenciesApi;
    @Inject GeneralPrefs generalPrefs;
    @Inject CurrenciesManager currenciesManager;

    public StartupService() {
        super(StartupService.class.getSimpleName());
    }

    public static void start(Context context) {
        context.startService(new Intent(context, StartupService.class));
    }

    @Override public void onCreate() {
        super.onCreate();
        App.with(getApplicationContext()).inject(this);
    }

    @Override protected void onHandleIntent(Intent intent) {
        undoUncommittedDeletes();
        updateCurrenciesIfNecessary();

        if (user.isPremium()) {
            api.sync();

            if (!gcmRegistration.isRegisteredWithServer()) {
                api.registerDevice();
            }
        }
    }

    private void undoUncommittedDeletes() {
        // This is necessary, because while DeleteFragment is visible, the app can terminate and we would need to handle
        // uncommitted deletes.
        // TODO Undo
    }

    private void updateCurrenciesIfNecessary() {
        if (!generalPrefs.isAutoUpdateCurrencies() || DateUtils.isToday(generalPrefs.getAutoUpdateCurrenciesTimestamp())) {
            return;
        }

        currenciesApi.updateExchangeRates();
        generalPrefs.setAutoUpdateCurrenciesTimestamp(System.currentTimeMillis());
    }
}

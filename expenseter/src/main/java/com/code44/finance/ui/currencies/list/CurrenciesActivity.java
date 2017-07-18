package com.mattleo.finance.ui.currencies.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattleo.finance.R;
import com.mattleo.finance.api.currencies.CurrenciesApi;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ActivityPresenter;
import com.mattleo.finance.utils.analytics.Analytics;
import com.mattleo.finance.utils.preferences.GeneralPrefs;

import javax.inject.Inject;

public class CurrenciesActivity extends BaseActivity {
    @Inject GeneralPrefs generalPrefs;
    @Inject CurrenciesApi currenciesApi;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    public static void start(Context context) {
        final Intent intent = makeIntentForActivity(context, CurrenciesActivity.class);
        CurrenciesActivityPresenter.addViewExtras(intent);
        startActivity(context, intent);
    }

    public static void startSelect(Activity activity, int requestCode) {
        final Intent intent = makeIntentForActivity(activity, CurrenciesActivity.class);
        CurrenciesActivityPresenter.addSelectExtras(intent);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_currencies);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CurrenciesActivityPresenter(getEventBus(), generalPrefs, currenciesApi, currenciesManager, amountFormatter);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CurrencyList;
    }
}

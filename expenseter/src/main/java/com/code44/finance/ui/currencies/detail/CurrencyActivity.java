package com.mattleo.finance.ui.currencies.detail;

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

import javax.inject.Inject;

public class CurrencyActivity extends BaseActivity {
    @Inject CurrenciesApi currenciesApi;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    public static void start(Context context, String currencyId) {
        final Intent intent = makeIntentForActivity(context, CurrencyActivity.class);
        CurrencyActivityPresenter.addExtras(intent, currencyId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_currency);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CurrencyActivityPresenter(getEventBus(), currenciesApi, currenciesManager, amountFormatter);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Currency;
    }
}

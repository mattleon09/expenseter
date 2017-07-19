package com.mattleo.finance.ui.accounts.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattleo.finance.R;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ActivityPresenter;
import com.mattleo.finance.utils.analytics.Analytics;

import javax.inject.Inject;

public class AccountEditActivity extends BaseActivity {
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    public static void start(Context context, String accountId) {
        final Intent intent = makeIntentForActivity(context, AccountEditActivity.class);
        AccountEditActivityPresenter.addExtras(intent, accountId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new AccountEditActivityPresenter(getEventBus(), currenciesManager, amountFormatter);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.AccountEdit;
    }
}

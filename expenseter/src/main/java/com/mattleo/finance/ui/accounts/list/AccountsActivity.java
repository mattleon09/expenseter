package com.mattleo.finance.ui.accounts.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattleo.finance.R;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseDrawerActivity;
import com.mattleo.finance.ui.common.navigation.NavigationScreen;
import com.mattleo.finance.ui.common.presenters.ActivityPresenter;
import com.mattleo.finance.ui.common.presenters.ModelsActivityPresenter;
import com.mattleo.finance.utils.analytics.Analytics;

import javax.inject.Inject;

public class AccountsActivity extends BaseDrawerActivity {
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    public static Intent makeViewIntent(Context context) {
        final Intent intent = makeIntentForActivity(context, AccountsActivity.class);
        AccountsActivityPresenter.addViewExtras(intent);
        return intent;
    }

    public static void startSelect(Activity activity, int requestCode) {
        final Intent intent = makeIntentForActivity(activity, AccountsActivity.class);
        AccountsActivityPresenter.addSelectExtras(intent);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        final ModelsActivityPresenter.Mode mode = (ModelsActivityPresenter.Mode) getIntent().getSerializableExtra(ModelsActivityPresenter.EXTRA_MODE);
        if (mode == ModelsActivityPresenter.Mode.View) {
            setShowDrawer(true);
            setShowDrawerToggle(true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_accounts);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new AccountsActivityPresenter(currenciesManager, amountFormatter);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Accounts;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.AccountList;
    }
}

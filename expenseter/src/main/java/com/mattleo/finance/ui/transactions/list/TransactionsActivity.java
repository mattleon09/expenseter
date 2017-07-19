package com.mattleo.finance.ui.transactions.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattleo.finance.R;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.ui.common.activities.BaseDrawerActivity;
import com.mattleo.finance.ui.common.navigation.NavigationScreen;
import com.mattleo.finance.ui.common.presenters.ActivityPresenter;
import com.mattleo.finance.utils.analytics.Analytics;
import com.mattleo.finance.utils.interval.CurrentInterval;

import javax.inject.Inject;

public class TransactionsActivity extends BaseDrawerActivity {
    @Inject AmountFormatter amountFormatter;
    @Inject CurrentInterval currentInterval;

    public static Intent makeViewIntent(Context context) {
        final Intent intent = makeIntentForActivity(context, TransactionsActivity.class);
        TransactionsActivityPresenter.addViewExtras(intent);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setShowDrawer(true);
        setShowDrawerToggle(true);
        super.onCreate(savedInstanceState);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_transactions);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new TransactionsActivityPresenter(getEventBus(), amountFormatter, currentInterval);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Transactions;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionList;
    }
}

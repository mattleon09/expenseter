package com.mattleo.finance.ui.transactions.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattleo.finance.R;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.qualifiers.Local;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ActivityPresenter;
import com.mattleo.finance.utils.analytics.Analytics;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class TransactionEditActivity extends BaseActivity {
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;
    @Inject @Local ExecutorService localExecutor;

    public static void start(Context context, String transactionId) {
        final Intent intent = makeIntentForActivity(context, TransactionEditActivity.class);
        TransactionEditActivityPresenter.addExtras(intent, transactionId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new TransactionEditActivityPresenter(getEventBus(), localExecutor, currenciesManager, amountFormatter);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionEdit;
    }
}

package com.mattleo.finance.ui.overview;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseDrawerActivity;
import com.mattleo.finance.ui.common.navigation.NavigationScreen;
import com.mattleo.finance.ui.reports.categories.CategoriesReportData;
import com.mattleo.finance.ui.reports.trends.DefaultTrendsChartPresenter;
import com.mattleo.finance.ui.reports.trends.TrendsChartPresenter;
import com.mattleo.finance.ui.reports.trends.TrendsChartView;
import com.mattleo.finance.ui.transactions.edit.TransactionEditActivity;
import com.mattleo.finance.utils.analytics.Analytics;
import com.mattleo.finance.utils.interval.CurrentInterval;
import com.mattleo.finance.views.AccountsView;
import com.mattleo.finance.views.FabImageButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class OverviewActivity extends BaseDrawerActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int LOADER_TRANSACTIONS = 1;
    private static final int LOADER_ACCOUNTS = 2;

    @Inject CurrentInterval currentInterval;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private OverviewGraphView overviewGraphView;
    private AccountsView accountsView;

    private TrendsChartPresenter trendsChartPresenter;

    public static Intent makeIntent(Context context) {
        return makeIntentForActivity(context, OverviewActivity.class);
    }

    public static void start(Context context) {
        final Intent intent = makeIntent(context);
        startActivity(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShowDrawer(true);
        setShowDrawerToggle(true);
        setContentView(R.layout.activity_overview);

        // Get views
        final FabImageButton newTransactionView = (FabImageButton) findViewById(R.id.newTransaction);
        final TrendsChartView trendsChartView = (TrendsChartView) findViewById(R.id.trendsChartView);
        overviewGraphView = (OverviewGraphView) findViewById(R.id.overviewGraphView);
        accountsView = (AccountsView) findViewById(R.id.accounts);

        // Setup
        trendsChartPresenter = new DefaultTrendsChartPresenter(trendsChartView, amountFormatter, currenciesManager);
        newTransactionView.setOnClickListener(this);
        overviewGraphView.setOnClickListener(this);
        accountsView.setOnClickListener(this);
        trendsChartView.setOnClickListener(this);

        // Loader
        getSupportLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
        getSupportLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Overview;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Overview;
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_TRANSACTIONS:
                return Tables.Transactions
                        .getQuery()
                        .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(currentInterval.getInterval().getStartMillis()), String.valueOf(currentInterval.getInterval().getEndMillis() - 1))
                        .clearSort()
                        .sortOrder(Tables.Transactions.DATE.getName())
                        .asCursorLoader(this, TransactionsProvider.uriTransactions());
            case LOADER_ACCOUNTS:
                return Tables.Accounts
                        .getQuery()
                        .selection(" and " + Tables.Accounts.INCLUDE_IN_TOTALS + "=?", "1")
                        .asCursorLoader(this, AccountsProvider.uriAccounts());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_TRANSACTIONS:
                onTransactionsLoaded(cursor);
                break;
            case LOADER_ACCOUNTS:
                onAccountsLoaded(cursor);
                break;
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newTransaction:
                TransactionEditActivity.start(this, null);
                break;
            case R.id.overviewGraphView:
                onNavigationItemSelected(NavigationScreen.Reports);
                break;
            case R.id.accounts:
                onNavigationItemSelected(NavigationScreen.Accounts);
                break;
            case R.id.trendsChartView:
                onNavigationItemSelected(NavigationScreen.Transactions);
                break;
        }
    }

    @Subscribe public void onCurrentIntervalChanged(CurrentInterval currentInterval) {
        getSupportLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
        getSupportActionBar().setTitle(currentInterval.getTitle());
    }

    private void onTransactionsLoaded(Cursor cursor) {
        final CategoriesReportData categoriesReportData = new CategoriesReportData(this, cursor, currenciesManager, TransactionType.Expense);
        overviewGraphView.setPieChartData(categoriesReportData.getPieChartData());
        overviewGraphView.setTotalExpense(categoriesReportData.getPieChartData().getTotalValue());

        trendsChartPresenter.setData(cursor, currentInterval);
    }

    private void onAccountsLoaded(Cursor cursor) {
        final List<Account> accounts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                accounts.add(Account.from(cursor));
            } while (cursor.moveToNext());
        }
        accountsView.setAccounts(accounts);
    }
}

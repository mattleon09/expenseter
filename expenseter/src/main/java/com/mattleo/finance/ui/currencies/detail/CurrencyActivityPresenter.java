package com.mattleo.finance.ui.currencies.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mattleo.finance.R;
import com.mattleo.finance.api.currencies.CurrenciesApi;
import com.mattleo.finance.api.currencies.UpdateExchangeRatesRequest;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.CurrencyFormat;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.data.providers.CurrenciesProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ModelActivityPresenter;
import com.mattleo.finance.ui.currencies.edit.CurrencyEditActivity;
import com.mattleo.finance.utils.EventBus;
import com.squareup.otto.Subscribe;

class CurrencyActivityPresenter extends ModelActivityPresenter<CurrencyFormat> implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int LOADER_ACCOUNTS = 1;

    private final CurrenciesApi currenciesApi;
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    private TextView codeTextView;
    private Button mainCurrencyButton;
    private TextView formatTextView;
    private TextView exchangeRateTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CurrencyAccountsAdapter adapter;

    protected CurrencyActivityPresenter(EventBus eventBus, CurrenciesApi currenciesApi, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(eventBus);
        this.currenciesApi = currenciesApi;
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        // Get views
        codeTextView = findView(activity, R.id.codeTextView);
        mainCurrencyButton = findView(activity, R.id.mainCurrencyButton);
        formatTextView = findView(activity, R.id.formatTextView);
        exchangeRateTextView = findView(activity, R.id.exchangeRateTextView);
        swipeRefreshLayout = findView(activity, R.id.swipeRefreshLayout);
        final ImageView refreshRateButton = findView(activity, R.id.refreshRateButton);
        final ListView listView = findView(activity, R.id.listView);

        // Setup
        refreshRateButton.setOnClickListener(this);
        mainCurrencyButton.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        adapter = new CurrencyAccountsAdapter(activity);
        listView.setAdapter(adapter);
    }

    @Override public void onResume(BaseActivity activity) {
        super.onResume(activity);
        getEventBus().register(this);
    }

    @Override public void onPause(BaseActivity activity) {
        super.onPause(activity);
        getEventBus().unregister(this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ACCOUNTS) {
            return Tables.Accounts.getQuery().asCursorLoader(getActivity(), AccountsProvider.uriAccounts());
        }
        return super.onCreateLoader(id, args);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ACCOUNTS) {
            adapter.swapCursor(data);
            return;
        }
        super.onLoadFinished(loader, data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_ACCOUNTS) {
            adapter.swapCursor(null);
            return;
        }
        super.onLoaderReset(loader);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.CurrencyFormats.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrency(modelId));
    }

    @Override protected CurrencyFormat getModelFrom(Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    @Override protected void onModelLoaded(CurrencyFormat currencyFormat) {
        adapter.setCurrency(currencyFormat);
        codeTextView.setText(currencyFormat.getCode());
        if (currenciesManager.isMainCurrency(currencyFormat.getCode())) {
            exchangeRateTextView.setText(R.string.main_currency);
            mainCurrencyButton.setVisibility(View.GONE);
        } else {
            exchangeRateTextView.setText(String.valueOf(currenciesManager.getExchangeRate(currencyFormat.getCode())));
            mainCurrencyButton.setVisibility(View.VISIBLE);
        }
        formatTextView.setText(amountFormatter.format(currencyFormat.getCode(), 100000));

        getActivity().supportInvalidateOptionsMenu();

        // Loader
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ACCOUNTS, null, this);
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        CurrencyEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.CurrencyFormats.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshRateButton:
                onRefresh();
                break;
            case R.id.mainCurrencyButton:
                setAsMainCurrency();
                break;
        }
    }

    @Override public void onRefresh() {
        currenciesApi.updateExchangeRates();
        setRefreshing(true);
    }

    @Subscribe public void onRefreshFinished(UpdateExchangeRatesRequest request) {
        setRefreshing(false);
    }

    private void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void setAsMainCurrency() {
        currenciesManager.setMainCurrencyCode(getStoredModel().getCode());
    }
}

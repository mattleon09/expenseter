package com.mattleo.finance.ui.currencies.list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.mattleo.finance.R;
import com.mattleo.finance.api.currencies.CurrenciesApi;
import com.mattleo.finance.api.currencies.UpdateExchangeRatesRequest;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.CurrencyFormat;
import com.mattleo.finance.data.providers.CurrenciesProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.adapters.ModelsAdapter;
import com.mattleo.finance.ui.common.presenters.ModelsActivityPresenter;
import com.mattleo.finance.ui.currencies.detail.CurrencyActivity;
import com.mattleo.finance.ui.currencies.edit.CurrencyEditActivity;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.preferences.GeneralPrefs;
import com.squareup.otto.Subscribe;

class CurrenciesActivityPresenter extends ModelsActivityPresenter<CurrencyFormat> implements CompoundButton.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener {
    private final EventBus eventBus;
    private final GeneralPrefs generalPrefs;
    private final CurrenciesApi currenciesApi;
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    private SwipeRefreshLayout swipeRefreshLayout;

    CurrenciesActivityPresenter(EventBus eventBus, GeneralPrefs generalPrefs, CurrenciesApi currenciesApi, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        this.eventBus = eventBus;
        this.generalPrefs = generalPrefs;
        this.currenciesApi = currenciesApi;
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        swipeRefreshLayout = findView(activity, R.id.swipeRefreshLayout);
        final View settingsContainerView = findView(activity, R.id.settingsContainerView);
        final SwitchCompat autoUpdateCurrenciesSwitch = findView(activity, R.id.autoUpdateCurrenciesSwitch);

        // Setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(activity.getResources().getIntArray(R.array.progress_bar_colors));
        autoUpdateCurrenciesSwitch.setChecked(generalPrefs.isAutoUpdateCurrencies());
        autoUpdateCurrenciesSwitch.setOnCheckedChangeListener(this);
        if (getMode() != Mode.View) {
            settingsContainerView.setVisibility(View.GONE);
        }
    }

    @Override public void onResume(BaseActivity activity) {
        super.onResume(activity);
        eventBus.register(this);
    }

    @Override public void onPause(BaseActivity activity) {
        super.onPause(activity);
        eventBus.unregister(this);
    }

    @Override public boolean onCreateOptionsMenu(BaseActivity activity, Menu menu) {
        super.onCreateOptionsMenu(activity, menu);
        activity.getMenuInflater().inflate(R.menu.currencies, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(BaseActivity activity, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_rates:
                onRefresh();
                return true;
        }
        return super.onOptionsItemSelected(activity, item);
    }

    @Override protected ModelsAdapter<CurrencyFormat> createAdapter(ModelsAdapter.OnModelClickListener<CurrencyFormat> defaultOnModelClickListener) {
        return new CurrenciesAdapter(defaultOnModelClickListener, currenciesManager, amountFormatter);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.CurrencyFormats.getQuery()
                .clearSort()
                .sortOrder("case when " + Tables.CurrencyFormats.CODE + "=\"" + currenciesManager.getMainCurrencyCode() + "\" then 0 else 1 end")
                .sortOrder(Tables.CurrencyFormats.CODE.getName())
                .asCursorLoader(context, CurrenciesProvider.uriCurrencies());
    }

    @Override protected void onModelClick(Context context, View view, CurrencyFormat model, Cursor cursor, int position) {
        CurrencyActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        CurrencyEditActivity.start(context, modelId);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        generalPrefs.setAutoUpdateCurrencies(isChecked);
        if (isChecked) {
            onRefresh();
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
}

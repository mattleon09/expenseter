package com.mattleo.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.mattleo.finance.data.db.Column;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.money.CurrenciesManager;

import java.util.Map;

import javax.inject.Inject;

public class ExchangeRatesProvider extends BaseModelProvider {
    @Inject CurrenciesManager currenciesManager;

    public static Uri uriExchangeRates() {
        return uriModels(ExchangeRatesProvider.class, Tables.ExchangeRates.TABLE_NAME);
    }

    @Override protected String getModelTable() {
        return Tables.ExchangeRates.TABLE_NAME;
    }

    @Override protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override protected Column getIdColumn() {
        return Tables.ExchangeRates.LOCAL_ID;
    }

    @Override protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, extras);
        currenciesManager.updateExchangeRates(getDatabase());
    }

    @Override protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, extras);
        currenciesManager.updateExchangeRates(getDatabase());
    }

    @Override protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        super.onAfterBulkInsertItems(uri, valuesArray, extras);
        currenciesManager.updateExchangeRates(getDatabase());
    }

    @Override protected Uri[] getOtherUrisToNotify() {
        return new Uri[]{AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions(), CurrenciesProvider.uriCurrencies()};
    }
}

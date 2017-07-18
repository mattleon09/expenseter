package com.mattleo.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.mattleo.finance.data.db.Column;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.money.AmountFormatter;

import java.util.Map;

import javax.inject.Inject;

public class CurrenciesProvider extends ModelProvider {
    @Inject AmountFormatter amountFormatter;

    public static Uri uriCurrencies() {
        return uriModels(CurrenciesProvider.class, Tables.CurrencyFormats.TABLE_NAME);
    }

    public static Uri uriCurrency(String currencyServerId) {
        return uriModel(CurrenciesProvider.class, Tables.CurrencyFormats.TABLE_NAME, currencyServerId);
    }

    @Override protected String getModelTable() {
        return Tables.CurrencyFormats.TABLE_NAME;
    }

    @Override protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override protected Column getIdColumn() {
        return Tables.CurrencyFormats.ID;
    }

    @Override protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, extras);
        amountFormatter.updateFormats(getDatabase());
    }

    @Override protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, extras);
        amountFormatter.updateFormats(getDatabase());
    }

    @Override protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        super.onAfterBulkInsertItems(uri, valuesArray, extras);
        amountFormatter.updateFormats(getDatabase());
    }

    @Override protected Uri[] getOtherUrisToNotify() {
        return new Uri[]{AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions()};
    }
}

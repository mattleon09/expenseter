package com.mattleo.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.ModelState;
import com.mattleo.finance.common.model.TransactionState;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.common.utils.Strings;
import com.mattleo.finance.data.DataStore;
import com.mattleo.finance.data.Query;
import com.mattleo.finance.data.db.Column;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.utils.IOUtils;

import java.util.List;
import java.util.Map;

public class AccountsProvider extends ModelProvider {
    private static final String EXTRA_BALANCE_DELTA = "balance_delta";

    public static Uri uriAccounts() {
        return uriModels(AccountsProvider.class, Tables.Accounts.TABLE_NAME);
    }

    public static Uri uriAccount(String accountServerId) {
        return uriModel(AccountsProvider.class, Tables.Accounts.TABLE_NAME, accountServerId);
    }

    @Override protected String getModelTable() {
        return Tables.Accounts.TABLE_NAME;
    }

    @Override protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override protected Column getIdColumn() {
        return Tables.Accounts.ID;
    }

    @Override protected void onBeforeInsertItem(Uri uri, ContentValues values, Map<String, Object> outExtras) {
        super.onBeforeInsertItem(uri, values, outExtras);

        final long currentBalance = getCurrentBalance(values);
        //noinspection ConstantConditions
        final long newBalance = values.getAsLong(Tables.Accounts.BALANCE.getName());
        outExtras.put(EXTRA_BALANCE_DELTA, newBalance - currentBalance);
        values.remove(Tables.Accounts.BALANCE.getName());
    }

    @Override protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, extras);

        final Account account = new Account();
        account.setId(values.getAsString(getIdColumn().getName()));

        long balanceDelta = (long) extras.get(EXTRA_BALANCE_DELTA);
        final Transaction transaction = createBalanceTransaction(account, balanceDelta);
        if (transaction != null) {
            DataStore.insert().model(transaction).into(getContext(), TransactionsProvider.uriTransactions());
        }
    }

    @Override protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, outExtras);
        putColumnToExtras(outExtras, getIdColumn(), selection, selectionArgs);
    }

    @Override protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, extras);

        final List<String> affectedIds = getColumnValues(extras);
        final ModelState modelState = getModelState(extras);
        if (affectedIds.size() > 0) {
            final Uri transactionsUri = uriForDeleteFromModelState(TransactionsProvider.uriTransactions(), modelState);

            Query query = Query.create().selectionInClause(Tables.Transactions.ACCOUNT_FROM_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());

            query = Query.create().selectionInClause(Tables.Transactions.ACCOUNT_TO_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
        }
    }

    private long getCurrentBalance(ContentValues values) {
        final String accountId = values.getAsString(Tables.Accounts.ID.getName());
        if (Strings.isEmpty(accountId)) {
            return 0;
        }

        final Cursor cursor = Query.create()
                .projection(Tables.Accounts.BALANCE.getName())
                .selection(Tables.Accounts.ID + "=?", String.valueOf(accountId))
                .from(getDatabase(), Tables.Accounts.TABLE_NAME)
                .execute();
        final long balance = cursor.moveToFirst() ? cursor.getLong(cursor.getColumnIndex(Tables.Accounts.BALANCE.getName())) : 0;
        IOUtils.closeQuietly(cursor);
        return balance;
    }

    private Transaction createBalanceTransaction(Account account, long balanceDelta) {
        Transaction transaction = null;

        if (balanceDelta > 0) {
            transaction = new Transaction();
            transaction.setAccountTo(account);
            transaction.setTransactionType(TransactionType.Income);
        } else if (balanceDelta < 0) {
            transaction = new Transaction();
            transaction.setAccountFrom(account);
            transaction.setTransactionType(TransactionType.Expense);
        }

        if (transaction != null) {
            transaction.setAmount(Math.abs(balanceDelta));
            transaction.setNote(getContext().getString(R.string.account_balance_update));
            transaction.setIncludeInReports(false);
            transaction.setTransactionState(TransactionState.Confirmed);
        }

        return transaction;
    }
}

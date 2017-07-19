package com.mattleo.finance.ui.accounts.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.mattleo.finance.common.model.TransactionState;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.AmountGrouper;
import com.mattleo.finance.money.AmountRetriever;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.reports.balance.BalanceChartPresenter;
import com.mattleo.finance.ui.reports.balance.BalanceChartView;
import com.mattleo.finance.utils.interval.BaseInterval;

import lecho.lib.hellocharts.model.Line;

class AccountBalanceChartPresenter extends BalanceChartPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ACCOUNT_BALANCE = 7231;

    private final CurrenciesManager currenciesManager;
    private final LoaderManager loaderManager;
    private BaseInterval baseInterval;
    private Account account;

    public AccountBalanceChartPresenter(BalanceChartView balanceChartView, AmountFormatter amountFormatter, CurrenciesManager currenciesManager, LoaderManager loaderManager) {
        super(balanceChartView, amountFormatter);
        this.currenciesManager = currenciesManager;
        this.loaderManager = loaderManager;
    }

    @Override protected AmountGrouper.AmountCalculator getTransactionValidator(Account account) {
        return new BalanceAmountCalculator(currenciesManager, account);
    }

    @Override protected void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line) {
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ACCOUNT_BALANCE) {
            return Tables.Transactions
                    .getQuery()
                    .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(baseInterval.getInterval().getStartMillis()), String.valueOf(baseInterval.getInterval().getEndMillis() - 1))
                    .selection(" and (" + Tables.Transactions.ACCOUNT_FROM_ID + "=? or " + Tables.Transactions.ACCOUNT_TO_ID + "=?)", account.getId(), account.getId())
                    .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                    .clearSort()
                    .sortOrder(Tables.Transactions.DATE.getName())
                    .asCursorLoader(getContext(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ACCOUNT_BALANCE) {
            setData(account, data, baseInterval);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void setAccountAndInterval(Account account, BaseInterval baseInterval) {
        this.account = account;
        this.baseInterval = baseInterval;
        loaderManager.restartLoader(LOADER_ACCOUNT_BALANCE, null, this);
    }

    private static class BalanceAmountCalculator implements AmountGrouper.AmountCalculator {
        private final CurrenciesManager currenciesManager;
        private final Account account;

        private BalanceAmountCalculator(CurrenciesManager currenciesManager, Account account) {
            this.currenciesManager = currenciesManager;
            this.account = account;
        }

        @Override public long getAmount(Transaction transaction) {
            switch (transaction.getTransactionType()) {
                case Expense:
                    return -AmountRetriever.getExpenseAmount(transaction, currenciesManager, account.getCurrencyCode());
                case Income:
                    return AmountRetriever.getIncomeAmount(transaction, currenciesManager, account.getCurrencyCode());
                case Transfer:
                    if (transaction.getAccountFrom().equals(account)) {
                        return -AmountRetriever.getExpenseAmount(transaction, currenciesManager, account.getCurrencyCode());
                    } else {
                        return AmountRetriever.getExpenseAmount(transaction, currenciesManager, account.getCurrencyCode());
                    }
                default:
                    throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
            }
        }
    }
}

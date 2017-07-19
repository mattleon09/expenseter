package com.mattleo.finance.ui.categories.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.mattleo.finance.common.model.TransactionState;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.AmountGrouper;
import com.mattleo.finance.money.AmountRetriever;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.reports.trends.TrendsChartPresenter;
import com.mattleo.finance.ui.reports.trends.TrendsChartView;
import com.mattleo.finance.utils.interval.BaseInterval;

import lecho.lib.hellocharts.model.Line;

class CategoryTrendsChartPresenter extends TrendsChartPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_CATEGORY_TRENDS = 712;

    private final LoaderManager loaderManager;
    private final AmountGrouper.AmountCalculator amountCalculator;
    private BaseInterval baseInterval;
    private Category category;

    public CategoryTrendsChartPresenter(TrendsChartView trendsChartView, CurrenciesManager currenciesManager, AmountFormatter amountFormatter, LoaderManager loaderManager, BaseInterval baseInterval) {
        super(trendsChartView, amountFormatter);
        this.loaderManager = loaderManager;
        amountCalculator = new CategoryAmountCalculator(currenciesManager);
        setData(null, baseInterval);
    }

    @Override protected AmountGrouper.AmountCalculator[] getTransactionValidators() {
        return new AmountGrouper.AmountCalculator[]{amountCalculator};
    }

    @Override protected void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line) {
        if (category != null) {
            line.setColor(category.getColor());
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CATEGORY_TRENDS) {
            return Tables.Transactions
                    .getQuery()
                    .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(baseInterval.getInterval().getStartMillis()), String.valueOf(baseInterval.getInterval().getEndMillis() - 1))
                    .selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", category != null ? category.getId() : "0")
                    .selection(" and " + Tables.Transactions.INCLUDE_IN_REPORTS + "=?", "1")
                    .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                    .clearSort()
                    .sortOrder(Tables.Transactions.DATE.getName())
                    .asCursorLoader(getContext(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CATEGORY_TRENDS) {
            setData(data, baseInterval);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void setCategoryAndInterval(Category category, BaseInterval baseInterval) {
        this.category = category;
        this.baseInterval = baseInterval;
        loaderManager.restartLoader(LOADER_CATEGORY_TRENDS, null, this);
    }

    private static class CategoryAmountCalculator implements AmountGrouper.AmountCalculator {
        private final CurrenciesManager currenciesManager;

        private CategoryAmountCalculator(CurrenciesManager currenciesManager) {
            this.currenciesManager = currenciesManager;
        }

        @Override public long getAmount(Transaction transaction) {
            return AmountRetriever.getAmount(transaction, currenciesManager, currenciesManager.getMainCurrencyCode());
        }
    }
}

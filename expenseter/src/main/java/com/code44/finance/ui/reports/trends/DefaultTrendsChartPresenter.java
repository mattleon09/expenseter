package com.mattleo.finance.ui.reports.trends;

import com.mattleo.finance.common.model.TransactionState;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.AmountGrouper;
import com.mattleo.finance.money.AmountRetriever;
import com.mattleo.finance.money.CurrenciesManager;

import lecho.lib.hellocharts.model.Line;

public class DefaultTrendsChartPresenter extends TrendsChartPresenter {
    private final CurrenciesManager currenciesManager;

    public DefaultTrendsChartPresenter(TrendsChartView trendsChartView, AmountFormatter amountFormatter, CurrenciesManager currenciesManager) {
        super(trendsChartView, amountFormatter);
        this.currenciesManager = currenciesManager;
    }

    @Override protected AmountGrouper.AmountCalculator[] getTransactionValidators() {
        return new AmountGrouper.AmountCalculator[]{new OverviewAmountCalculator(currenciesManager)};
    }

    @Override protected void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line) {
    }

    private static class OverviewAmountCalculator implements AmountGrouper.AmountCalculator {
        private final CurrenciesManager currenciesManager;

        private OverviewAmountCalculator(CurrenciesManager currenciesManager) {
            this.currenciesManager = currenciesManager;
        }

        @Override public long getAmount(Transaction transaction) {
            if (transaction.includeInReports() && transaction.getTransactionType() != TransactionType.Transfer && transaction.getTransactionState() == TransactionState.Confirmed) {
                return AmountRetriever.getExpenseAmount(transaction, currenciesManager, currenciesManager.getMainCurrencyCode());
            }

            return 0;
        }
    }
}

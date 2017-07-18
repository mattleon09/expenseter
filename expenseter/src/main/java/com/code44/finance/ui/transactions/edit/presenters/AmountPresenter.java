package com.mattleo.finance.ui.transactions.edit.presenters;

import android.view.View;
import android.widget.Button;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.Presenter;
import com.mattleo.finance.utils.ThemeUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class AmountPresenter extends Presenter {
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;
    private final Button amountButton;
    private final Button exchangeRateButton;
    private final Button amountToButton;

    private TransactionType transactionType;
    private Account accountFrom;
    private Account accountTo;
    private long amount = 0;
    private double exchangeRate = 1.0;

    public AmountPresenter(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;

        amountButton = findView(activity, R.id.amountButton);
        exchangeRateButton = findView(activity, R.id.exchangeRateButton);
        amountToButton = findView(activity, R.id.amountToButton);

        amountButton.setOnClickListener(clickListener);
        amountButton.setOnLongClickListener(longClickListener);
        exchangeRateButton.setOnClickListener(clickListener);
        exchangeRateButton.setOnLongClickListener(longClickListener);
        amountToButton.setOnClickListener(clickListener);
        amountButton.setOnLongClickListener(longClickListener);
    }

    public void showError() {
        amountButton.setTextColor(ThemeUtils.getColor(amountButton.getContext(), R.attr.colorError));
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        update();
    }

    public void setAccountFrom(Account account) {
        this.accountFrom = account;
        update();
    }

    public void setAccountTo(Account account) {
        this.accountTo = account;
        update();
    }

    public void setAmount(long amount) {
        this.amount = amount;
        update();
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
        update();
    }

    private void update() {
        if (amount > 0) {
            amountButton.setTextColor(ThemeUtils.getColor(amountButton.getContext(), android.R.attr.textColorPrimaryInverse));
        }

        switch (transactionType) {
            case Expense:
                exchangeRateButton.setVisibility(View.GONE);
                amountToButton.setVisibility(View.GONE);
                break;
            case Income:
                exchangeRateButton.setVisibility(View.GONE);
                amountToButton.setVisibility(View.GONE);
                break;
            case Transfer:
                final boolean bothAccountsSet = accountFrom != null && accountTo != null;
                final boolean differentCurrencies = bothAccountsSet && !accountFrom.getCurrencyCode().equals(accountTo.getCurrencyCode());
                if (bothAccountsSet && differentCurrencies) {
                    exchangeRateButton.setVisibility(View.VISIBLE);
                    amountToButton.setVisibility(View.VISIBLE);

                    // TODO This is also done in calculator. Do not duplicate.
                    final NumberFormat format = DecimalFormat.getInstance(Locale.ENGLISH);
                    format.setGroupingUsed(false);
                    format.setMaximumFractionDigits(20);
                    exchangeRateButton.setText(format.format(exchangeRate));
                    amountToButton.setText(amountFormatter.format(accountTo.getCurrencyCode(), Math.round(amount * exchangeRate)));
                } else {
                    exchangeRateButton.setVisibility(View.GONE);
                    amountToButton.setVisibility(View.GONE);
                }
                break;
        }
        amountButton.setText(amountFormatter.format(getAmountCurrency(), amount));
    }

    private String getAmountCurrency() {
        String currencyCode;
        switch (transactionType) {
            case Expense:
                currencyCode = accountFrom == null ? null : accountFrom.getCurrencyCode();
                break;
            case Income:
                currencyCode = accountTo == null ? null : accountTo.getCurrencyCode();
                break;
            case Transfer:
                currencyCode = accountFrom == null ? null : accountFrom.getCurrencyCode();
                break;
            default:
                throw new IllegalStateException("Category type " + transactionType + " is not supported.");
        }

        if (currencyCode == null) {
            // When account is not selected yet, we use main currency.
            currencyCode = currenciesManager.getMainCurrencyCode();
        }

        return currencyCode;
    }
}

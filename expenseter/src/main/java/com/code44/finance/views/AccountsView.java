package com.mattleo.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattleo.finance.App;
import com.mattleo.finance.R;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;

import java.util.List;

import javax.inject.Inject;

public class AccountsView extends LinearLayout {
    private static final int TOP_STATIC_VIEWS_COUNT = 1;
    private static final int BOTTOM_STATIC_VIEWS_COUNT = 1;

    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private View balanceContainerView;
    private TextView totalBalanceView;

    public AccountsView(Context context) {
        this(context, null);
    }

    public AccountsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            App.with(context).inject(this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();

        // Get views
        balanceContainerView = findViewById(R.id.balanceContainer);
        totalBalanceView = (TextView) findViewById(R.id.totalBalance);
    }

    public void setAccounts(List<Account> accounts) {
        addOrRemoveAccountViews(accounts);
        updateAccountViews(accounts);
    }

    private void addOrRemoveAccountViews(List<Account> accounts) {
        final int currentSize = getChildCount();
        final int newSize = TOP_STATIC_VIEWS_COUNT + BOTTOM_STATIC_VIEWS_COUNT + accounts.size();

        if (newSize > currentSize) {
            for (int i = 0, count = newSize - currentSize; i < count; i++) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.include_account, this, false);
                addView(view, TOP_STATIC_VIEWS_COUNT);
            }
        } else if (newSize < currentSize) {
            removeViews(TOP_STATIC_VIEWS_COUNT, currentSize - newSize);
        }
    }

    private void updateAccountViews(List<Account> accounts) {
        long totalBalance = 0;
        for (int i = TOP_STATIC_VIEWS_COUNT, size = getChildCount() - BOTTOM_STATIC_VIEWS_COUNT; i < size; i++) {
            final Account account = accounts.get(i - TOP_STATIC_VIEWS_COUNT);
            final View view = getChildAt(i);
            ((TextView) view.findViewById(R.id.titleTextView)).setText(account.getTitle());
            ((TextView) view.findViewById(R.id.balanceTextView)).setText(amountFormatter.format(account.getCurrencyCode(), account.getBalance()));
            totalBalance += account.getBalance() * currenciesManager.getExchangeRate(account.getCurrencyCode(), currenciesManager.getMainCurrencyCode());
        }

        if (accounts.size() > 1) {
            balanceContainerView.setVisibility(VISIBLE);
            totalBalanceView.setText(amountFormatter.format(totalBalance));
        } else {
            balanceContainerView.setVisibility(GONE);
        }
    }
}

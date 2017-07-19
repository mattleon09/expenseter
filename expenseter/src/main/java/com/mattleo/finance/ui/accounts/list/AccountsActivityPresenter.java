package com.mattleo.finance.ui.accounts.list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.mattleo.finance.R;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.accounts.detail.AccountActivity;
import com.mattleo.finance.ui.accounts.edit.AccountEditActivity;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.adapters.ModelsAdapter;
import com.mattleo.finance.ui.common.presenters.ModelsActivityPresenter;

class AccountsActivityPresenter extends ModelsActivityPresenter<Account> {
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    private TextView balanceTextView;

    public AccountsActivityPresenter(CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        final View balanceContainerView = findView(activity, R.id.balanceContainerView);
        balanceTextView = findView(activity, R.id.balanceTextView);

        if (getMode() != Mode.View) {
            balanceContainerView.setVisibility(View.GONE);
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            updateBalance(data);
        }
        super.onLoadFinished(loader, data);
    }

    @Override protected ModelsAdapter<Account> createAdapter(ModelsAdapter.OnModelClickListener<Account> defaultOnModelClickListener) {
        return new AccountsAdapter(defaultOnModelClickListener, currenciesManager, amountFormatter);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccounts());
    }

    @Override protected void onModelClick(Context context, View view, Account model, Cursor cursor, int position) {
        AccountActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        AccountEditActivity.start(context, modelId);
    }

    private void updateBalance(Cursor cursor) {
        long balance = 0;
        if (cursor.moveToFirst()) {
            do {
                final Account account = Account.from(cursor);
                if (account.includeInTotals()) {
                    balance += account.getBalance() * currenciesManager.getExchangeRate(account.getCurrencyCode(), currenciesManager.getMainCurrencyCode());
                }
            } while (cursor.moveToNext());
        }
        balanceTextView.setText(amountFormatter.format(balance));
    }
}

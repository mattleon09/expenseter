package com.mattleo.finance.ui.transactions.edit.presenters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.Presenter;
import com.mattleo.finance.ui.transactions.edit.autocomplete.AutoCompleteAdapter;
import com.mattleo.finance.ui.transactions.edit.autocomplete.adapters.AutoCompleteAccountsFromAdapter;
import com.mattleo.finance.ui.transactions.edit.autocomplete.adapters.AutoCompleteAccountsToAdapter;
import com.mattleo.finance.utils.ThemeUtils;

public class AccountsPresenter extends Presenter implements AutoCompletePresenter<Account>, AutoCompleteAdapter.AutoCompleteAdapterListener {
    private final Button accountFromButton;
    private final Button accountToButton;
    private final View accountsDividerView;
    private final ViewGroup accountsAutoCompleteContainerView;

    public AccountsPresenter(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        accountFromButton = findView(activity, R.id.accountFromButton);
        accountToButton = findView(activity, R.id.accountToButton);
        accountsDividerView = findView(activity, R.id.accountsDividerView);
        accountsAutoCompleteContainerView = findView(activity, R.id.accountsAutoCompleteContainerView);

        accountFromButton.setOnClickListener(clickListener);
        accountFromButton.setOnLongClickListener(longClickListener);
        accountToButton.setOnClickListener(clickListener);
        accountToButton.setOnLongClickListener(longClickListener);
    }

    public void showError() {
        accountFromButton.setHintTextColor(ThemeUtils.getColor(accountFromButton.getContext(), R.attr.colorError));
        accountToButton.setHintTextColor(ThemeUtils.getColor(accountToButton.getContext(), R.attr.colorError));
    }

    @Override public void onAutoCompleteAdapterShown(AutoCompleteAdapter autoCompleteAdapter) {
        if (autoCompleteAdapter instanceof AutoCompleteAccountsFromAdapter) {
            accountFromButton.setHint(R.string.show_all);
        } else {
            accountToButton.setHint(R.string.show_all);
        }
        accountsDividerView.setVisibility(View.GONE);
    }

    @Override public void onAutoCompleteAdapterHidden(AutoCompleteAdapter autoCompleteAdapter) {
        if (autoCompleteAdapter instanceof AutoCompleteAccountsFromAdapter) {
            accountFromButton.setHint(R.string.from);
        } else {
            accountToButton.setHint(R.string.to);
        }
        accountsDividerView.setVisibility(View.VISIBLE);
    }

    @Override public AutoCompleteAdapter<Account> showAutoComplete(AutoCompleteAdapter<?> currentAdapter, TransactionEditData transactionEditData, AutoCompleteAdapter.OnAutoCompleteItemClickListener<Account> clickListener, View view) {
        final AutoCompleteAdapter<Account> adapter = view.getId() == R.id.accountFromButton ? new AutoCompleteAccountsFromAdapter(accountsAutoCompleteContainerView, this, clickListener) : new AutoCompleteAccountsToAdapter(accountsAutoCompleteContainerView, this, clickListener);
        if (adapter.show(currentAdapter, transactionEditData)) {
            return adapter;
        }
        return null;
    }

    public void setTransactionType(TransactionType transactionType) {
        switch (transactionType) {
            case Expense:
                accountFromButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.GONE);
                break;
            case Income:
                accountFromButton.setVisibility(View.GONE);
                accountToButton.setVisibility(View.VISIBLE);
                break;
            case Transfer:
                accountFromButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setAccountFrom(Account account) {
        accountFromButton.setText(account == null ? null : account.getTitle());
    }

    public void setAccountTo(Account account) {
        accountToButton.setText(account == null ? null : account.getTitle());
    }
}

package com.mattleo.finance.ui.transactions.edit.presenters;

import android.view.View;
import android.widget.ImageView;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.Presenter;
import com.mattleo.finance.utils.ThemeUtils;

public class TransactionTypePresenter extends Presenter {
    private final ImageView transactionTypeImageView;

    public TransactionTypePresenter(BaseActivity activity, View.OnClickListener clickListener) {
        final View transactionTypeContainerView = findView(activity, R.id.transactionTypeContainerView);
        transactionTypeImageView = findView(activity, R.id.transactionTypeImageView);

        transactionTypeContainerView.setOnClickListener(clickListener);
    }

    public void setTransactionType(TransactionType transactionType) {
        final int color;
        switch (transactionType) {
            case Expense:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorNegative);
                break;
            case Income:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorPositive);
                break;
            case Transfer:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorNeutral);
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
        }
        transactionTypeImageView.setColorFilter(color);
    }
}

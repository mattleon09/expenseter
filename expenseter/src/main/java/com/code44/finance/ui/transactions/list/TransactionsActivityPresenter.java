package com.mattleo.finance.ui.transactions.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mattleo.finance.R;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.adapters.ModelsAdapter;
import com.mattleo.finance.ui.common.presenters.ModelsActivityPresenter;
import com.mattleo.finance.ui.common.recycler.DividerDecoration;
import com.mattleo.finance.ui.common.recycler.SectionsDecoration;
import com.mattleo.finance.ui.transactions.detail.TransactionActivity;
import com.mattleo.finance.ui.transactions.edit.TransactionEditActivity;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.interval.CurrentInterval;
import com.squareup.otto.Subscribe;

class TransactionsActivityPresenter extends ModelsActivityPresenter<Transaction> {
    private final EventBus eventBus;
    private final AmountFormatter amountFormatter;
    private final CurrentInterval currentInterval;

    public TransactionsActivityPresenter(EventBus eventBus, AmountFormatter amountFormatter, CurrentInterval currentInterval) {
        this.eventBus = eventBus;
        this.amountFormatter = amountFormatter;
        this.currentInterval = currentInterval;
    }

    @Override public void onResume(BaseActivity activity) {
        super.onResume(activity);
        eventBus.register(this);
    }

    @Override public void onPause(BaseActivity activity) {
        super.onPause(activity);
        eventBus.unregister(this);
    }

    @Override protected ModelsAdapter<Transaction> createAdapter(ModelsAdapter.OnModelClickListener<Transaction> defaultOnModelClickListener) {
        return new TransactionsAdapter(defaultOnModelClickListener, amountFormatter, currentInterval);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Transactions.getQuery().asCursorLoader(context, TransactionsProvider.uriTransactions());
    }

    @Override protected void onModelClick(Context context, View view, Transaction model, Cursor cursor, int position) {
        TransactionActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        TransactionEditActivity.start(context, modelId);
    }

    @Subscribe public void onIntervalChanged(CurrentInterval interval) {
        getAdapter().notifyDataSetChanged();
    }

    @Override protected RecyclerView.ItemDecoration[] getItemDecorations() {
        final Context context = getActivity();
        final RecyclerView.ItemDecoration dividerDecoration = new DividerDecoration(context).setPaddingLeft(context.getResources().getDimensionPixelSize(R.dimen.keyline_content));
        final RecyclerView.ItemDecoration sectionDecoration = new SectionsDecoration(true);
        return new RecyclerView.ItemDecoration[]{dividerDecoration, sectionDecoration};
    }
}

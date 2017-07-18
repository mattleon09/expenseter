package com.mattleo.finance.ui.transactions.edit.autocomplete.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattleo.finance.R;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.ui.transactions.edit.autocomplete.AutoCompleteAdapter;
import com.mattleo.finance.ui.transactions.edit.autocomplete.AutoCompleteResult;
import com.mattleo.finance.ui.transactions.edit.presenters.TransactionEditData;

import java.util.List;

public class AutoCompleteTagsAdapter extends AutoCompleteAdapter<List<Tag>> {
    public AutoCompleteTagsAdapter(ViewGroup containerView, AutoCompleteAdapterListener listener, OnAutoCompleteItemClickListener<List<Tag>> clickListener) {
        super(containerView, listener, clickListener);
    }

    @Override protected View newView(Context context, ViewGroup containerView) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_tag, containerView, false);
        final int keylineContent = context.getResources().getDimensionPixelSize(R.dimen.keyline_content);
        view.setPadding(keylineContent, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        return view;
    }

    @Override protected void bindView(View view, List<Tag> tags) {
        final StringBuilder sb = new StringBuilder();
        for (Tag tag : tags) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(tag.getTitle());
        }

        ((TextView) view.findViewById(R.id.titleTextView)).setText(sb.toString());
    }

    @Override protected boolean isSameAdapter(AutoCompleteAdapter<?> currentAdapter) {
        return currentAdapter instanceof AutoCompleteTagsAdapter;
    }

    @Override protected boolean showItem(TransactionEditData transactionEditData, List<Tag> item) {
        return item != null && !item.equals(transactionEditData.getTags());
    }

    @Override protected List<List<Tag>> getItems(AutoCompleteResult autoCompleteResult) {
        return autoCompleteResult.getTags();
    }
}

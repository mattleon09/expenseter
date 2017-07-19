package com.mattleo.finance.ui.transactions.edit.autocomplete;

import com.mattleo.finance.data.model.Transaction;

public interface FinderScore {
    public void add(Transaction transaction);

    public float getScore();
}

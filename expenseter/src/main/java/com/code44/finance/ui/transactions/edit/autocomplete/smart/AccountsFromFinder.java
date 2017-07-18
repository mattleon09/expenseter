package com.mattleo.finance.ui.transactions.edit.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;

import com.mattleo.finance.common.utils.Strings;
import com.mattleo.finance.data.Query;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.ui.transactions.edit.autocomplete.AutoCompleteInput;
import com.mattleo.finance.ui.transactions.edit.autocomplete.Finder;
import com.mattleo.finance.ui.transactions.edit.autocomplete.FinderScore;

import java.util.ArrayList;
import java.util.List;

public class AccountsFromFinder extends Finder<Account> {
    private final Category category;

    protected AccountsFromFinder(Context context, AutoCompleteInput autoCompleteInput, boolean log, Category category) {
        super(context, autoCompleteInput, log);
        this.category = category;
    }

    @Override protected Cursor queryTransactions(AutoCompleteInput input) {
        final Query query = getBaseQuery();

        if (!Strings.isEmpty(input.getNote())) {
            query.selection(" and " + Tables.Transactions.NOTE + "=?", input.getNote());
        }

        if (input.getCategory() != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", input.getCategory().getId());
        } else if (category != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", category.getId());
        }

        if (input.getTags() != null && input.getTags().size() > 0) {
            final List<String> tagIds = new ArrayList<>();
            for (Tag tag : input.getTags()) {
                tagIds.add(tag.getId());
            }
            query.selection(" and ").selectionInClause(Tables.TransactionTags.TAG_ID.getName(), tagIds);
        }

        if (input.getAccountTo() != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_TO_ID + "=?", input.getAccountTo().getId());
        }

        return executeQuery(query);
    }

    @Override protected FinderScore createScore(AutoCompleteInput autoCompleteInput) {
        return new Score(autoCompleteInput.getDate(), autoCompleteInput.getAmount());
    }

    @Override protected boolean isValidTransaction(Transaction transaction) {
        return transaction.getAccountFrom() != null;
    }

    @Override protected Account getModelForTransaction(Transaction transaction) {
        return transaction.getAccountFrom();
    }

    @Override protected String getLogName(Account model) {
        return model.getTitle();
    }
}

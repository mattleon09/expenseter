package com.mattleo.finance.ui.transactions.edit.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;

import com.mattleo.finance.common.utils.Strings;
import com.mattleo.finance.data.Query;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.ui.transactions.edit.autocomplete.AutoCompleteInput;
import com.mattleo.finance.ui.transactions.edit.autocomplete.Finder;
import com.mattleo.finance.ui.transactions.edit.autocomplete.FinderScore;

import java.util.ArrayList;
import java.util.List;

public class NotesFinder extends Finder<String> {
    private final Category category;
    private final List<Tag> tags;

    protected NotesFinder(Context context, AutoCompleteInput autoCompleteInput, boolean log, Category category, List<Tag> tags) {
        super(context, autoCompleteInput, log);
        this.category = category;
        this.tags = tags;
    }

    @Override protected Cursor queryTransactions(AutoCompleteInput input) {
        final Query query = getBaseQuery();

        if (input.getCategory() != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", input.getCategory().getId());
        } else if (category != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", category.getId());
        }

        if ((input.getTags() != null && input.getTags().size() > 0) || (tags != null && tags.size() > 0)) {
            final List<String> tagIds = new ArrayList<>();
            for (Tag tag : input.getTags() != null ? input.getTags() : tags) {
                tagIds.add(tag.getId());
            }
            query.selection(" and ").selectionInClause(Tables.TransactionTags.TAG_ID.getName(), tagIds);
        }

        if (input.getAccountFrom() != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_FROM_ID + "=?", input.getAccountFrom().getId());
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
        return !Strings.isEmpty(transaction.getNote());
    }

    @Override protected String getModelForTransaction(Transaction transaction) {
        return transaction.getNote();
    }

    @Override protected String getLogName(String model) {
        return model;
    }
}

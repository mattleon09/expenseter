package com.mattleo.finance.api.requests;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mattleo.finance.api.User;
import com.mattleo.finance.backend.endpoint.transactions.Transactions;
import com.mattleo.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.Query;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.Model;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.data.providers.CategoriesProvider;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.utils.IOUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTransactionsRequest extends GetRequest<TransactionEntity> {
    private final Transactions transactionsService;
    private final Map<String, Account> accounts;
    private final Map<String, Category> categories;

    public GetTransactionsRequest(Context context, User user, Transactions transactions) {
        super(null, context, user);
        Preconditions.notNull(transactions, "Transactions cannot be null.");

        this.transactionsService = transactions;
        accounts = new HashMap<>();
        categories = new HashMap<>();
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getTransactionsTimestamp();
    }

    @Override protected List<TransactionEntity> performRequest(long timestamp) throws Exception {
        return transactionsService.list(timestamp).execute().getItems();
    }

    @Override protected Model getModelFrom(TransactionEntity entity) {
        return null;
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setTransactionsTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return TransactionsProvider.uriTransactions();
    }

    private Account getAccountFor(String id) {
        Account account = accounts.get(id);

        if (account == null) {
            final Cursor cursor = Query.create()
                    .projectionLocalId(Tables.Accounts.LOCAL_ID)
                    .projection(Tables.Accounts.PROJECTION)
                    .projection(Tables.CurrencyFormats.PROJECTION)
                    .selection(Tables.Accounts.ID + "=?", id)
                    .from(getContext(), AccountsProvider.uriAccounts())
                    .execute();
            account = Account.from(cursor);
            IOUtils.closeQuietly(cursor);
            accounts.put(id, account);
        }

        return account;
    }

    private Category getCategoryFor(TransactionEntity entity) {
        Category category = categories.get(entity.getId());

        if (category == null) {
            final Cursor cursor = Query.create()
                    .projectionLocalId(Tables.Categories.LOCAL_ID)
                    .projection(Tables.Categories.PROJECTION)
                    .selection(Tables.Accounts.ID + "=?", entity.getId())
                    .from(getContext(), CategoriesProvider.uriCategories())
                    .execute();
            category = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
            categories.put(entity.getId(), category);
        }

        return category;
    }
}

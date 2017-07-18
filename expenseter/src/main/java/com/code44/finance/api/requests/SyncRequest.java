package com.mattleo.finance.api.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mattleo.finance.api.GcmRegistration;
import com.mattleo.finance.api.Request;
import com.mattleo.finance.api.User;
import com.mattleo.finance.backend.endpoint.accounts.Accounts;
import com.mattleo.finance.backend.endpoint.categories.Categories;
import com.mattleo.finance.backend.endpoint.currencies.Currencies;
import com.mattleo.finance.backend.endpoint.tags.Tags;
import com.mattleo.finance.backend.endpoint.transactions.Transactions;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.DataStore;
import com.mattleo.finance.data.Query;
import com.mattleo.finance.data.db.Column;
import com.mattleo.finance.data.db.DBHelper;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.CurrencyFormat;
import com.mattleo.finance.data.model.SyncState;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.data.providers.CategoriesProvider;
import com.mattleo.finance.data.providers.CurrenciesProvider;
import com.mattleo.finance.data.providers.TagsProvider;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;

public class SyncRequest extends Request {
    private final Context context;
    private final DBHelper dbHelper;
    private final User user;
    private final GcmRegistration gcmRegistration;
    private final Currencies currenciesService;
    private final Categories categoriesService;
    private final Tags tagsService;
    private final Accounts accountsService;
    private final Transactions transactionsService;

    public SyncRequest(EventBus eventBus, Context context, DBHelper dbHelper, User user, GcmRegistration gcmRegistration, Currencies currenciesService, Categories categoriesService, Tags tagsService, Accounts accountsService, Transactions transactionsService) {
        super(eventBus);
        Preconditions.notNull(eventBus, "EventBus cannot be null.");
        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(dbHelper, "DBHelper cannot be null.");
        Preconditions.notNull(user, "User cannot be null.");
        Preconditions.notNull(gcmRegistration, "Gcm registration cannot be null.");
        Preconditions.notNull(currenciesService, "Currencies service cannot be null.");
        Preconditions.notNull(categoriesService, "Categories service cannot be null.");
        Preconditions.notNull(tagsService, "Tags service cannot be null.");
        Preconditions.notNull(accountsService, "Accounts service cannot be null.");
        Preconditions.notNull(transactionsService, "Transactions service cannot be null.");

        this.context = context;
        this.dbHelper = dbHelper;
        this.user = user;
        this.gcmRegistration = gcmRegistration;
        this.currenciesService = currenciesService;
        this.categoriesService = categoriesService;
        this.tagsService = tagsService;
        this.accountsService = accountsService;
        this.transactionsService = transactionsService;
    }

    @Override protected Object performRequest() throws Exception {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        pushCurrencies(database);
        getCurrencies();

        pushCategories(database);
        getCategories();

        pushTags(database);
        getTags();

        pushAccounts(database);
        getAccounts();

        pushTransactions(database);
        getTransactions();

        return null;
    }

    private void pushCurrencies(SQLiteDatabase database) {
        markInProgress(database, Tables.CurrencyFormats.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.CurrencyFormats.LOCAL_ID)
                .projection(Tables.CurrencyFormats.PROJECTION)
                .selection(Tables.CurrencyFormats.SYNC_STATE + "=?", SyncState.InProgress.asString())
                .from(context, CurrenciesProvider.uriCurrencies())
                .execute();
        final List<CurrencyFormat> currencies = new ArrayList<>();
        do {
            currencies.add(CurrencyFormat.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

//        new PostCurrenciesRequest(gcmRegistration, currenciesService, currencies).run();
    }

    private void getCurrencies() {
//        new GetCurrenciesRequest(context, user, currenciesService).run();
    }

    private void pushCategories(SQLiteDatabase database) {
        markInProgress(database, Tables.Categories.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Categories.LOCAL_ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.SYNC_STATE + "=?", SyncState.InProgress.asString())
                .from(context, CategoriesProvider.uriCategories())
                .execute();
        final List<Category> categories = new ArrayList<>();
        do {
            categories.add(Category.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

//        new PostCategoriesRequest(gcmRegistration, categoriesService, categories).run();
    }

    private void getCategories() {
//        new GetCategoriesRequest(context, user, categoriesService).call();
    }

    private void pushTags(SQLiteDatabase database) {
        markInProgress(database, Tables.Tags.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Tags.LOCAL_ID)
                .projection(Tables.Tags.PROJECTION)
                .selection(Tables.Tags.SYNC_STATE + "=?", SyncState.InProgress.asString())
                .from(context, TagsProvider.uriTags())
                .execute();
        final List<Tag> tags = new ArrayList<>();
        do {
            tags.add(Tag.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

//        new PostTagsRequest(gcmRegistration, tagsService, tags).run();
    }

    private void getTags() {
//        new GetTagsRequest(context, user, tagsService).run();
    }

    private void pushAccounts(SQLiteDatabase database) {
        markInProgress(database, Tables.Accounts.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Accounts.LOCAL_ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.CurrencyFormats.PROJECTION)
                .selection(Tables.Accounts.SYNC_STATE + "=?", SyncState.InProgress.asString())
                .from(context, AccountsProvider.uriAccounts())
                .execute();
        final List<Account> accounts = new ArrayList<>();
        do {
            accounts.add(Account.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

//        new PostAccountsRequest(gcmRegistration, accountsService, accounts).run();
    }

    private void getAccounts() {
//        new GetAccountsRequest(context, user, accountsService).run();
    }

    private void pushTransactions(SQLiteDatabase database) {
        markInProgress(database, Tables.Transactions.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Transactions.LOCAL_ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_FROM)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_TO)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Transactions.SYNC_STATE + "=?", SyncState.InProgress.asString())
                .from(context, TransactionsProvider.uriTransactions())
                .execute();
        final List<Transaction> transactions = new ArrayList<>();
        do {
            transactions.add(Transaction.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

//        new PostTransactionsRequest(gcmRegistration, transactionsService, transactions).run();
    }

    private void getTransactions() {
//        new GetTransactionsRequest(context, user, transactionsService).run();
    }

    private void markInProgress(SQLiteDatabase database, Column syncStateColumn) {
        final ContentValues values = new ContentValues();
        values.put(syncStateColumn.getName(), SyncState.InProgress.asInt());
        DataStore.update()
                .values(values)
                .withSelection(syncStateColumn.getName() + "<>?", SyncState.Synced.asString())
                .into(database, syncStateColumn.getTableName());
    }
}

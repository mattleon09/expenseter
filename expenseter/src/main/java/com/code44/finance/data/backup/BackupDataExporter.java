package com.mattleo.finance.data.backup;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mattleo.finance.data.Query;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.CurrencyFormat;
import com.mattleo.finance.data.model.Model;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.data.providers.CategoriesProvider;
import com.mattleo.finance.data.providers.CurrenciesProvider;
import com.mattleo.finance.data.providers.TagsProvider;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.utils.IOUtils;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class BackupDataExporter extends DataExporter {
    public static final int VERSION = 9;

    private static final String CHARSET_NAME = "UTF-8";

    private final Context context;

    public BackupDataExporter(OutputStream outputStream, Context context) {
        super(outputStream);
        this.context = context;
    }

    @Override public void exportData(OutputStream outputStream) throws Exception {
        final JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, CHARSET_NAME));
        writer.setIndent("  ");

        writer.beginObject();

        writeMetaData(writer);

        writer.name("currencies").beginArray();
        writeCurrencies(writer);
        writer.endArray();

        writer.name("categories").beginArray();
        writeCategories(writer);
        writer.endArray();

        writer.name("tags").beginArray();
        writeTags(writer);
        writer.endArray();

        writer.name("accounts").beginArray();
        writeAccounts(writer);
        writer.endArray();

        writer.name("transactions").beginArray();
        writeTransactions(writer);
        writer.endArray();

        writer.endObject();
        writer.close();
    }

    private void writeMetaData(JsonWriter writer) throws IOException {
        writer.name("version").value(VERSION);
        writer.name("timestamp").value(System.currentTimeMillis());
    }

    private void writeCurrencies(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(CurrenciesProvider.uriCurrencies(), Tables.CurrencyFormats.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final CurrencyFormat currencyFormat = new CurrencyFormat();
                do {
                    currencyFormat.updateFromCursor(cursor, null);

                    writer.beginObject();
                    writeBaseModel(currencyFormat, writer);
                    writer.name("code").value(currencyFormat.getCode());
                    writer.name("symbol").value(currencyFormat.getSymbol());
                    writer.name("symbol_position").value(currencyFormat.getSymbolPosition().asInt());
                    writer.name("decimal_separator").value(currencyFormat.getDecimalSeparator().symbol());
                    writer.name("group_separator").value(currencyFormat.getGroupSeparator().symbol());
                    writer.name("decimal_count").value(currencyFormat.getDecimalCount());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeCategories(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(CategoriesProvider.uriCategories(), Tables.Categories.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final Category category = new Category();
                do {
                    category.updateFromCursor(cursor, null);

                    writer.beginObject();
                    writeBaseModel(category, writer);
                    writer.name("title").value(category.getTitle());
                    writer.name("color").value(category.getColor());
                    writer.name("transaction_type").value(category.getTransactionType().asInt());
                    writer.name("sort_order").value(category.getSortOrder());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeTags(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(TagsProvider.uriTags(), Tables.Tags.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final Tag tag = new Tag();
                do {
                    tag.updateFromCursor(cursor, null);

                    writer.beginObject();
                    writeBaseModel(tag, writer);
                    writer.name("title").value(tag.getTitle());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeAccounts(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(AccountsProvider.uriAccounts(), Tables.Accounts.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final Account account = new Account();
                do {
                    account.updateFromCursor(cursor, null);

                    writer.beginObject();
                    writeBaseModel(account, writer);
                    writer.name("currency_code").value(account.getCurrencyCode());
                    writer.name("title").value(account.getTitle());
                    writer.name("note").value(account.getNote());
                    writer.name("balance").value(account.getBalance());
                    writer.name("include_in_totals").value(account.includeInTotals());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeTransactions(JsonWriter writer) throws IOException {
        final Cursor cursor = Tables.Transactions.getQuery().clearSelection().clearArgs().selection("1=1").from(context, TransactionsProvider.uriTransactions()).execute();
        try {
            if (cursor.moveToFirst()) {
                final Transaction transaction = new Transaction();
                do {
                    transaction.updateFromCursor(cursor, null);
                    transaction.prepareForContentValues();

                    writer.beginObject();
                    writeBaseModel(transaction, writer);
                    writer.name("account_from_id").value(transaction.getAccountFrom() != null ? transaction.getAccountFrom().getId() : null);
                    writer.name("account_to_id").value(transaction.getAccountTo() != null ? transaction.getAccountTo().getId() : null);
                    writer.name("category_id").value(transaction.getCategory() != null ? transaction.getCategory().getId() : null);
                    writer.name("tag_ids").beginArray();
                    for (Tag tag : transaction.getTags()) {
                        writer.value(tag.getId());
                    }
                    writer.endArray();
                    writer.name("date").value(transaction.getDate());
                    writer.name("amount").value(transaction.getAmount());
                    writer.name("exchange_rate").value(transaction.getExchangeRate());
                    writer.name("note").value(transaction.getNote());
                    writer.name("transaction_state").value(transaction.getTransactionState().asInt());
                    writer.name("transaction_type").value(transaction.getTransactionType().asInt());
                    writer.name("include_in_reports").value(transaction.includeInReports());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private Cursor getCursor(Uri uri, String... projection) {
        return Query.create().projection(projection).from(context, uri).execute();
    }

    private void writeBaseModel(Model model, JsonWriter writer) throws IOException {
        writer.name("id").value(model.getId());
        writer.name("model_state").value(model.getModelState().asInt());
        writer.name("sync_state").value(model.getSyncState().asInt());
    }
}

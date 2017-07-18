package com.mattleo.finance.data.backup;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mattleo.finance.common.model.DecimalSeparator;
import com.mattleo.finance.common.model.GroupSeparator;
import com.mattleo.finance.common.model.ModelState;
import com.mattleo.finance.common.model.SymbolPosition;
import com.mattleo.finance.common.model.TransactionState;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.data.DataStore;
import com.mattleo.finance.data.db.DBHelper;
import com.mattleo.finance.data.db.DBMigration;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Account;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.CurrencyFormat;
import com.mattleo.finance.data.model.Model;
import com.mattleo.finance.data.model.SyncState;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.model.Transaction;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.data.providers.CategoriesProvider;
import com.mattleo.finance.data.providers.CurrenciesProvider;
import com.mattleo.finance.data.providers.TagsProvider;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BackupDataImporter extends DataImporter {
    private static final int MIN_VALID_VERSION = 7;

    private final Context context;
    private final DBHelper dbHelper;
    private final boolean merge;

    public BackupDataImporter(InputStream inputStream, Context context, DBHelper dbHelper, boolean merge) {
        super(inputStream);
        this.context = context.getApplicationContext();
        this.dbHelper = dbHelper;
        this.merge = merge;
    }

    @Override protected void importData(InputStream inputStream) throws Exception {
        final JsonObject json = inputStreamToJson(inputStream);
        final int version = validate(json);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.beginTransaction();

            if (!merge) {
                cleanDatabase(database);
            }

            final Map<String, CurrencyFormat> currencyFormats = importCurrencies(json);
            importCategories(json);
            importTags(json);
            importAccounts(json, version, currencyFormats);
            importTransactions(json);

            if (version <= 7) {
                DBMigration.fixTransactionsWithNotExistingAccounts(database);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private JsonObject inputStreamToJson(InputStream inputStream) {
        final JsonParser parser = new JsonParser();
        final JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        jsonReader.setLenient(true);
        final JsonElement jsonElement = parser.parse(jsonReader);
        return jsonElement.getAsJsonObject();
    }

    private int validate(JsonObject json) {
        final int version = json.get("version").getAsInt();
        if (version < MIN_VALID_VERSION) {
            throw new IllegalArgumentException("Backup version " + version + " is not supported anymore.");
        }
        return version;
    }

    private void cleanDatabase(SQLiteDatabase database) {
        database.delete(Tables.CurrencyFormats.TABLE_NAME, null, null);
        database.delete(Tables.Categories.TABLE_NAME, null, null);
        database.delete(Tables.Tags.TABLE_NAME, null, null);
        database.delete(Tables.Accounts.TABLE_NAME, null, null);
        database.delete(Tables.Transactions.TABLE_NAME, null, null);
        database.delete(Tables.TransactionTags.TABLE_NAME, null, null);
    }

    private Map<String, CurrencyFormat> importCurrencies(JsonObject json) {
        final Map<String, CurrencyFormat> currencyFormats = new HashMap<>();
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("currencies");
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            final CurrencyFormat model = new CurrencyFormat();
            updateBaseModel(model, modelJson);
            model.setCode(modelJson.get("code").getAsString());
            model.setSymbol(modelJson.get("symbol").getAsString());
            model.setSymbolPosition(SymbolPosition.fromInt(modelJson.get("symbol_position").getAsInt()));
            model.setDecimalSeparator(DecimalSeparator.fromSymbol(modelJson.get("decimal_separator").getAsString()));
            model.setGroupSeparator(GroupSeparator.fromSymbol(modelJson.get("group_separator").getAsString()));
            model.setDecimalCount(modelJson.get("decimal_count").getAsInt());
            valuesList.add(model.asContentValues());
            currencyFormats.put(model.getId(), model);
        }
        insert(valuesList, CurrenciesProvider.uriCurrencies());
        return currencyFormats;
    }

    private void importCategories(JsonObject json) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("categories");
        final Category model = new Category();
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            model.setTitle(modelJson.get("title").getAsString());
            model.setColor(modelJson.get("color").getAsInt());
            model.setTransactionType(TransactionType.fromInt(modelJson.get("transaction_type").getAsInt()));
            model.setSortOrder(modelJson.get("sort_order").getAsInt());
            valuesList.add(model.asContentValues());
        }
        insert(valuesList, CategoriesProvider.uriCategories());
    }

    private void importTags(JsonObject json) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("tags");
        final Tag model = new Tag();
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            model.setTitle(modelJson.get("title").getAsString());
            valuesList.add(model.asContentValues());
        }
        insert(valuesList, TagsProvider.uriTags());
    }

    private void importAccounts(JsonObject json, int version, Map<String, CurrencyFormat> currencyFormats) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("accounts");
        final Account model = new Account();
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            if (version >= 9) {
                model.setCurrencyCode(modelJson.get("currency_code").getAsString());
            } else {
                final CurrencyFormat currencyFormat = currencyFormats.get(modelJson.get("currency_id").getAsString());
                model.setCurrencyCode(currencyFormat != null ? currencyFormat.getCode() : "USD");
            }
            model.setTitle(modelJson.get("title").getAsString());
            model.setNote(modelJson.get("note").getAsString());
            model.setIncludeInTotals(modelJson.get("include_in_totals").getAsBoolean());
            valuesList.add(model.asContentValues());
        }
        insert(valuesList, AccountsProvider.uriAccounts());
    }

    private void importTransactions(JsonObject json) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("transactions");
        final Transaction model = new Transaction();
        final Account accountFrom = new Account();
        final Account accountTo = new Account();
        final Category category = new Category();
        final List<Tag> tags = new ArrayList<>();
        final Set<Tag> tagCache = new HashSet<>();
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            model.setAccountFrom(accountFrom);
            model.setAccountTo(accountTo);
            model.setCategory(category);
            model.setTags(tags);
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            accountFrom.setId(modelJson.get("account_from_id").isJsonNull() ? null : modelJson.get("account_from_id").getAsString());
            accountTo.setId(modelJson.get("account_to_id").isJsonNull() ? null : modelJson.get("account_to_id").getAsString());
            category.setId(modelJson.get("category_id").isJsonNull() ? null : modelJson.get("category_id").getAsString());
            tagCache.addAll(tags);
            tags.clear();
            final JsonArray tagsJson = modelJson.get("tag_ids").getAsJsonArray();
            for (int tagI = 0, tagSize = tagsJson.size(); tagI < tagSize; tagI++) {
                final Tag tag = getTagInstance(tagCache);
                tag.setId(tagsJson.get(tagI).getAsString());
                tags.add(tag);
            }
            model.setDate(modelJson.get("date").getAsLong());
            model.setAmount(modelJson.get("amount").getAsLong());
            model.setExchangeRate(modelJson.get("exchange_rate").getAsDouble());
            model.setNote(modelJson.get("note").getAsString());
            model.setTransactionState(TransactionState.fromInt(modelJson.get("transaction_state").getAsInt()));
            model.setTransactionType(TransactionType.fromInt(modelJson.get("transaction_type").getAsInt()));
            model.setIncludeInReports(modelJson.get("include_in_reports").getAsBoolean());

            // This is a hack to solve data migration issue where account_to_id = 2
            if (model.getAccountTo() != null && model.getAccountTo().getId() != null && model.getAccountTo().getId().equals("2")) {
                model.setAccountTo(null);
                model.setTransactionState(TransactionState.Pending);
            }

            valuesList.add(model.asContentValues());
        }
        insert(valuesList, TransactionsProvider.uriTransactions());
    }

    private Tag getTagInstance(Set<Tag> tagCache) {
        if (tagCache.size() > 0) {
            final Iterator<Tag> iterator = tagCache.iterator();
            final Tag tag = iterator.next();
            iterator.remove();
            return tag;
        } else {
            return new Tag();
        }
    }

    private void updateBaseModel(Model model, JsonObject json) {
        model.setId(json.get("id").getAsString());
        model.setModelState(ModelState.fromInt(json.get("model_state").getAsInt()));
        model.setSyncState(SyncState.fromInt(json.get("sync_state").getAsInt()));
    }

    private void insert(List<ContentValues> valuesList, Uri uri) {
        if (valuesList.size() > 0) {
            DataStore.bulkInsert().values(valuesList).into(context, uri);
        }
    }
}

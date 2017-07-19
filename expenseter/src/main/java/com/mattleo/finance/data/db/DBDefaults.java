package com.mattleo.finance.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.model.CurrencyFormat;
import com.mattleo.finance.money.CurrenciesManager;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class DBDefaults {
    private final Context context;
    private final SQLiteDatabase database;
    private final CurrenciesManager currenciesManager;

    public DBDefaults(Context context, SQLiteDatabase database, CurrenciesManager currenciesManager) {
        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.database = Preconditions.notNull(database, "Database cannot be null.");
        this.currenciesManager = Preconditions.notNull(currenciesManager, "CurrenciesManager cannot be null.");
    }

    public void addDefaults() {
        addCurrencies();
        addCategories();
    }

    private void addCurrencies() {
        final Set<String> currencyCodes = new HashSet<>();
        currenciesManager.setMainCurrencyCode(getMainCurrencyCode());
        currencyCodes.add(currenciesManager.getMainCurrencyCode());

        // Popular currencies
        currencyCodes.add("USD");
        currencyCodes.add("EUR");
        currencyCodes.add("GBP");
        currencyCodes.add("CNY");
        currencyCodes.add("INR");
        currencyCodes.add("RUB");
        currencyCodes.add("JPY");

        // Create currencies
        for (String code : currencyCodes) {
            java.util.Currency javaCurrency = getCurrencyFromCode(code);
            if (javaCurrency != null) {
                CurrencyFormat currencyFormat = new CurrencyFormat();
                currencyFormat.setId(UUID.randomUUID().toString());
                currencyFormat.setCode(code);
                currencyFormat.setSymbol(javaCurrency.getSymbol());
                currencyFormat.setDecimalCount(javaCurrency.getDefaultFractionDigits());
                database.insert(Tables.CurrencyFormats.TABLE_NAME, null, currencyFormat.asContentValues());
            }
        }
    }

    private void addCategories() {
        insertCategories(context.getResources().getStringArray(R.array.expense_categories), context.getResources().getStringArray(R.array.expense_categories_colors), TransactionType.Expense);
        insertCategories(context.getResources().getStringArray(R.array.income_categories), context.getResources().getStringArray(R.array.income_categories_colors), TransactionType.Income);
    }

    private String getMainCurrencyCode() {
        String code = null;
        try {
            code = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        } catch (Exception ignored) {
        }

        if (TextUtils.isEmpty(code)) {
            code = "USD";
        }

        return code;
    }

    private java.util.Currency getCurrencyFromCode(String code) {
        try {
            return java.util.Currency.getInstance(code);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void insertCategories(String[] titles, String[] colors, TransactionType type) {
        int order = 0;
        for (String title : titles) {
            final Category category = new Category();
            category.setId(UUID.randomUUID().toString());
            category.setTransactionType(type);
            category.setTitle(title);
            category.setColor(Color.parseColor(colors[order % colors.length]));
            category.setSortOrder(order++);
            database.insert(Tables.Categories.TABLE_NAME, null, category.asContentValues());
        }
    }
}

package com.mattleo.finance.ui.currencies.edit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.DecimalSeparator;
import com.mattleo.finance.common.model.GroupSeparator;
import com.mattleo.finance.common.model.SymbolPosition;
import com.mattleo.finance.data.DataStore;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.CurrencyFormat;
import com.mattleo.finance.data.providers.CurrenciesProvider;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.mattleo.finance.utils.EventBus;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

class CurrencyEditActivityPresenter extends ModelEditActivityPresenter<CurrencyFormat> implements View.OnClickListener {
    private static final String STATE_CODE = "STATE_CODE";
    private static final String STATE_SYMBOL = "STATE_SYMBOL";
    private static final String STATE_SYMBOL_POSITION = "STATE_SYMBOL_POSITION";
    private static final String STATE_GROUP_SEPARATOR = "STATE_GROUP_SEPARATOR";
    private static final String STATE_DECIMAL_SEPARATOR = "STATE_DECIMAL_SEPARATOR";
    private static final String STATE_DECIMAL_COUNT = "STATE_DECIMAL_COUNT";

    private static final int LOADER_CURRENCIES = 1;

    private final Set<String> existingCurrencyCodes = new HashSet<>();
    private final CurrencyFormat formatCurrencyFormat = new CurrencyFormat();

    private TextView codeTextView;
    private AutoCompleteTextView codeEditTextView;
    private EditText symbolEditTextView;
    private TextView errorTextView;
    private TextView currencyFormatTextView;

    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;
    private GroupSeparator groupSeparator;
    private DecimalSeparator decimalSeparator;
    private Integer decimalCount;

    public CurrencyEditActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        // Get view
        codeTextView = findView(activity, R.id.codeTextView);
        codeEditTextView = findView(activity, R.id.codeEditTextView);
        symbolEditTextView = findView(activity, R.id.symbolEditTextView);
        errorTextView = findView(activity, R.id.errorTextView);
        currencyFormatTextView = findView(activity, R.id.currencyFormatTextView);
        final Button symbolPositionButton = findView(activity, R.id.symbolPositionButton);
        final Button groupSeparatorButton = findView(activity, R.id.groupSeparatorButton);
        final Button decimalSeparatorButton = findView(activity, R.id.decimalSeparatorButton);
        final Button decimalCountButton = findView(activity, R.id.decimalCountButton);

        // Setup
        symbolPositionButton.setOnClickListener(this);
        groupSeparatorButton.setOnClickListener(this);
        decimalSeparatorButton.setOnClickListener(this);
        decimalCountButton.setOnClickListener(this);
        codeEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code = codeEditTextView.getText().toString();
                updateFormat();
            }
        });
        symbolEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                symbol = symbolEditTextView.getText().toString();
                updateFormat();
            }
        });

        if (!isNewModel()) {
            codeTextView.setVisibility(View.VISIBLE);
            codeEditTextView.setVisibility(View.GONE);
        }

        // Restore state
        if (savedInstanceState != null) {
            code = savedInstanceState.getString(STATE_CODE);
            symbol = savedInstanceState.getString(STATE_SYMBOL);
            symbolPosition = (SymbolPosition) savedInstanceState.getSerializable(STATE_SYMBOL_POSITION);
            groupSeparator = (GroupSeparator) savedInstanceState.getSerializable(STATE_GROUP_SEPARATOR);
            decimalSeparator = (DecimalSeparator) savedInstanceState.getSerializable(STATE_DECIMAL_SEPARATOR);
            decimalCount = savedInstanceState.getInt(STATE_DECIMAL_COUNT, -1);
            if (decimalCount == -1) {
                decimalCount = null;
            }
            onDataChanged(getStoredModel());
        }

        if (isNewModel()) {
            prepareCurrenciesAutoComplete();
            activity.getSupportLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
        }
    }

    @Override public void onSaveInstanceState(BaseActivity activity, Bundle outState) {
        super.onSaveInstanceState(activity, outState);
        outState.putString(STATE_CODE, code);
        outState.putString(STATE_SYMBOL, symbol);
        outState.putSerializable(STATE_SYMBOL_POSITION, symbolPosition);
        outState.putSerializable(STATE_GROUP_SEPARATOR, groupSeparator);
        outState.putSerializable(STATE_DECIMAL_SEPARATOR, decimalSeparator);
        outState.putInt(STATE_DECIMAL_COUNT, decimalCount == null ? -1 : decimalCount);
    }

    @Override protected void onDataChanged(CurrencyFormat model) {
        codeTextView.setText(getCode());
        codeEditTextView.setText(getCode());
        symbolEditTextView.setText(getSymbol());
        updateFormat();
    }

    @Override protected boolean onSave() {
        boolean canSave = true;

        final String code = getCode();
        if (!TextUtils.isEmpty(code) && !checkForCurrencyDuplicate(code)) {
            canSave = false;
        }

        if (TextUtils.isEmpty(code) || code.length() != 3) {
            canSave = false;
            errorTextView.setText(R.string.l_please_enter_currency_code);
            errorTextView.setVisibility(View.VISIBLE);
        }

        if (canSave) {
            final CurrencyFormat currencyFormat = new CurrencyFormat();
            currencyFormat.setId(getId());
            currencyFormat.setCode(code);
            currencyFormat.setSymbol(getSymbol());
            currencyFormat.setSymbolPosition(getSymbolPosition());
            currencyFormat.setGroupSeparator(getGroupSeparator());
            currencyFormat.setDecimalSeparator(getDecimalSeparator());
            currencyFormat.setDecimalCount(getDecimalCount());
            DataStore.insert().values(currencyFormat.asContentValues()).into(getActivity(), CurrenciesProvider.uriCurrencies());
        }

        return canSave;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.CurrencyFormats.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrency(modelId));
    }

    @Override protected CurrencyFormat getModelFrom(Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CURRENCIES) {
            return Tables.CurrencyFormats.getQuery().asCursorLoader(getActivity(), CurrenciesProvider.uriCurrencies());
        }
        return super.onCreateLoader(id, args);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CURRENCIES) {
            existingCurrencyCodes.clear();
            if (data.moveToFirst()) {
                do {
                    existingCurrencyCodes.add(CurrencyFormat.from(data).getCode());
                } while (data.moveToNext());
            }
            return;
        }
        super.onLoadFinished(loader, data);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.symbolPositionButton:
                toggleSymbolPosition();
                break;
            case R.id.groupSeparatorButton:
                toggleGroupSeparator();
                break;
            case R.id.decimalSeparatorButton:
                toggleDecimalSeparator();
                break;
            case R.id.decimalCountButton:
                toggleDecimalCount();
                break;
        }
    }


    private void updateFormat() {
        formatCurrencyFormat.setCode(getCode());
        formatCurrencyFormat.setSymbol(getSymbol());
        formatCurrencyFormat.setSymbolPosition(getSymbolPosition());
        formatCurrencyFormat.setGroupSeparator(getGroupSeparator());
        formatCurrencyFormat.setDecimalSeparator(getDecimalSeparator());
        formatCurrencyFormat.setDecimalCount(getDecimalCount());
        currencyFormatTextView.setText(formatCurrencyFormat.format(100000));
    }

    private void prepareCurrenciesAutoComplete() {
        // Build currencies set
        final Set<java.util.Currency> currencySet = new HashSet<>();
        final Locale[] locales = Locale.getAvailableLocales();
        for (Locale loc : locales) {
            try {
                currencySet.add(java.util.Currency.getInstance(loc));
            } catch (Exception exc) {
                // Locale not found
            }
        }

        // Build currencies codes array
        final String[] currencies = new String[currencySet.size()];
        int i = 0;
        for (java.util.Currency currency : currencySet) {
            currencies[i++] = currency.getCurrencyCode();
        }

        // Prepare auto complete view
        final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, currencies);
        codeEditTextView.setAdapter(autoCompleteAdapter);
        codeEditTextView.setThreshold(0);
        codeEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkForCurrencyDuplicate(codeEditTextView.getText().toString());
            }
        });
    }

    private boolean checkForCurrencyDuplicate(String code) {
        if (isCurrencyExists(code) && isNewModel()) {
            errorTextView.setText(getActivity().getString(R.string.l_currency_exists));
            errorTextView.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorTextView.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isCurrencyExists(String code) {
        return existingCurrencyCodes.contains(code.toUpperCase());
    }

    private String getId() {
        return getStoredModel() != null ? getStoredModel().getId() : null;
    }

    private String getCode() {
        if (code != null) {
            return code;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getCode();
        }

        return null;
    }

    private String getSymbol() {
        if (symbol != null) {
            return symbol;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getSymbol();
        }

        return "";
    }

    private SymbolPosition getSymbolPosition() {
        if (symbolPosition != null) {
            return symbolPosition;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getSymbolPosition();
        }

        return SymbolPosition.FarRight;
    }

    private GroupSeparator getGroupSeparator() {
        if (groupSeparator != null) {
            return groupSeparator;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getGroupSeparator();
        }

        return GroupSeparator.Comma;
    }

    private DecimalSeparator getDecimalSeparator() {
        if (decimalSeparator != null) {
            return decimalSeparator;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getDecimalSeparator();
        }

        return DecimalSeparator.Dot;
    }

    private int getDecimalCount() {
        if (decimalCount != null) {
            return decimalCount;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getDecimalCount();
        }

        return 2;
    }

    private void toggleSymbolPosition() {
        switch (getSymbolPosition()) {
            case CloseRight:
                symbolPosition = SymbolPosition.FarLeft;
                break;
            case FarRight:
                symbolPosition = SymbolPosition.CloseRight;
                break;
            case CloseLeft:
                symbolPosition = SymbolPosition.FarRight;
                break;
            case FarLeft:
                symbolPosition = SymbolPosition.CloseLeft;
                break;
            default:
                throw new IllegalArgumentException("Symbol position " + getSymbolPosition() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleGroupSeparator() {
        switch (getGroupSeparator()) {
            case None:
                groupSeparator = GroupSeparator.Comma;
                break;
            case Dot:
                groupSeparator = GroupSeparator.Space;
                break;
            case Comma:
                groupSeparator = GroupSeparator.Dot;
                break;
            case Space:
                groupSeparator = GroupSeparator.None;
                break;
            default:
                throw new IllegalArgumentException("Group separator " + getGroupSeparator() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleDecimalSeparator() {
        switch (getDecimalSeparator()) {
            case Dot:
                decimalSeparator = DecimalSeparator.Space;
                break;
            case Comma:
                decimalSeparator = DecimalSeparator.Dot;
                break;
            case Space:
                decimalSeparator = DecimalSeparator.Comma;
                break;
            default:
                throw new IllegalArgumentException("Decimal separator " + getDecimalSeparator() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleDecimalCount() {
        switch (getDecimalCount()) {
            case 0:
                decimalCount = 2;
                break;
            case 1:
                decimalCount = 0;
                break;
            case 2:
                decimalCount = 1;
                break;
            default:
                throw new IllegalArgumentException("Decimal count " + getDecimalCount() + " is not supported.");
        }
        updateFormat();
    }
}

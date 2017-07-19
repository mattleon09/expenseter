package com.mattleo.finance.api.requests;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.mattleo.finance.api.User;
import com.mattleo.finance.backend.endpoint.currencies.Currencies;
import com.mattleo.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.model.Model;
import com.mattleo.finance.data.providers.CurrenciesProvider;

import java.util.List;

public class GetCurrenciesRequest extends GetRequest<CurrencyEntity> {
    private final Currencies currenciesService;

    public GetCurrenciesRequest(Context context, User user, Currencies currenciesService) {
        super(null, context, user);
        Preconditions.notNull(currenciesService, "Currencies cannot be null.");

        this.currenciesService = currenciesService;
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getCurrenciesTimestamp();
    }

    @Override protected List<CurrencyEntity> performRequest(long timestamp) throws Exception {
        return currenciesService.list(timestamp).execute().getItems();
    }

    @Override protected Model getModelFrom(CurrencyEntity entity) {
        return null;
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setCurrenciesTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override protected void onValuesCreated(ContentValues values) {
        super.onValuesCreated(values);
//        values.remove(Tables.Currencies.EXCHANGE_RATE.getName());
    }
}

package com.mattleo.finance.api.currencies;

import android.content.Context;

import com.mattleo.finance.api.Request;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.utils.EventBus;

import java.util.concurrent.ExecutorService;

public class CurrenciesApi {
    private final ExecutorService executor;
    private final Context context;
    private final EventBus eventBus;
    private final CurrenciesRequestService requestService;

    public CurrenciesApi(ExecutorService executor, Context context, EventBus eventBus, CurrenciesRequestService requestService) {
        this.executor = Preconditions.notNull(executor, "Executor cannot be null.");
        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.eventBus = Preconditions.notNull(eventBus, "EventBus cannot be null.");
        this.requestService = Preconditions.notNull(requestService, "CurrenciesRequestService cannot be null.");
    }

    public void updateExchangeRates() {
        updateExchangeRates(null);
    }

    public void updateExchangeRates(String fromCode) {
        final UpdateExchangeRatesRequest request = new UpdateExchangeRatesRequest(eventBus, requestService, context, fromCode);
        executeRequest(request);
    }

    private void executeRequest(Request request) {
        executor.submit(request);
    }
}

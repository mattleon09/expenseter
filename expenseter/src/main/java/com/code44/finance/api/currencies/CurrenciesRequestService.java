package com.mattleo.finance.api.currencies;

import retrofit.http.GET;
import retrofit.http.Query;

public interface CurrenciesRequestService {
    @GET("/v1/public/yql?env=store://datatables.org/alltableswithkeys&format=json")
    public ExchangeRatesResponse getExchangeRates(@Query("q") String query);
}

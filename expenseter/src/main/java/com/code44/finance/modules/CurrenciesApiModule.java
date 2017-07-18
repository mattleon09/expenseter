package com.mattleo.finance.modules;

import android.content.Context;

import com.mattleo.finance.BuildConfig;
import com.mattleo.finance.api.currencies.CurrenciesApi;
import com.mattleo.finance.api.currencies.CurrenciesRequestService;
import com.mattleo.finance.qualifiers.ApplicationContext;
import com.mattleo.finance.qualifiers.Network;
import com.mattleo.finance.services.StartupService;
import com.mattleo.finance.utils.EventBus;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true,
        injects = {
                StartupService.class
        }
)
public class CurrenciesApiModule {
    @Provides @Singleton public CurrenciesApi provideCurrenciesApi(@Network ExecutorService executor, @ApplicationContext Context context, EventBus eventBus, CurrenciesRequestService currenciesRequestService) {
        return new CurrenciesApi(executor, context, eventBus, currenciesRequestService);
    }

    @Provides @Singleton public CurrenciesRequestService provideCurrenciesRequestService() {
        final String endpoint = "http://query.yahooapis.com";
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint).build();
        restAdapter.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
        return restAdapter.create(CurrenciesRequestService.class);
    }
}

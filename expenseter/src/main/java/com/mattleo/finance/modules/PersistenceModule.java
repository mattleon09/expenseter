package com.mattleo.finance.modules;

import android.content.Context;

import com.mattleo.finance.data.db.DBHelper;
import com.mattleo.finance.data.providers.AccountsProvider;
import com.mattleo.finance.data.providers.CategoriesProvider;
import com.mattleo.finance.data.providers.CurrenciesProvider;
import com.mattleo.finance.data.providers.ExchangeRatesProvider;
import com.mattleo.finance.data.providers.TagsProvider;
import com.mattleo.finance.data.providers.TransactionsProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.qualifiers.ApplicationContext;
import com.mattleo.finance.services.StartupService;
import com.mattleo.finance.utils.preferences.GeneralPrefs;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                CurrenciesProvider.class,
                CategoriesProvider.class,
                TagsProvider.class,
                AccountsProvider.class,
                TransactionsProvider.class,
                ExchangeRatesProvider.class,
                StartupService.class
        }
)
public class PersistenceModule {
    @Provides @Singleton public DBHelper provideDBHelper(@ApplicationContext Context context, CurrenciesManager currenciesManager) {
        return new DBHelper(context, currenciesManager);
    }

    @Provides @Singleton public CurrenciesManager provideCurrenciesManager(@ApplicationContext Context context, GeneralPrefs generalPrefs) {
        return new CurrenciesManager(context, generalPrefs);
    }

    @Provides @Singleton public AmountFormatter provideAmountFormatter(@ApplicationContext Context context, CurrenciesManager currenciesManager) {
        return new AmountFormatter(context, currenciesManager);
    }
}

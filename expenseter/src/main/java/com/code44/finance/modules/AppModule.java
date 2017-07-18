package com.mattleo.finance.modules;

import android.content.Context;

import com.mattleo.finance.App;
import com.mattleo.finance.modules.providers.AnalyticsProviderModule;
import com.mattleo.finance.qualifiers.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                App.class
        },
        includes = {
                UtilsModule.class,
                UIModule.class,
                ApiModule.class,
                CurrenciesApiModule.class,
                AccountModule.class,
                PersistenceModule.class,
                ViewModule.class,
                AnalyticsProviderModule.class
        }
)
public final class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides @Singleton @ApplicationContext Context provideApplication() {
        return app;
    }
}

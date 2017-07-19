package com.mattleo.finance.modules;

import android.content.Context;

import com.mattleo.finance.api.NetworkExecutor;
import com.mattleo.finance.money.Calculator;
import com.mattleo.finance.qualifiers.ApplicationContext;
import com.mattleo.finance.qualifiers.Local;
import com.mattleo.finance.qualifiers.Network;
import com.mattleo.finance.ui.playservices.GoogleApiConnection;
import com.mattleo.finance.ui.settings.security.Security;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.LayoutType;
import com.mattleo.finance.utils.LocalExecutor;
import com.mattleo.finance.utils.interval.ActiveInterval;
import com.mattleo.finance.utils.interval.CurrentInterval;
import com.mattleo.finance.utils.preferences.GeneralPrefs;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public final class UtilsModule {
    @Provides @Singleton public EventBus providesEventBus() {
        return new EventBus();
    }

    @Provides public Calculator provideCalculator(@ApplicationContext Context context) {
        return new Calculator(context);
    }

    @Provides @Singleton @Network public ExecutorService provideNetworkExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new NetworkExecutor(numberCores * 2 + 1);
    }

    @Provides @Singleton @Local public ExecutorService provideLocalExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new LocalExecutor(numberCores * 2 + 1);
    }

    @Provides @Singleton public CurrentInterval provideCurrentInterval(@ApplicationContext Context context, EventBus eventBus, GeneralPrefs generalPrefs) {
        return new CurrentInterval(context, eventBus, generalPrefs.getIntervalType(), generalPrefs.getIntervalLength());
    }

    @Provides @Singleton public ActiveInterval provideActiveInterval(@ApplicationContext Context context, EventBus eventBus, GeneralPrefs generalPrefs) {
        return new ActiveInterval(context, eventBus, generalPrefs.getIntervalType(), generalPrefs.getIntervalLength());
    }

    @Provides public LayoutType provideLayoutType(@ApplicationContext Context context) {
        return new LayoutType(context);
    }

    @Provides @Singleton public GoogleApiConnection provideGoogleApiConnection(EventBus eventBus) {
        return new GoogleApiConnection(eventBus);
    }

    @Provides @Singleton public Security provideSecurity(@ApplicationContext Context context, EventBus eventBus) {
        return new Security(context, eventBus);
    }
}

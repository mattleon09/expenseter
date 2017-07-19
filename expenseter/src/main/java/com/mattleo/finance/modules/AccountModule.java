package com.mattleo.finance.modules;

import android.content.Context;

import com.mattleo.finance.api.GcmRegistration;
import com.mattleo.finance.api.User;
import com.mattleo.finance.data.db.DBHelper;
import com.mattleo.finance.qualifiers.ApplicationContext;
import com.mattleo.finance.services.StartupService;
import com.mattleo.finance.ui.settings.security.Security;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.preferences.GeneralPrefs;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                StartupService.class
        }
)
class AccountModule {
    @Provides @Singleton public User provideUser(@ApplicationContext Context context, DBHelper dbHelper, GcmRegistration gcmRegistration, Security security, EventBus eventBus) {
        return new User(context, dbHelper, gcmRegistration, security, eventBus);
    }

    @Provides @Singleton public GcmRegistration provideGcmRegistration(@ApplicationContext Context context) {
        return new GcmRegistration(context);
    }

    @Provides @Singleton public GeneralPrefs provideGeneralPrefs(@ApplicationContext Context context, EventBus eventBus) {
        return new GeneralPrefs(context, eventBus);
    }
}

package com.mattleo.finance.modules.providers;

import android.content.Context;

import com.mattleo.finance.BuildConfig;
import com.mattleo.finance.qualifiers.AppTracker;
import com.mattleo.finance.qualifiers.ApplicationContext;
import com.mattleo.finance.utils.analytics.Analytics;
import com.mattleo.finance.utils.preferences.GeneralPrefs;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public class AnalyticsProviderModule {
    private static final String APP_TRACKER_ID = "UA-38249360-1";
    private static final long SESSION_TIMEOUT = 3 * 60; // 3 minutes

    @Provides @Singleton public Analytics provideAnalytics(@AppTracker Tracker tracker) {
        return new Analytics(tracker);
    }

    @Provides public GoogleAnalytics provideGoogleAnalytics(@ApplicationContext Context context, GeneralPrefs generalPrefs) {
        final GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        googleAnalytics.setDryRun(BuildConfig.DEBUG);
        googleAnalytics.setAppOptOut(generalPrefs.isAnalyticsOptOut());
        return googleAnalytics;
    }

    @Provides @Singleton @AppTracker public Tracker provideAppTracker(GoogleAnalytics googleAnalytics) {
        final Tracker tracker = googleAnalytics.newTracker(APP_TRACKER_ID);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(false);
        tracker.setSessionTimeout(SESSION_TIMEOUT);
        return tracker;
    }
}

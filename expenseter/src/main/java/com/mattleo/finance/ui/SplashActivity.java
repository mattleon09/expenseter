package com.mattleo.finance.ui;

import android.os.Bundle;

import com.mattleo.finance.BuildConfig;
import com.mattleo.finance.services.StartupService;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.overview.OverviewActivity;
import com.mattleo.finance.utils.preferences.GeneralPrefs;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {
    @Inject GeneralPrefs generalPrefs;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        checkVersionUpdate();

        OverviewActivity.start(this);
        StartupService.start(this);
        finish();
    }

    private void checkVersionUpdate() {
        final int lastVersionCode = generalPrefs.getLastVersionCode();
        final int currentVersionCode = BuildConfig.VERSION_CODE;

        if (lastVersionCode < currentVersionCode) {
            // TODO Start upgrade service.
            generalPrefs.setLastVersionCode(currentVersionCode);
        }
    }
}

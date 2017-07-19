package com.mattleo.finance.ui.settings.data;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import com.mattleo.finance.R;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.utils.analytics.Analytics;

public class DataActivity extends BaseActivity {
    public static void start(Context context) {
        startActivity(context, makeIntentForActivity(context, DataActivity.class));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        getSupportActionBar().setTitle(R.string.your_data);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, DataFragment.newInstance()).commit();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        return true;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.YourData;
    }
}

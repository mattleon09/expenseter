package com.mattleo.finance.ui.categories.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattleo.finance.R;
import com.mattleo.finance.common.model.TransactionType;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ActivityPresenter;
import com.mattleo.finance.utils.analytics.Analytics;

public class CategoriesActivity extends BaseActivity {
    public static void start(Context context) {
        final Intent intent = makeIntentForActivity(context, CategoriesActivity.class);
        CategoriesActivityPresenter.addViewExtras(intent);
        startActivity(context, intent);
    }

    public static void startSelect(Activity activity, int requestCode, TransactionType transactionType) {
        final Intent intent = makeIntentForActivity(activity, CategoriesActivity.class);
        CategoriesActivityPresenter.addSelectExtras(intent);
        CategoriesActivityPresenter.addExtras(intent, transactionType);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_categories);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CategoriesActivityPresenter();
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CategoryList;
    }
}

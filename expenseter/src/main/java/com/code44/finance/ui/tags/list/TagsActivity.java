package com.mattleo.finance.ui.tags.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattleo.finance.R;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ActivityPresenter;
import com.mattleo.finance.utils.analytics.Analytics;

import java.util.List;

public class TagsActivity extends BaseActivity {
    public static void start(Context context) {
        final Intent intent = makeIntentForActivity(context, TagsActivity.class);
        TagsActivityPresenter.addViewExtras(intent);
        startActivity(context, intent);
    }

    public static void startMultiSelect(Activity activity, int requestCode, List<Tag> selectedTags) {
        final Intent intent = makeIntentForActivity(activity, TagsActivity.class);
        TagsActivityPresenter.addMultiSelectExtras(intent, selectedTags);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_tags);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new TagsActivityPresenter();
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TagList;
    }
}

package com.mattleo.finance.ui.tags.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.widget.TextView;

import com.mattleo.finance.R;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.providers.TagsProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ModelActivityPresenter;
import com.mattleo.finance.ui.reports.trends.TrendsChartView;
import com.mattleo.finance.ui.tags.edit.TagEditActivity;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.interval.BaseInterval;

class TagActivityPresenter extends ModelActivityPresenter<Tag> {
    private final BaseInterval interval;
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    private TagTrendsChartPresenter tagTrendsChartPresenter;
    private TextView titleTextView;

    public TagActivityPresenter(EventBus eventBus, BaseInterval interval, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(eventBus);
        this.interval = interval;
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        titleTextView = findView(activity, R.id.titleTextView);

        final TrendsChartView trendsChartView = findView(activity, R.id.trendsChartView);
        tagTrendsChartPresenter = new TagTrendsChartPresenter(trendsChartView, currenciesManager, amountFormatter, activity.getSupportLoaderManager(), interval);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(context, TagsProvider.uriTag(modelId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override protected void onModelLoaded(Tag model) {
        titleTextView.setText(model.getTitle());
        tagTrendsChartPresenter.setTagAndInterval(model, interval);
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        TagEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return TagsProvider.uriTags();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.Tags.ID + "=?", new String[]{String.valueOf(modelId)});
    }
}

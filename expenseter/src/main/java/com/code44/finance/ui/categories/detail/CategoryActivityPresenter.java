package com.mattleo.finance.ui.categories.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import com.mattleo.finance.R;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Category;
import com.mattleo.finance.data.providers.CategoriesProvider;
import com.mattleo.finance.money.AmountFormatter;
import com.mattleo.finance.money.CurrenciesManager;
import com.mattleo.finance.ui.categories.edit.CategoryEditActivity;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ModelActivityPresenter;
import com.mattleo.finance.ui.reports.trends.TrendsChartView;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.interval.BaseInterval;

class CategoryActivityPresenter extends ModelActivityPresenter<Category> {
    private final BaseInterval interval;
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    private CategoryTrendsChartPresenter categoryTrendsChartPresenter;
    private TextView titleTextView;
    private ImageView colorImageView;

    public CategoryActivityPresenter(EventBus eventBus, BaseInterval interval, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(eventBus);
        this.interval = interval;
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        titleTextView = findView(activity, R.id.titleTextView);
        colorImageView = findView(activity, R.id.colorImageView);

        final TrendsChartView trendsChartView = findView(activity, R.id.trendsChartView);
        categoryTrendsChartPresenter = new CategoryTrendsChartPresenter(trendsChartView, currenciesManager, amountFormatter, activity.getSupportLoaderManager(), interval);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Categories.getQuery(null).asCursorLoader(context, CategoriesProvider.uriCategory(modelId));
    }

    @Override protected Category getModelFrom(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override protected void onModelLoaded(Category model) {
        titleTextView.setText(model.getTitle());
        colorImageView.setColorFilter(model.getColor());
        categoryTrendsChartPresenter.setCategoryAndInterval(model, interval);
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        CategoryEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return CategoriesProvider.uriCategories();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.Categories.ID + "=?", new String[]{String.valueOf(modelId)});
    }
}

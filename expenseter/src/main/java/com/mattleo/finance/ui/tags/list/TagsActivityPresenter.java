package com.mattleo.finance.ui.tags.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.view.View;

import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.providers.TagsProvider;
import com.mattleo.finance.ui.common.adapters.ModelsAdapter;
import com.mattleo.finance.ui.common.presenters.ModelsActivityPresenter;
import com.mattleo.finance.ui.tags.detail.TagActivity;
import com.mattleo.finance.ui.tags.edit.TagEditActivity;

class TagsActivityPresenter extends ModelsActivityPresenter<Tag> {
    @Override protected ModelsAdapter<Tag> createAdapter(ModelsAdapter.OnModelClickListener<Tag> defaultOnModelClickListener) {
        return new TagsAdapter(defaultOnModelClickListener);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Tags.getQuery().sortOrder("lower(" + Tables.Tags.TITLE + ")").asCursorLoader(context, TagsProvider.uriTags());
    }

    @Override protected void onModelClick(Context context, View view, Tag model, Cursor cursor, int position) {
        TagActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        TagEditActivity.start(context, modelId);
    }
}

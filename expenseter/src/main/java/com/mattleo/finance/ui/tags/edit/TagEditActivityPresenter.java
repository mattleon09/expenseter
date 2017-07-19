package com.mattleo.finance.ui.tags.edit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.mattleo.finance.R;
import com.mattleo.finance.data.DataStore;
import com.mattleo.finance.data.db.Tables;
import com.mattleo.finance.data.model.Tag;
import com.mattleo.finance.data.providers.TagsProvider;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.ThemeUtils;

class TagEditActivityPresenter extends ModelEditActivityPresenter<Tag> implements TextWatcher {
    private static final String STATE_TITLE = "STATE_TITLE";

    private EditText titleEditText;
    private String title;

    public TagEditActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        titleEditText = findView(activity, R.id.titleEditText);
        titleEditText.addTextChangedListener(this);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(STATE_TITLE);
            onDataChanged(getStoredModel());
        }
    }

    @Override public void onSaveInstanceState(BaseActivity activity, Bundle outState) {
        super.onSaveInstanceState(activity, outState);
        outState.putString(STATE_TITLE, title);
    }

    @Override protected void onDataChanged(Tag storedModel) {
        titleEditText.setText(getTitle());
        titleEditText.setSelection(titleEditText.getText().length());
    }

    @Override protected boolean onSave() {
        boolean canSave = true;

        final String title = getTitle();
        if (TextUtils.isEmpty(title)) {
            canSave = false;
            titleEditText.setHintTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.textColorNegative));
        }

        if (canSave) {
            final Tag tag = new Tag();
            tag.setId(getId());
            tag.setTitle(title);

            DataStore.insert().model(tag).into(titleEditText.getContext(), TagsProvider.uriTags());
        }

        return canSave;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Tags.getQuery().asCursorLoader(context, TagsProvider.uriTag(modelId));
    }

    @Override protected Tag getModelFrom(Cursor cursor) {
        return Tag.from(cursor);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override public void afterTextChanged(Editable s) {
        title = titleEditText.getText().toString();
    }

    private String getId() {
        return getStoredModel() != null ? getStoredModel().getId() : null;
    }

    private String getTitle() {
        if (title != null) {
            return title;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTitle();
        }

        return null;
    }
}

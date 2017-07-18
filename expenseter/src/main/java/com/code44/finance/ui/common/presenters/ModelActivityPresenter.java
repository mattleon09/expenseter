package com.mattleo.finance.ui.common.presenters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.mattleo.finance.R;
import com.mattleo.finance.common.utils.Strings;
import com.mattleo.finance.data.model.Model;
import com.mattleo.finance.ui.ModelFragment;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.dialogs.DeleteDialogFragment;
import com.mattleo.finance.utils.EventBus;
import com.squareup.otto.Subscribe;

public abstract class ModelActivityPresenter<M extends Model> extends ActivityPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String EXTRA_MODEL_ID = ModelActivityPresenter.class.getName() + ".EXTRA_MODEL_ID";

    private static final int REQUEST_DELETE = 6666;

    private static final String FRAGMENT_DELETE = "FRAGMENT_DELETE";

    private static final int LOADER_MODEL = 1000;

    private final EventBus eventBus;
    private final Object eventHandler = new Object() {
        @Subscribe public void onItemDeleted(DeleteDialogFragment.DeleteDialogEvent event) {
            if (event.getRequestCode() == ModelFragment.REQUEST_DELETE && event.isPositiveClicked()) {
                getActivity().finish();
            }
        }
    };

    private String modelId;
    private M storedModel;

    protected ModelActivityPresenter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public static void addExtras(Intent intent, String modelId) {
        intent.putExtra(EXTRA_MODEL_ID, modelId);
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        eventBus.register(eventHandler);

        // Get extras
        modelId = activity.getIntent().getStringExtra(EXTRA_MODEL_ID);
        if (Strings.isEmpty(modelId)) {
            modelId = "0";
        }

        // Loader
        activity.getSupportLoaderManager().initLoader(LOADER_MODEL, null, this);
    }

    @Override public void onDestroy(BaseActivity activity) {
        super.onDestroy(activity);
        eventBus.unregister(eventHandler);
    }

    @Override public boolean onCreateOptionsMenu(BaseActivity activity, Menu menu) {
        super.onCreateOptionsMenu(activity, menu);
        activity.getMenuInflater().inflate(R.menu.model, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(BaseActivity activity, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                startModelEdit(activity, modelId);
                return true;

            case R.id.action_delete:
                final Uri deleteUri = getDeleteUri();
                final Pair<String, String[]> deleteSelection = getDeleteSelection(modelId);
                DeleteDialogFragment.newInstance(getActivity(), REQUEST_DELETE, deleteUri, deleteSelection.first, deleteSelection.second).show(getActivity().getSupportFragmentManager(), FRAGMENT_DELETE);
                return true;
        }
        return super.onOptionsItemSelected(activity, item);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODEL) {
            return getModelCursorLoader(getActivity(), modelId);
        }

        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODEL && data.moveToFirst()) {
            storedModel = getModelFrom(data);
            onModelLoaded(storedModel);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    protected abstract CursorLoader getModelCursorLoader(Context context, String modelId);

    protected abstract M getModelFrom(Cursor cursor);

    protected abstract void onModelLoaded(M model);

    protected abstract void startModelEdit(Context context, String modelId);

    protected abstract Uri getDeleteUri();

    protected abstract Pair<String, String[]> getDeleteSelection(String modelId);

    protected EventBus getEventBus() {
        return eventBus;
    }

    protected String getModelId() {
        return modelId;
    }

    protected M getStoredModel() {
        return storedModel;
    }
}

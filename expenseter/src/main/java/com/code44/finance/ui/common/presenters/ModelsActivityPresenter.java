package com.mattleo.finance.ui.common.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mattleo.finance.R;
import com.mattleo.finance.data.model.Model;
import com.mattleo.finance.ui.common.activities.BaseActivity;
import com.mattleo.finance.ui.common.adapters.ModelsAdapter;
import com.mattleo.finance.ui.common.recycler.DividerDecoration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ModelsActivityPresenter<M extends Model> extends RecyclerViewPresenter<ModelsAdapter<M>> implements LoaderManager.LoaderCallbacks<Cursor>, ModelsAdapter.OnModelClickListener<M> {
    public static final String EXTRA_MODE = ModelsActivityPresenter.class.getName() + ".EXTRA_MODE";
    public static final String EXTRA_SELECTED_MODELS = ModelsActivityPresenter.class.getName() + ".EXTRA_SELECTED_MODELS";

    protected static final int LOADER_MODELS = 4124;

    private static final String RESULT_EXTRA_MODEL = "RESULT_EXTRA_MODEL";
    private static final String RESULT_EXTRA_MODELS = "RESULT_EXTRA_MODELS";

    private static final String STATE_SELECTED_MODELS = ModelsActivityPresenter.class.getName() + ".STATE_SELECTED_MODELS";

    private Mode mode;

    public static void addViewExtras(Intent intent) {
        intent.putExtra(EXTRA_MODE, Mode.View);
    }

    public static void addSelectExtras(Intent intent) {
        intent.putExtra(EXTRA_MODE, Mode.Select);
    }

    public static void addMultiSelectExtras(Intent intent, List<? extends Model> selectedModels) {
        intent.putExtra(EXTRA_MODE, Mode.MultiSelect);
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (Model model : selectedModels) {
            parcelables[index++] = model;
        }
        intent.putExtra(EXTRA_SELECTED_MODELS, parcelables);
    }

    public static <T extends Parcelable> T getModelExtra(Intent data) {
        return data.getParcelableExtra(RESULT_EXTRA_MODEL);
    }

    public static <T extends Parcelable> List<T> getModelsExtra(Intent data) {
        final Parcelable[] parcelables = data.getParcelableArrayExtra(RESULT_EXTRA_MODELS);
        final List<T> models = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            //noinspection unchecked
            models.add((T) parcelable);
        }
        return models;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        mode = (Mode) activity.getIntent().getSerializableExtra(EXTRA_MODE);
        final Parcelable[] selectedModels = activity.getIntent().getParcelableArrayExtra(EXTRA_SELECTED_MODELS);

        getAdapter().setMode(mode);
        if (mode == Mode.MultiSelect) {
            final Set<M> selectedModelsSet = new HashSet<>();
            final Parcelable[] parcelables;
            if (savedInstanceState == null) {
                parcelables = selectedModels;
            } else {
                final List<Parcelable> parcelableList = savedInstanceState.getParcelableArrayList(STATE_SELECTED_MODELS);
                parcelables = new Parcelable[parcelableList.size()];
                parcelableList.toArray(parcelables);
            }

            for (Parcelable parcelable : parcelables) {
                //noinspection unchecked
                selectedModelsSet.add((M) parcelable);
            }
            getAdapter().setSelectedModels(selectedModelsSet);
        }

        final View editButtonsContainerView = activity.findViewById(R.id.editButtonsContainerView);
        if (editButtonsContainerView != null) {
            if (mode == Mode.MultiSelect) {
                editButtonsContainerView.setVisibility(View.VISIBLE);
                final Button saveButton = findView(editButtonsContainerView, R.id.saveButton);
                final Button cancelButton = findView(editButtonsContainerView, R.id.cancelButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        onMultipleModelsSelected(getAdapter().getSelectedModels());
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        onMultipleModelsSelectCanceled();
                    }
                });
            } else {
                editButtonsContainerView.setVisibility(View.GONE);
            }
        }

        activity.getSupportLoaderManager().initLoader(LOADER_MODELS, null, this);
    }

    @Override public boolean onCreateOptionsMenu(BaseActivity activity, Menu menu) {
        super.onCreateOptionsMenu(activity, menu);
        activity.getMenuInflater().inflate(R.menu.models, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(BaseActivity activity, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startModelEdit(getActivity(), null);
                break;
        }
        return super.onOptionsItemSelected(activity, item);
    }

    @Override public void onSaveInstanceState(BaseActivity activity, Bundle outState) {
        super.onSaveInstanceState(activity, outState);
        outState.putParcelableArrayList(STATE_SELECTED_MODELS, new ArrayList<Parcelable>(getAdapter().getSelectedModels()));
    }

    @Override protected void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        final RecyclerView.ItemDecoration[] itemDecorations = getItemDecorations();
        if (itemDecorations != null) {
            for (RecyclerView.ItemDecoration itemDecoration : itemDecorations) {
                recyclerView.addItemDecoration(itemDecoration);
            }
        }
    }

    @Override protected ModelsAdapter<M> createAdapter() {
        return createAdapter(this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            return getModelsCursorLoader(getRecyclerView().getContext());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            getAdapter().setCursor(data);
            getEmptyContainerView().setEmpty(getAdapter().getItemCount() == 0);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_MODELS) {
            getAdapter().setCursor(null);
        }
    }

    @Override public void onModelClick(View view, M model, Cursor cursor, int position, Mode mode, boolean isSelected) {
        if (mode == Mode.View) {
            onModelClick(view.getContext(), view, model, cursor, position);
        } else if (mode == Mode.Select) {
            onModelSelected(model);
        } else {
            getAdapter().toggleModelSelected(model, position);
        }
    }

    @Override public void onEmptyAddClick(View v) {
        startModelEdit(v.getContext(), null);
    }

    protected abstract ModelsAdapter<M> createAdapter(ModelsAdapter.OnModelClickListener<M> defaultOnModelClickListener);

    protected abstract CursorLoader getModelsCursorLoader(Context context);

    protected abstract void onModelClick(Context context, View view, M model, Cursor cursor, int position);

    protected abstract void startModelEdit(Context context, String modelId);

    protected RecyclerView.ItemDecoration[] getItemDecorations() {
        return new RecyclerView.ItemDecoration[]{new DividerDecoration(getRecyclerView().getContext())};
    }

    protected Mode getMode() {
        return mode;
    }

    private void onModelSelected(M model) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MODEL, model);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    private void onMultipleModelsSelected(Set<M> selectedModels) {
        final Intent data = new Intent();
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (Model model : selectedModels) {
            parcelables[index++] = model;
        }
        data.putExtra(RESULT_EXTRA_MODELS, parcelables);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    private void onMultipleModelsSelectCanceled() {
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    public static enum Mode {
        View, Select, MultiSelect
    }

}

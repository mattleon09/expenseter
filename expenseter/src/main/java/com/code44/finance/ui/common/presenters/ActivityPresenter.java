package com.mattleo.finance.ui.common.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mattleo.finance.ui.common.activities.BaseActivity;

public class ActivityPresenter extends Presenter {
    private BaseActivity activity;

    public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        this.activity = activity;
    }

    public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, Intent data) {
    }

    public void onStart(BaseActivity activity) {
    }

    public void onResume(BaseActivity activity) {
    }

    public void onPause(BaseActivity activity) {
    }

    public void onStop(BaseActivity activity) {
    }

    public void onSaveInstanceState(BaseActivity activity, Bundle outState) {
    }

    public void onDestroy(BaseActivity activity) {
        this.activity = null;
    }

    public boolean onCreateOptionsMenu(BaseActivity activity, Menu menu) {
        return false;
    }

    public boolean onPrepareOptionsMenu(BaseActivity activity, Menu menu) {
        return false;
    }

    public boolean onOptionsItemSelected(BaseActivity activity, MenuItem item) {
        return false;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    protected <T extends View> T findView(BaseActivity activity, @IdRes int viewId) {
        //noinspection unchecked
        T view = (T) activity.findViewById(viewId);
        if (view == null) {
            throw new IllegalStateException("Layout must contain view with id: " + activity.getResources().getResourceName(viewId) + " in " + activity.getClass().getName() + ". You should override onViewCreated(Bundle) method.");
        }

        return view;
    }
}

package com.mattleo.finance.data.providers;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;

import com.mattleo.finance.App;
import com.mattleo.finance.BuildConfig;
import com.mattleo.finance.data.db.DBHelper;

import javax.inject.Inject;

public abstract class BaseProvider extends ContentProvider {
    protected static final String CONTENT_URI_BASE = "content://";

    protected static final String TYPE_LIST_BASE = "vnd.android.cursor.dir/vnd.mattleo.";
    protected static final String TYPE_ITEM_BASE = "vnd.android.cursor.item/vnd.mattleo.";

    protected final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    @Inject DBHelper dbHelper;
    private SQLiteDatabase database;

    protected static String getAuthority(Class<? extends BaseProvider> cls) {
        return BuildConfig.APPLICATION_ID + ".data.providers." + cls.getSimpleName();
    }

    @Override public boolean onCreate() {
        return true;
    }

    protected String getAuthority() {
        return getAuthority(getClass());
    }

    protected SQLiteDatabase getDatabase() {
        if (database == null) {
            App.with(getContext()).inject(this);
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }
}

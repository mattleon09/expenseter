package com.mattleo.finance.ui.settings.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cloudrail.si.interfaces.CloudStorage;
import com.mattleo.finance.R;
import com.mattleo.finance.ui.BaseFragment;
import com.mattleo.finance.ui.dialogs.ListDialogFragment;
import com.mattleo.finance.ui.dropbox.DropboxApiFragment;
import com.mattleo.finance.ui.dropbox.FilesActivity;
import com.mattleo.finance.ui.settings.SettingsActivity;
import com.dropbox.core.android.Auth;
import com.squareup.otto.Subscribe;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.mattleo.finance.ui.settings.data.ExportActivity.FRAGMENT_DROPBOX_API;

public class DataFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_LOGIN_DROPBOX = 1;
    private static final int REQUEST_BACKUP_DESTINATION = 2;
    private static final int REQUEST_RESTORE_DESTINATION = 3;
    private static final int REQUEST_RESTORE_AND_MERGE_DESTINATION = 4;
    private static final int REQUEST_EXPORT_CSV_DESTINATION = 5;

    private static final String FRAGMENT_DESTINATION = "FRAGMENT_DESTINATION";

    private static final String ARG_EXPORT_TYPE = "ARG_EXPORT_TYPE";

    private Random mRandom = new Random();
    private Runnable mRunnable;
    private int mInterval = 4000;
    private int mCounter;
    private Handler mHandler;
    private static String dbxAccessToken = null;
    private Button login_dbx;

    public static DataFragment newInstance() {
        return new DataFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        login_dbx = (Button) view.findViewById(R.id.login_dbx);
        final Button backup_B = (Button) view.findViewById(R.id.backup_B);
        final Button restore_B = (Button) view.findViewById(R.id.restore_B);
        final Button restoreAndMerge_B = (Button) view.findViewById(R.id.restoreAndMerge_B);
        final Button exportCsv_B = (Button) view.findViewById(R.id.exportCsv_B);

        // Setup
        login_dbx.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Activity activity = getActivity();
                Auth.startOAuth2Authentication(activity, ExportActivity.UNIQUE_DROPBOX_API_ID);
            }
        });
        backup_B.setOnClickListener(this);
        restore_B.setOnClickListener(this);
        restoreAndMerge_B.setOnClickListener(this);
        exportCsv_B.setOnClickListener(this);

        mHandler  = new Handler();

        mRunnable = new Runnable() {
            @Override public void run() {
                checkDropboxAuthorization(getActivity());
                mHandler.postDelayed(mRunnable,2000);
            }
        };
        mHandler.post(mRunnable);

    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_dbx:
                Auth.startOAuth2Authentication(getActivity(),ExportActivity.UNIQUE_DROPBOX_API_ID);




              //  chooseSourceOrDestination(REQUEST_LOGIN_DROPBOX, R.string.log_into_dropbox);
                break;
            case R.id.backup_B:
                chooseSourceOrDestination(REQUEST_BACKUP_DESTINATION, R.string.create_backup);
                break;
            case R.id.restore_B:
                chooseSourceOrDestination(REQUEST_RESTORE_DESTINATION, R.string.clear_and_restore);
                break;
            case R.id.restoreAndMerge_B:
                chooseSourceOrDestination(REQUEST_RESTORE_AND_MERGE_DESTINATION, R.string.import_backup);
                break;
            case R.id.exportCsv_B:
                chooseSourceOrDestination(REQUEST_EXPORT_CSV_DESTINATION, R.string.export_csv);
                break;
        }
    }

    @Subscribe public void onBackupDestinationSelected(ListDialogFragment.ListDialogEvent event) {
        if ((event.getRequestCode() != REQUEST_BACKUP_DESTINATION
                && event.getRequestCode() != REQUEST_RESTORE_DESTINATION
                && event.getRequestCode() != REQUEST_RESTORE_AND_MERGE_DESTINATION
                && event.getRequestCode() != REQUEST_EXPORT_CSV_DESTINATION) || event.isActionButtonClicked()) {
            return;
        }

        event.dismiss();

        if (event.getRequestCode() == REQUEST_BACKUP_DESTINATION) {
            final ExportActivity.Destination destination;
            if (event.getPosition() == 0) {
                destination = ExportActivity.Destination.GoogleDrive;
            } else {
                destination = ExportActivity.Destination.File;
            }

            ExportActivity.start(getActivity(), ExportActivity.ExportType.Backup, destination);
        } else if (event.getRequestCode() == REQUEST_RESTORE_DESTINATION) {
            final ImportActivity.Source source;
            if (event.getPosition() == 0) {
                source = ImportActivity.Source.GoogleDrive;
            } else {
                source = ImportActivity.Source.File;
            }

            ImportActivity.start(getActivity(), ImportActivity.ImportType.Backup, source);
        } else if (event.getRequestCode() == REQUEST_RESTORE_AND_MERGE_DESTINATION) {
            final ImportActivity.Source source;
            if (event.getPosition() == 0) {
                source = ImportActivity.Source.GoogleDrive;
            } else {
                source = ImportActivity.Source.File;
            }

            ImportActivity.start(getActivity(), ImportActivity.ImportType.MergeBackup, source);
        } else {
            final ExportActivity.Destination destination;
            if (event.getPosition() == 0) {
                destination = ExportActivity.Destination.GoogleDrive;
                ExportActivity.start(getActivity(), ExportActivity.ExportType.Backup , destination);
            } else if (event.getPosition() == 1) {
                destination = ExportActivity.Destination.Dropbox;
                DropboxApiFragment dropboxApiFragment = (DropboxApiFragment) getActivity().getSupportFragmentManager().findFragmentByTag(FRAGMENT_DROPBOX_API);
                if(dropboxApiFragment == null) {
                    dropboxApiFragment  = new DropboxApiFragment();
                    dropboxApiFragment.connect(getActivity());
                      getActivity().startActivity(FilesActivity.getIntent(getActivity(), ""));
                }

            } else {
                destination = ExportActivity.Destination.File;
                ExportActivity.start(getActivity(), ExportActivity.ExportType.CSV , destination);
            }



        }
    }

    private void chooseSourceOrDestination(int requestCode, int titleResId) {
        if(requestCode > 1) {
            final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
            items.add(new ListDialogFragment.ListDialogItem(getString(R.string.google_drive)));
            items.add(new ListDialogFragment.ListDialogItem(getString(R.string.dropbox)));
            items.add(new ListDialogFragment.ListDialogItem(getString(R.string.file)));

            final Bundle args = new Bundle();
            args.putSerializable(ARG_EXPORT_TYPE, ExportActivity.ExportType.Backup);

            ListDialogFragment.build(requestCode)
                    .setTitle(getString(titleResId))
                    .setArgs(args)
                    .setNegativeButtonText(getString(R.string.cancel))
                    .setItems(items)
                    .build()
                    .show(getChildFragmentManager(), FRAGMENT_DESTINATION);
        } else if(requestCode  == REQUEST_LOGIN_DROPBOX) {
            SharedPreferences prefs = getActivity().getSharedPreferences("dropbox-expenseter", MODE_PRIVATE);
            Auth.startOAuth2Authentication(getActivity(), ExportActivity.UNIQUE_DROPBOX_API_ID);
            prefs.edit().putBoolean("auto-login-dbx", true).apply();
        }
    }


    public static void checkDropboxAuthorization(Activity activity) {
        Button login_dbx;
        login_dbx = (Button) activity.findViewById(R.id.login_dbx);

        String accessToken = Auth.getOAuth2Token();
        SharedPreferences prefs = activity.getSharedPreferences("dropbox-expenseter", MODE_PRIVATE);
        if(accessToken == null) {
            login_dbx.setEnabled(true);
           accessToken =  prefs.getString("access-token",null);
            if(accessToken == null) {
                login_dbx.setText("Not Logged In");
                return;
            }
        }

        dbxAccessToken = accessToken;
        prefs.edit().putString("access-token", accessToken).apply();
        login_dbx.setText(R.string.logged_in);
        login_dbx.setEnabled(false);
        return;
    }
}

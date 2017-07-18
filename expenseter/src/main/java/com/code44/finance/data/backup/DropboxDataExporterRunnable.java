package com.mattleo.finance.data.backup;

import android.content.Context;

import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;
import com.mattleo.finance.ui.settings.data.ExportActivity;
import com.mattleo.finance.utils.EventBus;
import com.mattleo.finance.utils.errors.ExportError;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxPathV2;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import com.cloudrail.*;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Matthieu on 7/6/2017.
 */

public class DropboxDataExporterRunnable implements  Runnable {


    private final DbxClientV2 dbxClientV2;
    private final DbxPathV2 dbxPathV2;
    private final ExportActivity.ExportType exportType;
    private final ExportActivity.Destination destination;
    private final Context context;
    private final EventBus eventBus;
    private final String fileTitle;

    public DropboxDataExporterRunnable(DbxClientV2 dbxClientV2, DbxPathV2 dbxPathV2, ExportActivity.ExportType exportType, ExportActivity.Destination destination, Context context, EventBus eventBus, String fileTitle) {
        this.dbxClientV2 = dbxClientV2;
        this.dbxPathV2 = dbxPathV2;
        this.exportType = exportType;
        this.destination = destination;
        this.context = context;
        this.eventBus = eventBus;
        this.fileTitle = fileTitle;
    }

    @Override public void run() {


    }
}

package com.mattleo.finance.ui.dropbox;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mattleo.finance.ui.BaseFragment;
import com.mattleo.finance.ui.dialogs.ListDialogFragment;
import com.mattleo.finance.ui.settings.data.ExportActivity;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.HttpRequestor;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.http.OkHttpRequestor;
import com.dropbox.core.v2.DbxAppClientV2Base;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxClientV2Base;
import com.dropbox.core.v2.DbxPathV2;

import com.dropbox.core.http.OkHttp3Requestor;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Matthieu on 7/8/2017.
 */

public class DropboxApiFragment extends BaseFragment {
    private static final String ARG_UNIQUE_CLIENT_ID = "ARG_UNIQUE_CLIENT_ID";
    private static final String ARG_UNIQUE_CLIENT_SECRET = "ARG_UNIQUE_CLIENT_SECRET";
    private static final String ARG_USE_DROPBOX= "ARG_USE_DROPBOX";

    private static final String FRAGMENT_ERROR_DIALOG = "FRAGMENT_ERROR_DIALOG";

    private static final int REQUEST_RESOLVE_ERROR = 9000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 9001;

    @Inject DropboxApiConnection dropboxApiConnection;

    private static DbxClientV2 client;
    private String uniqueClientId;
    private String accessToken;
    private String uniqueClientSecret;
    private boolean connectWhenPossible = false;
    private Activity activity;


    @Override public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences("dropbox-expenseter", MODE_PRIVATE);
        accessToken = prefs.getString("access-token", null);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                prefs.edit().putString("access-token", accessToken).apply();
                init();
            }
        } else {
            init();
        }

      //  Auth.startOAuth2Authentication(getActivity(),uniqueClientId); // Auth.getUid();
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        init();
        if(connectWhenPossible){
            connect(getActivity());
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
      client = null;
    }





    public void connect(Activity activity) {
        if (getActivity() == null || client == null) {
            this.activity = activity;
            connectWhenPossible = true;
            SharedPreferences prefs = activity.getSharedPreferences("dropbox-expenseter", MODE_PRIVATE);
            accessToken = prefs.getString("access-token", null);
            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token();
                if (accessToken != null) {
                    prefs.edit().putString("access-token", accessToken).apply();
                    init();
                }
            } else {
                init();
            }

            return;
        }
    }


    private void init() {
        //Get arguments
        if(client == null) {
            OkHttpClient okHttpClient = new OkHttpClient();

           OkHttp3Requestor requestor =  new OkHttp3Requestor(okHttpClient);

            final DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("dropbox/expenseter")
                    .withHttpRequestor(requestor)
                    .build();

            client = new DbxClientV2(requestConfig, accessToken);
        }
    }




    protected boolean hasToken() {
        SharedPreferences prefs = getActivity().getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        return accessToken != null;
    }


    public static DbxClientV2 getClient() {
        if (client == null) {
            throw new IllegalStateException("Client not initialized.");
        }
        return client;
    }

}

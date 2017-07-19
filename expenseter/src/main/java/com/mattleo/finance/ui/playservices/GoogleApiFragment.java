package com.mattleo.finance.ui.playservices;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.mattleo.finance.ui.BaseFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;

import javax.inject.Inject;

public class GoogleApiFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String ARG_UNIQUE_CLIENT_ID = "ARG_UNIQUE_CLIENT_ID";
    private static final String ARG_USE_PLUS = "ARG_USE_PLUS";
    private static final String ARG_USE_DRIVE = "ARG_USE_DRIVE";

    private static final String FRAGMENT_ERROR_DIALOG = "FRAGMENT_ERROR_DIALOG";

    private static final int REQUEST_RESOLVE_ERROR = 9000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 9001;

    @Inject GoogleApiConnection googleApiConnection;

    private GoogleApiClient client;
    private String uniqueClientId;
    private boolean connectWhenPossible = false;

    public static Builder with(String uniqueClientId) {
        return new Builder(uniqueClientId);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        buildGoogleApiClient();
        if (connectWhenPossible) {
            connect();
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override public void onConnected(Bundle bundle) {
        sendEventConnected();
    }

    @Override public void onConnectionSuspended(int cause) {
        sendEventSuspended();
    }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
        sendEventFailed();
        if (connectionResult.hasResolution()) {
            try {
                if (getActivity() == null) {
                    return;
                }
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                client.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    public boolean handleOnActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {
                connect();
            }
            return true;
        }

        return false;
    }

    public void connect() {
        if (getActivity() == null || client == null) {
            connectWhenPossible = true;
            return;
        }

        connectWhenPossible = false;
        if (checkGooglePlayServicesAvailable()) {
            if (client.isConnected() || client.isConnecting()) {
                sendEventConnected();
            } else {
                client.connect();
            }
        }
    }

    public void disconnect() {
        googleApiConnection.remove(uniqueClientId);
        connectWhenPossible = false;

        if (client != null && (client.isConnected() || client.isConnecting())) {
            client.disconnect();
        } else {
            sendEventSuspended();
        }
    }

    public GoogleApiClient getClient() {
        return client;
    }

    protected void sendEventConnected() {
        googleApiConnection.put(uniqueClientId, client);
        getEventBus().post(new GoogleApiConnectedEvent(client, uniqueClientId));

    }

    protected void sendEventSuspended() {
        googleApiConnection.remove(uniqueClientId);
        getEventBus().post(new GoogleApiSuspendedEvent(client, uniqueClientId));
    }

    protected void sendEventFailed() {
        googleApiConnection.remove(uniqueClientId);
        getEventBus().post(new GoogleApiFailedEvent(client, uniqueClientId));
    }

    private void buildGoogleApiClient() {
        // Get arguments
        final Bundle args = getArguments();
        final boolean usePlus = args.getBoolean(ARG_USE_PLUS, false);
        final boolean useDrive = args.getBoolean(ARG_USE_DRIVE, false);
        uniqueClientId = args.getString(ARG_UNIQUE_CLIENT_ID);

        // Init a client
        final GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getActivity(), this, this);

        if (usePlus) {
            builder.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN);
        }

        if (useDrive) {
            builder.addApi(Drive.API).addScope(Drive.SCOPE_FILE);
        }

        client = builder.build();
    }

    private boolean checkGooglePlayServicesAvailable() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            //noinspection StatementWithEmptyBody
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), REQUEST_GOOGLE_PLAY_SERVICES).show();
            } else {
                // TODO Notify user that device is not supported
            }
            return false;
        }
        return true;
    }

    private void showErrorDialog(int errorCode) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Get the error dialog fromInt Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), REQUEST_GOOGLE_PLAY_SERVICES);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), FRAGMENT_ERROR_DIALOG);
        }
    }

    public static class Builder {
        private final String uniqueClientId;
        boolean usePlus;
        boolean useDrive;

        public Builder(String uniqueClientId) {
            this.uniqueClientId = uniqueClientId;
        }

        public Builder setUsePlus(boolean usePlus) {
            this.usePlus = usePlus;
            return this;
        }

        public Builder setUseDrive(boolean useDrive) {
            this.useDrive = useDrive;
            return this;
        }

        public GoogleApiFragment build() {
            Bundle args = new Bundle();
            args.putString(ARG_UNIQUE_CLIENT_ID, uniqueClientId);
            args.putBoolean(ARG_USE_PLUS, usePlus);
            args.putBoolean(ARG_USE_DRIVE, useDrive);

            GoogleApiFragment fragment = new GoogleApiFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }

    public static abstract class GoogleApiEvent {
        private final GoogleApiClient client;
        private final String uniqueClientId;

        public GoogleApiEvent(GoogleApiClient client, String uniqueClientId) {
            this.client = client;
            this.uniqueClientId = uniqueClientId;
        }

        public GoogleApiClient getClient() {
            return client;
        }

        public String getUniqueClientId() {
            return uniqueClientId;
        }
    }

    public static class GoogleApiConnectedEvent extends GoogleApiEvent {
        public GoogleApiConnectedEvent(GoogleApiClient client, String uniqueClientId) {
            super(client, uniqueClientId);
        }
    }

    public static class GoogleApiSuspendedEvent extends GoogleApiEvent {
        public GoogleApiSuspendedEvent(GoogleApiClient client, String uniqueClientId) {
            super(client, uniqueClientId);
        }
    }

    public static class GoogleApiFailedEvent extends GoogleApiEvent {
        public GoogleApiFailedEvent(GoogleApiClient client, String uniqueClientId) {
            super(client, uniqueClientId);
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog dialog;

        public ErrorDialogFragment() {
            super();
            dialog = null;
        }

        public void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
            return dialog;
        }
    }
}

package com.mattleo.finance.ui.dropbox;

import com.mattleo.finance.utils.EventBus;
import com.dropbox.core.v2.DbxClientV2;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Produce;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthieu on 7/8/2017.
 */

public class DropboxApiConnection {
    private final EventBus eventBus;
    private final Map<String, DbxClientV2> dropboxApiClients = new HashMap<>();

    public DropboxApiConnection(EventBus eventbus) {
        this.eventBus = eventbus;
        eventbus.register(this);
    }

    @Produce
    public DropboxApiConnection produceDropboxApiConnection() {
        return dropboxApiClients.size() > 0 ? this: null;
    }

    public void put(String clientUniqueId, DbxClientV2 client){
        if(client != null) {
            dropboxApiClients.put(clientUniqueId,client);
            eventBus.post(this);
        }
    }

    public void remove(String clientUniqueId) {
         dropboxApiClients.remove(clientUniqueId);
    }

    public DbxClientV2 get(String clientUniqueId) {
        return dropboxApiClients.get(clientUniqueId);
    }

    public boolean contains(String clientUniqueId) {
        return dropboxApiClients.containsKey(clientUniqueId);
    }
}

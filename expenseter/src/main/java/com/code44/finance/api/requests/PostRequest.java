package com.mattleo.finance.api.requests;

import com.mattleo.finance.api.GcmRegistration;
import com.mattleo.finance.api.Request;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.utils.EventBus;
import com.google.api.client.json.GenericJson;

public abstract class PostRequest<T extends GenericJson> extends Request {
    private final GcmRegistration gcmRegistration;

    public PostRequest(EventBus eventBus, GcmRegistration gcmRegistration) {
        super(eventBus);

        Preconditions.notNull(gcmRegistration, "GCM registration cannot be null.");

        this.gcmRegistration = gcmRegistration;
    }

    @Override protected Object performRequest() throws Exception {
        T body = createBody();
        body.set("deviceRegId", gcmRegistration.getRegistrationId());
        onAddPostData(body);
        if (!isPostDataEmpty(body)) {
            performRequest(body);
        }
        return null;
    }

    protected abstract T createBody();

    protected abstract void onAddPostData(T body);

    protected abstract boolean isPostDataEmpty(T body);

    protected abstract void performRequest(T body) throws Exception;
}

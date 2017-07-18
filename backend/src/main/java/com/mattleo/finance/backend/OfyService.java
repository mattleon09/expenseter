package com.mattleo.finance.backend;

import com.mattleo.finance.backend.entity.CurrencyEntity;
import com.mattleo.finance.backend.entity.DeviceEntity;
import com.mattleo.finance.backend.entity.UserAccount;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {

    static {
        ObjectifyService.register(UserAccount.class);
        ObjectifyService.register(DeviceEntity.class);
        ObjectifyService.register(CurrencyEntity.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}

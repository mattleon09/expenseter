package com.mattleo.finance.backend.endpoint;

import com.mattleo.finance.backend.endpoint.body.CurrenciesBody;
import com.mattleo.finance.backend.entity.CurrencyEntity;
import com.mattleo.finance.backend.entity.UserAccount;
import com.mattleo.finance.backend.utils.EndpointUtils;
import com.mattleo.finance.common.Constants;
import com.google.api.server.spi.Constant;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.mattleo.finance.backend.OfyService;

import java.io.IOException;
import java.util.List;

import javax.inject.Named;

@Api(
        name = "currencies",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constant.API_EXPLORER_CLIENT_ID, Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE},
        namespace = @ApiNamespace(
                ownerDomain = "endpoint.backend.finance.mattleo.com",
                ownerName = "endpoint.backend.finance.mattleo.com",
                packagePath = ""
        )
)
public class CurrenciesEndpoint {
    @ApiMethod(name = "list", httpMethod = "GET", path = "")
    public CollectionResponse<CurrencyEntity> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);
        final List<CurrencyEntity> currencies = OfyService.ofy()
                .load()
                .type(CurrencyEntity.class)
                .filter("userAccount", Key.create(UserAccount.class, userAccount.getId()))
                .filter("editTimestamp >=", timestamp)
                .list();

        return CollectionResponse.<CurrencyEntity>builder().setItems(currencies).build();
    }

    @ApiMethod(name = "save", httpMethod = "POST", path = "")
    public void save(CurrenciesBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);
        final Key<UserAccount> key = Key.create(UserAccount.class, userAccount.getId());
        final List<CurrencyEntity> currencies = body.getCurrencies();

        final Objectify ofy = OfyService.ofy();
        for (CurrencyEntity currency : currencies) {
            if (CurrencyEntity.find(currency.getId()) == null) {
                currency.onCreate();
            } else {
                currency.onUpdate();
            }
            currency.setUserAccount(key);
        }
        ofy.save().entities(currencies).now();

        EndpointUtils.notifyOtherDevices(userAccount, body.getDeviceRegId());
    }
}
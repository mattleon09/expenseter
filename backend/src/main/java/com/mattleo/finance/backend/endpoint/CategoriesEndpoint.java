package com.mattleo.finance.backend.endpoint;

import com.mattleo.finance.backend.endpoint.body.CategoriesBody;
import com.mattleo.finance.backend.entity.CategoryEntity;
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
        name = "categories",
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
public class CategoriesEndpoint {
    @ApiMethod(name = "list", httpMethod = "GET", path = "")
    public CollectionResponse<CategoryEntity> list(@Named("timestamp") long timestamp, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException {
        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);
        final List<CategoryEntity> categories = OfyService.ofy()
                .load()
                .type(CategoryEntity.class)
                .filter("userAccount", Key.create(UserAccount.class, userAccount.getId()))
                .filter("editTimestamp >=", timestamp)
                .list();

        return CollectionResponse.<CategoryEntity>builder().setItems(categories).build();
    }

    @ApiMethod(name = "save", httpMethod = "POST", path = "")
    public void save(CategoriesBody body, User user) throws BadRequestException, OAuthRequestException, ForbiddenException, NotFoundException, IOException {
        final UserAccount userAccount = EndpointUtils.getUserAccountAndVerifyPermissions(user);
        final Key<UserAccount> key = Key.create(UserAccount.class, userAccount.getId());
        final List<CategoryEntity> categories = body.getCategories();

        final Objectify ofy = OfyService.ofy();
        for (CategoryEntity category : categories) {
            if (CategoryEntity.find(category.getId()) == null) {
                category.onCreate();
            } else {
                category.onUpdate();
            }
            category.setUserAccount(key);
        }
        ofy.save().entities(categories).now();

        EndpointUtils.notifyOtherDevices(userAccount, body.getDeviceRegId());
    }
}
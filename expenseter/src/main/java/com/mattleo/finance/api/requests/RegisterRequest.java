package com.mattleo.finance.api.requests;

import android.content.Context;

import com.mattleo.finance.api.Request;
import com.mattleo.finance.api.User;
import com.mattleo.finance.backend.endpoint.users.Users;
import com.mattleo.finance.backend.endpoint.users.model.RegisterBody;
import com.mattleo.finance.backend.endpoint.users.model.UserAccount;
import com.mattleo.finance.common.utils.Preconditions;
import com.mattleo.finance.data.db.DBHelper;
import com.mattleo.finance.services.StartupService;
import com.mattleo.finance.utils.EventBus;

public class RegisterRequest extends Request {
    private final Context context;
    private final Users usersService;
    private final User user;
    private final DBHelper dbHelper;
    private final RegisterBody body;

    public RegisterRequest(EventBus eventBus, Context context, Users usersService, User user, DBHelper dbHelper, String email, String googleId, String firstName, String lastName, String photoUrl, String coverUrl) {
        super(eventBus);

        Preconditions.notNull(eventBus, "EventBus cannot be null.");
        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(usersService, "Users cannot be null.");
        Preconditions.notNull(dbHelper, "DBHelper cannot be null.");
        Preconditions.notEmpty(email, "Email cannot be empty.");
        Preconditions.notEmpty(googleId, "Google Id cannot be empty.");
        Preconditions.notEmpty(firstName, "First name cannot be empty.");

        this.context = context;
        this.usersService = usersService;
        this.user = user;
        this.dbHelper = dbHelper;

        user.setEmail(email);

        body = new RegisterBody();
        body.setGoogleId(googleId);
        body.setFirstName(firstName);
        body.setLastName(lastName);
        body.setPhotoUrl(photoUrl);
        body.setCoverUrl(coverUrl);
    }

    @Override protected Object performRequest() throws Exception {
        try {
            final UserAccount userAccount = usersService.register(body).execute();

            user.setId(userAccount.getId());
            user.setEmail(userAccount.getEmail());
            user.setGoogleId(userAccount.getGoogleId());
            user.setFirstName(userAccount.getFirstName());
            user.setLastName(userAccount.getLastName());
            user.setPhotoUrl(userAccount.getPhotoUrl());
            user.setCoverUrl(userAccount.getCoverUrl());
            user.setPremium(userAccount.getPremium());

            final boolean isExistingUser = !userAccount.getCreateTs().equals(userAccount.getEditTs());
            if (isExistingUser) {
                dbHelper.clear();
            }
            StartupService.start(context);
        } catch (Exception e) {
            e.printStackTrace();
            user.clear();
            throw e;
        }
        user.notifyChanged();
        return null;
    }
}

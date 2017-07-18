package com.mattleo.finance.backend.entity;

import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.mattleo.finance.backend.OfyService;

@Entity
public class UserAccount extends BaseEntity {
    @Index
    @ApiResourceProperty(name = "email")
    private String email;

    @ApiResourceProperty(name = "google_id")
    private String googleId;

    @ApiResourceProperty(name = "photo_url")
    private String photoUrl;

    @ApiResourceProperty(name = "cover_url")
    private String coverUrl;

    @ApiResourceProperty(name = "first_name")
    private String firstName;

    @ApiResourceProperty(name = "last_name")
    private String lastName;

    @ApiResourceProperty(name = "is_premium")
    private boolean isPremium;

    public static UserAccount find(User user) {
        return OfyService.ofy().load().type(UserAccount.class).filter("email", user.getEmail()).first().now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }
}

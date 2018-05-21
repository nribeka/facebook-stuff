package com.meh.stuff.facebook.model;

import com.google.gson.Gson;

public class Profile {

    private String id;
    private String username;

    public Profile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static Profile of(String profilePayload) {
        Gson gson = new Gson();
        return gson.fromJson(profilePayload, Profile.class);
    }
}

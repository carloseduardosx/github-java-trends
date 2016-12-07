package com.carloseduardo.github.data.model;

import com.google.gson.annotations.SerializedName;

import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToOne;

@Entity(stateless = true)
abstract class BaseOwner {

    @Key
    int id;

    @SerializedName("avatar_url")
    String avatarUrl;

    String login;

    @OneToOne(mappedBy = "owner")
    Repository repository;

    @OneToOne(mappedBy = "user")
    Pull pull;
}
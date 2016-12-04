package com.carloseduardo.github.data.model;

import com.google.gson.annotations.SerializedName;

import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToOne;

@Entity
abstract class BaseOwner {

    @Key
    int id;

    @SerializedName("avatar_url")
    String avatarUrl;

    String login;

    @OneToOne()
    Repository repository;
}

package com.carloseduardo.github.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToOne;

@Entity
abstract class BasePull {

    @Key
    int id;

    @SerializedName("html_url")
    String htmlUrl;

    String title;

    @SerializedName("created_at")
    Date createdAt;

    String body;

    @SerializedName("user")
    @ForeignKey
    @OneToOne
    Owner owner;

    @Column(nullable = false, foreignKey = @ForeignKey)
    @ManyToOne(cascade = CascadeAction.NONE)
    Repository repository;
}
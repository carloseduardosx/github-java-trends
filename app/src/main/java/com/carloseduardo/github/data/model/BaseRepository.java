package com.carloseduardo.github.data.model;

import com.google.gson.annotations.SerializedName;

import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToOne;

@Entity
abstract class BaseRepository {

    @Key
    int id;

    String name;

    @SerializedName("full_name")
    String fullName;

    String description;

    @SerializedName("stargazers_count")
    int stargazersCount;

    @SerializedName("forks_count")
    int forksCount;

    @Column(nullable = false, foreignKey = @ForeignKey)
    @ManyToOne(cascade = CascadeAction.NONE)
    RepositoriesContainer repositoriesContainer;

    @ForeignKey
    @OneToOne
    Owner owner;
}
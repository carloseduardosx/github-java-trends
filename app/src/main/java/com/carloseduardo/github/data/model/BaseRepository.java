package com.carloseduardo.github.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToMany;
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

    @SerializedName("pulls_url")
    String pullsUrl;

    @Column(nullable = false, foreignKey = @ForeignKey)
    @ManyToOne(cascade = CascadeAction.NONE)
    RepositoriesContainer repositoriesContainer;

    @ForeignKey
    @OneToOne
    Owner owner;

    @OneToMany(mappedBy = "repository", cascade = {CascadeAction.SAVE, CascadeAction.DELETE})
    List<Pull> pulls;
}
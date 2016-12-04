package com.carloseduardo.github.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.requery.CascadeAction;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.OneToMany;

@Entity
abstract class BaseRepositoriesContainer {

    @Key
    @Generated
    int id;

    @SerializedName("total_count")
    int totalCount;

    @SerializedName("incomplete_results")
    boolean incompleteResults;

    @SerializedName("items")
    @OneToMany(mappedBy = "repositoriesContainer", cascade = {CascadeAction.SAVE, CascadeAction.DELETE})
    List<Repository> repositories;
}
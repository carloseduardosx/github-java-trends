package com.carloseduardo.github.data.model;

import com.google.gson.annotations.SerializedName;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

@Entity
abstract class BaseRepositoriesContainer {

    @Key
    @Generated
    int id;

    @SerializedName("total_count")
    int totalCount;
}
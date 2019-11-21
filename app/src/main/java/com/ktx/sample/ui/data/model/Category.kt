package com.ktx.sample.ui.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * ---------------------------
 * Created by [J. An Android Lover] on 2019-11-16
 * Screen Name:
 * TODO: <Add a class header comment!>
 * ---------------------------
 */
data class Category(
    @Expose
    @SerializedName("_id")
    val _id: String,
    @Expose
    @SerializedName("kind")
    var kind: Int,
    @Expose
    @SerializedName("title")
    val title: String,
    @Expose
    @SerializedName("description")
    val description: String,
    @Expose
    @SerializedName("createdAt")
    val createdAt: String,
    @Expose
    @SerializedName("updatedAt")
    val updatedAt: String,
    @Expose
    @SerializedName("__v")
    var __v: Int
)
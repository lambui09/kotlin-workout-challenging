package com.ktx.sample.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * ---------------------------
 * Created by [J. An Android Lover] on 2019-11-16
 * Screen Name:
 * TODO: <Add a class header comment!>
 * ---------------------------
 */
data class Exercise(
    @Expose
    @SerializedName("_id")
    val id: String? = null,
    @Expose
    @SerializedName("fields")
    val fields: List<String>? = null,
    @Expose
    @SerializedName("picture")
    val picture: List<String>? = null,
    @Expose
    @SerializedName("instructions")
    val instructions: List<String>? = null,
    @Expose
    @SerializedName("popularity")
    val popularity: Int? = null,
    @Expose
    @SerializedName("category")
    val category: Category? = null,
    @Expose
    @SerializedName("body_part")
    val bodyPart: String,
    @Expose
    @SerializedName("title")
    val title: String,
    @Expose
    @SerializedName("image")
    val image: String,
    @Expose
    @SerializedName("link")
    val link: String,
    @Expose
    @SerializedName("__v")
    var __v: Int,
    @Expose
    @SerializedName("last_used")
    val lastUsed: String,
    @Expose
    @SerializedName("category_type")
    val categoryType: String,
    @Expose
    @SerializedName("author")
    val author: String,
    @Expose
    @SerializedName("share")
    var share: Int,
    @Expose
    @SerializedName("category_type_name")
    val categoryTypeName: String,
    @Expose
    @SerializedName("binding_system_exercise")
    val bindingSystemExercise: String,
    @Expose
    @SerializedName("author_name")
    val authorName: String,
    @Expose
    @SerializedName("createdAt")
    val createdAt: String,
    @Expose
    @SerializedName("updatedAt")
    val updatedAt: String
)
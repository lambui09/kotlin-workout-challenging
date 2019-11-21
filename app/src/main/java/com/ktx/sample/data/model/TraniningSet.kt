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
data class TrainingSet(
    @Expose
    @SerializedName("weight")
    val weight: Weight? = null,
    @Expose
    @SerializedName("rest")
    val rest: Rest? = null,
    @Expose
    @SerializedName("reps")
    val reps: Reps? = null,
    @Expose
    @SerializedName("is_completed")
    val isCompleted: Boolean? = null,
    @Expose
    @SerializedName("_id")
    val _id: String? = null
)

data class Weight(
    @Expose
    @SerializedName("value")
    val value: Float? = null
)

data class Rest(
    @Expose
    @SerializedName("value")
    val value: Float? = null
)

data class Reps(
    @Expose
    @SerializedName("value")
    val value: Float? = null
)
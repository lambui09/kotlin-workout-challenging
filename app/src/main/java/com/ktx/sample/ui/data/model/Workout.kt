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
data class Workout(
    @Expose
    @SerializedName("exercise")
    val exercise: Exercise? = null,
    @Expose
    @SerializedName("training_sets")
    val trainingSets: List<TrainingSet>? = null
)
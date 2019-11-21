package com.ktx.sample.views

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ktx.sample.data.model.Workout
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.io.InputStream


class WorkoutViewModel : ViewModel() {

    var exerciseDataResponse = MutableLiveData<Workout>()

    fun getExercise(context: Context, fileName: String) {
        val disposable = Single.just(loadJSONFromAsset(context, fileName))
            .map {
                return@map it
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                val exerciseData = Gson().fromJson<Workout>(it, Workout::class.java)
                exerciseData?.let { workout ->
                    exerciseDataResponse.value = workout
                }
            }
    }

    private fun loadJSONFromAsset(context: Context, fileName: String): String? {
        val json: String?
        json = try {
            val stream: InputStream = context.assets.open(fileName)
            val size: Int = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}

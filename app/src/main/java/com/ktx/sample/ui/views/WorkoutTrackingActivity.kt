package com.ktx.sample.ui.views

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.ktx.sample.R
import com.ktx.sample.ui.data.model.TrainingSet
import com.ktx.sample.ui.data.model.Workout
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.main_view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class WorkoutTrackingActivity : AppCompatActivity() {

    private var trainingSetAdapter: TrainingSetAdapter? = null
    private var sheetBehavior: BottomSheetBehavior<*>? = null
    private var threadTimer: Thread? = null
    private var trainingTime = INIT_TIME
    private var totalDuration = 0L

    private val viewModel: WorkoutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workout_tracking_activity)

        this.initBottomSheet()
        this.initActions()
        this.onSubscriber()

        // Loading data from json file
        this.viewModel.getExercise(this, JSON_FILE_NAME)
    }

    private fun onSubscriber() {
        with(viewModel) {
            exerciseDataResponse.observe(this@WorkoutTrackingActivity, Observer {
                // Load data on list here!!
                showingData(it)
            })
        }
    }

    private fun initActions() {
        btnBack.setOnClickListener {
            // TODO back action
        }

        btnTimer.setOnClickListener {
            // TODO set time to play
        }

        btnPlay.setOnClickListener {
            this.openTrainingSet()
        }

        btnClose.setOnClickListener {
            sheetBehavior?.isHideable = true
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            viewMiniTimber.visibility = View.VISIBLE
        }

        btnDecrease.setOnClickListener {
            this.decreaseTimeClicked()
        }

        btnIncrease.setOnClickListener {
            this.increaseTimeClicked()
        }

        btnSkipTimber.setOnClickListener {
            sheetBehavior?.isHideable = true
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            viewMiniTimber.visibility = View.GONE
            this.stopTimer()
        }

        viewMiniTimber.setOnClickListener {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onSlide(view: View, newState: Float) {
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        sheetBehavior?.isHideable = true
                        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                        viewMiniTimber.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })
        sheetBehavior?.isHideable = true
        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showingData(workout: Workout) {
        with(workout) {
            // Loading exercise photo
            loadImage(exercise?.image, ivPhoto)

            // Title
            tvSetTitle.text = exercise?.title

            // Showing training sets on views
            if (trainingSets == null) return
            loadTrainingSets(trainingSets)
        }
    }

    private fun loadImage(url: String?, view: ImageView) {
        if (url.isNullOrEmpty()) return

        val options = RequestOptions()
            .priority(Priority.HIGH)
            .dontTransform()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

        Glide.with(this).load(url)
            .apply(options)
            .into(view)
    }

    private fun loadTrainingSets(trainingSets: List<TrainingSet>) {
        rvSets.layoutManager = LinearLayoutManager(this)

        // init adapter
        trainingSetAdapter = TrainingSetAdapter(this, trainingSets)

        rvSets.adapter = trainingSetAdapter
        trainingSetAdapter?.notifyDataSetChanged()
    }

    private fun initProgress() {
        pbTimer.startingDegree = 270
        pbTimer.innerBackgroundColor = Color.WHITE
        pbTimer.unfinishedStrokeColor = ContextCompat.getColor(this, R.color.colorApp)
        pbTimer.finishedStrokeColor = ContextCompat.getColor(this, R.color.colorDeliver)
        pbTimer.finishedStrokeWidth = 20f
        pbTimer.unfinishedStrokeWidth = 20f
        pbTimer.isShowText = false
        pbTimer.progress = 0f
        tvTimber.text = "00:00"

        // Init mini timer
        pbMiniTimber.startingDegree = 270
        pbMiniTimber.innerBackgroundColor = Color.WHITE
        pbMiniTimber.unfinishedStrokeColor = ContextCompat.getColor(this, R.color.colorApp)
        pbMiniTimber.finishedStrokeColor = ContextCompat.getColor(this, R.color.colorDeliver)
        pbMiniTimber.finishedStrokeWidth = 14f
        pbMiniTimber.unfinishedStrokeWidth = 14f
        pbMiniTimber.isShowText = false
        pbMiniTimber.progress = 0f
        tvMiniTimber.text = "00:00"
    }

    private fun openTrainingSet() {
        this.loadTrainingSetForRest()
        this.initProgress()
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        this.startTimer()
    }

    private fun decreaseTimeClicked() {
        if (trainingTime <= 30) return

        trainingTime -= THIRTY_SECONDS
        pbTimer.max = (trainingTime / ONE_SECOND).toInt()
        pbMiniTimber.max = (trainingTime / ONE_SECOND).toInt()
    }

    private fun increaseTimeClicked() {
        trainingTime += THIRTY_SECONDS
        pbTimer.max = (trainingTime / ONE_SECOND).toInt()
        pbMiniTimber.max = (trainingTime / ONE_SECOND).toInt()
    }

    private fun loadTrainingSetForRest() {
        val workout = viewModel.exerciseDataResponse.value

        workout?.trainingSets?.forEach {
            if (it.isCompleted == true) return

            loadImage(workout.exercise?.image, setPhoto)
            tvName.text = workout.exercise?.title

            tvWeight.text = String.format("%dlb", it.weight?.value?.toInt())

            return@forEach
        }
    }

    private fun stopTimer() {
        this.threadTimer?.interrupt()
    }

    private fun setTotalDuration(totalDuration: Long) {
        val minutes = TimeUnit.SECONDS.toMinutes(totalDuration) % TimeUnit.HOURS.toMinutes(1)
        val seconds = TimeUnit.SECONDS.toSeconds(totalDuration) % TimeUnit.MINUTES.toSeconds(1)
        val totalDisplay = String.format("Total Duration: %02d:%02d", minutes, seconds)

        val spannableContent = SpannableString(totalDisplay)
        val style = StyleSpan(Typeface.BOLD)
        val color = ForegroundColorSpan(Color.WHITE)

        spannableContent.setSpan(
            color,
            totalDisplay.length - 5,
            totalDisplay.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableContent.setSpan(
            style,
            totalDisplay.length - 5,
            totalDisplay.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        tvTitle.text = spannableContent
    }


    private fun startTimer() {
        trainingTime = INIT_TIME
        pbTimer.max = (trainingTime / ONE_SECOND).toInt()
        pbMiniTimber.max = (trainingTime / ONE_SECOND).toInt()

        threadTimer = object: Thread() {
            override fun run() {
               try {
                   while (!isInterrupted) {
                       sleep(ONE_SECOND.toLong())
                       runOnUiThread {
                           trainingTime -= ONE_SECOND

                           if (trainingTime <= 0) {
                               sheetBehavior?.isHideable = true
                               sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                               viewMiniTimber.visibility = View.GONE
                               interrupt()
                               return@runOnUiThread
                           }

                           val minutes = TimeUnit.MILLISECONDS.toMinutes(trainingTime) % TimeUnit.HOURS.toMinutes(1)
                           val seconds = TimeUnit.MILLISECONDS.toSeconds(trainingTime) % TimeUnit.MINUTES.toSeconds(1)

                           tvTimber.text = String.format("%02d:%02d", minutes, seconds)
                           pbTimer.progress = pbTimer.progress + 1

                           // set for mini timer
                           tvMiniTimber.text = String.format("%02d:%02d", minutes, seconds)
                           pbMiniTimber.progress = pbTimer.progress + 1
                           totalDuration += 1
                           setTotalDuration(totalDuration)
                       }
                   }
               } catch (e: InterruptedException) {}
            }
        }

        threadTimer?.start()
    }

    companion object {
        const val JSON_FILE_NAME = "exercise_data.json"
        const val INIT_TIME = 60000L
        const val ONE_SECOND = 1000
        const val THIRTY_SECONDS = 30 * ONE_SECOND
    }
}

package com.ktx.sample.views

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.ktx.sample.R
import com.ktx.sample.data.model.TrainingSet
import com.ktx.sample.data.model.Workout
import com.ktx.sample.utils.makeStatusBarTransparent
import com.ktx.sample.utils.setMarginTop
import kotlinx.android.synthetic.main.view_rest_timer.*
import kotlinx.android.synthetic.main.view_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class WorkoutTrackingActivity : AppCompatActivity() {

    private val viewModel: WorkoutViewModel by viewModel()

    private var trainingSetAdapter: TrainingSetAdapter? = null
    private var sheetBehavior: BottomSheetBehavior<*>? = null
    private var trainingTime = INIT_TIME
    private var totalDuration = 0L
    private var handler = Handler()
    private var isStartWorkout = false

    private var runnable = Runnable {
        updateRemainingTime(trainingTime)

        // -1 second
        trainingTime -= ONE_SECOND

        // If time over, just hide and stop
        if (trainingTime <= 0) {
            sheetBehavior?.isHideable = true
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            viewMiniTimber.visibility = View.GONE
            this.stopTrainingTimer()
            return@Runnable
        }

        pbTimer.progress = pbTimer.progress + 1
        pbMiniTimber.progress = pbTimer.progress + 1

        totalDuration += 1
        setTotalDuration(totalDuration)

        // Post new
        this.startTrainingTimer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workout_tracking_activity)

        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content_container)) { _, insets ->
            topBar.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        this.initBottomSheet()
        this.initActions()
        this.onSubscriber()

        // Loading data from json file
        this.viewModel.getExercise(this, JSON_FILE_NAME)

        // Init total duration
        setTotalDuration(0)
    }

    private fun onSubscriber() {
        with(viewModel) {
            exerciseDataResponse.observe(this@WorkoutTrackingActivity, Observer {
                showingData(it)
            })
        }
    }

    override fun onPause() {
        this.stopTrainingTimer()
        super.onPause()
    }

    override fun onResume() {
        this.startTrainingTimer()
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        this.stopTrainingTimer()
    }

    private fun initActions() {
        btnBack.setOnClickListener {
            this.finish()
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
            this.stopTrainingTimer()
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
        tvTimber.text = "01:00"

        // Init mini timer
        pbMiniTimber.startingDegree = 270
        pbMiniTimber.innerBackgroundColor = Color.WHITE
        pbMiniTimber.unfinishedStrokeColor = ContextCompat.getColor(this, R.color.colorApp)
        pbMiniTimber.finishedStrokeColor = ContextCompat.getColor(this, R.color.colorDeliver)
        pbMiniTimber.finishedStrokeWidth = 14f
        pbMiniTimber.unfinishedStrokeWidth = 14f
        pbMiniTimber.isShowText = false
        pbMiniTimber.progress = 0f
        tvMiniTimber.text = "01:00"
    }

    private fun openTrainingSet() {
        this.isStartWorkout = true
        this.loadTrainingSetForRest()
        this.initProgress()
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        trainingTime = INIT_TIME
        pbTimer.max = (trainingTime / ONE_SECOND).toInt()
        pbMiniTimber.max = (trainingTime / ONE_SECOND).toInt()

        this.startTrainingTimer()
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
            // TODO if completed, done show next up
            if (it.isCompleted == true) return

            loadImage(workout.exercise?.image, setPhoto)
            tvName.text = workout.exercise?.title

            tvSetWeight.text = String.format("%dlb", it.weight?.value?.toInt())

            return@forEach
        }
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
            totalDisplay.length - TIME_LENGTH,
            totalDisplay.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableContent.setSpan(
            style,
            totalDisplay.length - TIME_LENGTH,
            totalDisplay.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        tvTitle.text = spannableContent
    }

    private fun updateRemainingTime(time: Long) {
        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)
        tvTimber.text = String.format("%02d:%02d", minutes, seconds)
        tvMiniTimber.text = String.format("%02d:%02d", minutes, seconds)
    }


    private fun startTrainingTimer() {
        if (!isStartWorkout) return

        runnable.let { handler.postDelayed(it, ONE_SECOND) }
    }

    private fun stopTrainingTimer() {
        runnable.let { handler.removeCallbacks(it) }
    }

    companion object {
        const val JSON_FILE_NAME = "exercise_data.json"
        const val INIT_TIME = 60000L
        const val ONE_SECOND = 1000L
        const val THIRTY_SECONDS = 30 * ONE_SECOND
        const val TIME_LENGTH = 5
    }
}

package com.ktx.sample.ui.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ktx.sample.R
import com.ktx.sample.ui.data.model.TrainingSet
import kotlinx.android.synthetic.main.item_set.view.*

/**
 * ---------------------------
 * Created by [J. An Android Lover] on 2019-11-16
 * Screen Name:
 * TODO: <Add a class header comment!>
 * ---------------------------
 */
class TrainingSetAdapter(private val context: Context, private val data: List<TrainingSet>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_set, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemIndex = position + 1
        (holder as ItemViewHolder).bindData(data[position], itemIndex)
    }

    companion object {
        class ItemViewHolder(view: View) : RecyclerView.ViewHolder(
            view
        ) {

            fun bindData(trainingSet: TrainingSet?, itemNo: Int) {
                trainingSet?.let {
                    with(itemView) {
                        tvSetNo.text = itemNo.toString()
                        tvWeight.text = String.format("%dlb", it.weight?.value?.toInt())
                        tvReps.text = String.format("%d reps", it.reps?.value?.toInt())
                    }
                }
            }
        }
    }
}
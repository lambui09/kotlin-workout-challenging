package com.ktx.sample.di

import com.ktx.sample.views.WorkoutViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * ---------------------------
 * Created by [J. An Android Lover] on 2019-11-16
 * Screen Name:
 * TODO: <Add a class header comment!>
 * ---------------------------
 */
val appModule = module {
    viewModel { WorkoutViewModel() }
}
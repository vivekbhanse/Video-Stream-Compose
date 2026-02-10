package com.reminder.myottapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StreamingApp  : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}
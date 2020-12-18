package com.skapps.android.csicodathonproject.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Syed Umair on 17/12/2020.
 */

const val CHANNEL_REVIEW_LISTENER = "channel_review_listener"
const val CHANNEL_REVIEW_METRIC = "channel_review_metric"

@HiltAndroidApp
class TheSystemApp: Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelReviewListener = NotificationChannel(
                CHANNEL_REVIEW_LISTENER,
                "Channel Reviews Listener",
                NotificationManager.IMPORTANCE_HIGH
            )
            channelReviewListener.description = "Spam reviews listener channel"

            val channelReviewsMetrics = NotificationChannel(
                CHANNEL_REVIEW_LISTENER,
                "Channel Reviews Metrics",
                NotificationManager.IMPORTANCE_HIGH
            )
            channelReviewsMetrics.description = "Reviews metrics channel"

            val manager = getSystemService(NotificationManager::class.java)
            @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            manager.createNotificationChannel(channelReviewListener)
            manager.createNotificationChannel(channelReviewsMetrics)
        }
    }

}
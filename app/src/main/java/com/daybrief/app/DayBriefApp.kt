package com.daybrief.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

/**
 * DAYBRIEF Application class.
 * Initializes Hilt dependency injection and notification channels.
 */
@HiltAndroidApp
class DayBriefApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    /**
     * Creates all notification channels required by the app.
     * Called once on app startup. Safe to call multiple times -
     * channels are only created if they don't already exist.
     */
    private fun createNotificationChannels() {
        val listeningChannel = NotificationChannel(
            CHANNEL_LISTENING,
            "DAYBRIEF Listening",
            NotificationManager.IMPORTANCE_LOW // Shows in status bar, no sound
        ).apply {
            description = "Shows when DAYBRIEF is actively listening"
            setShowBadge(false)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(listeningChannel)
    }

    companion object {
        /** Notification channel for the active listening foreground service */
        const val CHANNEL_LISTENING = "daybrief_listening"
    }
}

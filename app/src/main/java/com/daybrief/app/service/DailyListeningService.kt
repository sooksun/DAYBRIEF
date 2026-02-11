package com.daybrief.app.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import com.daybrief.app.notification.NotificationHelper

/**
 * Foreground Service that manages the active listening session.
 *
 * Privacy & compliance rules (.cursorrules):
 * - NEVER auto-starts: only started by explicit user action via UI
 * - ALWAYS shows a persistent notification while active
 * - User can stop / pause at any time via UI or notification action
 * - Does NOT record silently or in the background without notification
 *
 * Lifecycle:
 * 1. User taps "เริ่มงานวันนี้" -> startForegroundService() with ACTION_START
 * 2. Service calls startForeground() immediately (within 5 seconds)
 * 3. Persistent notification shown with pause/stop actions
 * 4. User can pause/resume via "พักการฟัง" / "ฟังต่อ"
 * 5. User taps "จบงานวันนี้" or notification stop -> ACTION_STOP -> stopSelf()
 *
 * Note: Audio capture pipeline (AudioRecorder, chunking, VAD) will be
 * implemented in Task Group C. This service currently manages only the
 * foreground lifecycle and notification.
 */
class DailyListeningService : Service() {

    companion object {
        private const val TAG = "DailyListeningService"

        /** Intent action to start the listening session */
        const val ACTION_START = "com.daybrief.app.action.START_LISTENING"

        /** Intent action to stop the listening session */
        const val ACTION_STOP = "com.daybrief.app.action.STOP_LISTENING"

        /** Intent action to pause the listening session */
        const val ACTION_PAUSE = "com.daybrief.app.action.PAUSE_LISTENING"

        /** Intent action to resume the listening session */
        const val ACTION_RESUME = "com.daybrief.app.action.RESUME_LISTENING"
    }

    /** Tracks whether the service is currently in the foreground */
    private var isListening = false

    /** Tracks whether listening is paused */
    private var isPaused = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startListening()
            ACTION_STOP -> stopListening()
            ACTION_PAUSE -> pauseListening()
            ACTION_RESUME -> resumeListening()
            else -> {
                Log.w(TAG, "Unknown action: ${intent?.action}")
            }
        }

        // Don't restart automatically - user must explicitly start
        return START_NOT_STICKY
    }

    /**
     * Starts the foreground listening session.
     * Must be called within 5 seconds of startForegroundService().
     */
    private fun startListening() {
        if (isListening) {
            Log.d(TAG, "Already listening, ignoring duplicate start")
            return
        }

        Log.i(TAG, "Starting listening session")

        val notification = NotificationHelper.createListeningNotification(this)

        // Start as foreground with microphone service type (required for Android 14+)
        startForeground(
            NotificationHelper.LISTENING_NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
        )

        isListening = true
        isPaused = false

        // TODO: Task Group C - Start AudioRecorder here
        // TODO: Task Group C - Begin audio chunking pipeline
    }

    /**
     * Pauses the listening session without stopping the foreground service.
     * Notification updates to show paused state.
     */
    private fun pauseListening() {
        if (!isListening || isPaused) return

        Log.i(TAG, "Pausing listening session")
        isPaused = true

        // Update notification to show paused state
        val notification = NotificationHelper.createPausedNotification(this)
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        notificationManager.notify(NotificationHelper.LISTENING_NOTIFICATION_ID, notification)

        // TODO: Task Group C - Pause AudioRecorder
    }

    /**
     * Resumes the listening session from paused state.
     * Notification updates back to active listening state.
     */
    private fun resumeListening() {
        if (!isListening || !isPaused) return

        Log.i(TAG, "Resuming listening session")
        isPaused = false

        // Update notification to show active listening state
        val notification = NotificationHelper.createListeningNotification(this)
        val notificationManager = getSystemService(android.app.NotificationManager::class.java)
        notificationManager.notify(NotificationHelper.LISTENING_NOTIFICATION_ID, notification)

        // TODO: Task Group C - Resume AudioRecorder
    }

    /**
     * Stops the listening session and shuts down the foreground service.
     * Can be triggered by:
     * - User tapping "จบงานวันนี้ & สรุปผล" in the app UI
     * - User tapping "หยุด" action on the notification
     */
    private fun stopListening() {
        if (!isListening) {
            Log.d(TAG, "Not listening, ignoring stop")
            stopSelf()
            return
        }

        Log.i(TAG, "Stopping listening session")

        // TODO: Task Group C - Stop AudioRecorder here
        // TODO: Task Group C - Flush any remaining audio chunks

        isListening = false
        isPaused = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Not a bound service
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service destroyed")
        isListening = false
        isPaused = false
    }
}

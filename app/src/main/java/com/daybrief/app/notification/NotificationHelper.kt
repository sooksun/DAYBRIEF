package com.daybrief.app.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.daybrief.app.DayBriefApp
import com.daybrief.app.MainActivity
import com.daybrief.app.R
import com.daybrief.app.service.DailyListeningService

/**
 * Helper class for creating and managing DAYBRIEF notifications.
 *
 * Privacy compliance:
 * - Notification is always visible when recording is active
 * - User can stop or pause recording directly from the notification
 * - No transcript or audio content is shown in notifications
 * - Thai language throughout (Figma-matched)
 */
object NotificationHelper {

    /** Unique ID for the listening foreground service notification */
    const val LISTENING_NOTIFICATION_ID = 1001

    /**
     * Creates the persistent notification shown during active listening.
     *
     * Title: "DAYBRIEF กำลังช่วยจำ"
     * Actions: พัก (pause), หยุด (stop)
     */
    fun createListeningNotification(context: Context): Notification {
        val openAppPendingIntent = createOpenAppIntent(context)
        val stopPendingIntent = createActionIntent(context, DailyListeningService.ACTION_STOP, 1)
        val pausePendingIntent = createActionIntent(context, DailyListeningService.ACTION_PAUSE, 2)

        return NotificationCompat.Builder(context, DayBriefApp.CHANNEL_LISTENING)
            .setContentTitle(context.getString(R.string.notification_listening_title))
            .setContentText(context.getString(R.string.notification_listening_text))
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_pause,
                context.getString(R.string.notification_action_pause),
                pausePendingIntent
            )
            .addAction(
                R.drawable.ic_stop,
                context.getString(R.string.notification_action_stop),
                stopPendingIntent
            )
            .build()
    }

    /**
     * Creates the notification shown when listening is paused.
     *
     * Title: "DAYBRIEF พักการฟัง"
     * Actions: ฟังต่อ (resume), หยุด (stop)
     */
    fun createPausedNotification(context: Context): Notification {
        val openAppPendingIntent = createOpenAppIntent(context)
        val stopPendingIntent = createActionIntent(context, DailyListeningService.ACTION_STOP, 1)
        val resumePendingIntent = createActionIntent(context, DailyListeningService.ACTION_RESUME, 3)

        return NotificationCompat.Builder(context, DayBriefApp.CHANNEL_LISTENING)
            .setContentTitle(context.getString(R.string.notification_paused_title))
            .setContentText(context.getString(R.string.notification_paused_text))
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_mic,
                context.getString(R.string.notification_action_resume),
                resumePendingIntent
            )
            .addAction(
                R.drawable.ic_stop,
                context.getString(R.string.notification_action_stop),
                stopPendingIntent
            )
            .build()
    }

    /** Creates PendingIntent to open the app when notification is tapped */
    private fun createOpenAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Creates PendingIntent for a service action (stop/pause/resume) */
    private fun createActionIntent(
        context: Context,
        action: String,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, DailyListeningService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

package com.daybrief.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.daybrief.app.viewmodel.ActionItem
import com.daybrief.app.viewmodel.DayEvent
import com.daybrief.app.viewmodel.PendingIssue

/**
 * Room entity for a daily brief.
 * One row per day — stores summary, action items, pending issues, events.
 */
@Entity(tableName = "daily_briefs")
data class DailyBriefEntity(
    @PrimaryKey
    val dateEpochDay: Long,

    val listeningDuration: String,
    val overviewSummary: String,
    val actionItems: List<ActionItem>,
    val pendingIssues: List<PendingIssue>,
    val events: List<DayEvent>,
    val createdAt: Long
)

package com.daybrief.app.data.db

import androidx.room.TypeConverter
import com.daybrief.app.viewmodel.ActionItem
import com.daybrief.app.viewmodel.DayEvent
import com.daybrief.app.viewmodel.PendingIssue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room TypeConverters for JSON columns.
 */
class Converters {

    private val gson = Gson()

    private val actionItemListType = object : TypeToken<List<ActionItem>>() {}.type
    private val pendingIssueListType = object : TypeToken<List<PendingIssue>>() {}.type
    private val dayEventListType = object : TypeToken<List<DayEvent>>() {}.type

    @TypeConverter
    fun fromActionItemList(value: List<ActionItem>): String = gson.toJson(value)

    @TypeConverter
    fun toActionItemList(value: String): List<ActionItem> =
        if (value.isBlank()) emptyList()
        else gson.fromJson(value, actionItemListType)

    @TypeConverter
    fun fromPendingIssueList(value: List<PendingIssue>): String = gson.toJson(value)

    @TypeConverter
    fun toPendingIssueList(value: String): List<PendingIssue> =
        if (value.isBlank()) emptyList()
        else gson.fromJson(value, pendingIssueListType)

    @TypeConverter
    fun fromDayEventList(value: List<DayEvent>): String = gson.toJson(value)

    @TypeConverter
    fun toDayEventList(value: String): List<DayEvent> =
        if (value.isBlank()) emptyList()
        else gson.fromJson(value, dayEventListType)
}

package com.daybrief.app.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daybrief.app.service.DailyListeningService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the listening session.
 */
data class ListeningUiState(
    /** Whether the service is currently active (listening or paused) */
    val isListening: Boolean = false,

    /** Whether listening is paused */
    val isPaused: Boolean = false,

    /** Scheduled start time - hour (24h format) */
    val startHour: Int = 8,

    /** Scheduled start time - minute */
    val startMinute: Int = 0,

    /** Scheduled end time - hour (24h format) */
    val endHour: Int = 18,

    /** Scheduled end time - minute */
    val endMinute: Int = 0,

    /** Elapsed time since listening started, in seconds */
    val elapsedSeconds: Long = 0,

    /** Number of events manually marked by the user */
    val eventCount: Int = 0
) {
    /** Formatted elapsed time as HH:MM:SS */
    val formattedElapsedTime: String
        get() {
            val hours = elapsedSeconds / 3600
            val minutes = (elapsedSeconds % 3600) / 60
            val seconds = elapsedSeconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
}

/**
 * ViewModel for managing the daily listening session.
 *
 * Responsibilities:
 * - Start / stop / pause / resume the DailyListeningService
 * - Track elapsed time (pauses when listening is paused)
 * - Manage schedule times
 * - Handle manual event marking
 *
 * Privacy compliance (.cursorrules):
 * - Never auto-starts recording
 * - All actions require explicit user interaction
 * - No transcript data exposed to UI during the day
 */
@HiltViewModel
class ListeningViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ListeningUiState())
    val uiState: StateFlow<ListeningUiState> = _uiState.asStateFlow()

    /** Timer coroutine job reference */
    private var timerJob: Job? = null

    /**
     * Starts the listening session.
     * Called only by explicit user action (tap "เริ่มงานวันนี้").
     */
    fun startListening() {
        if (_uiState.value.isListening) return

        sendServiceAction(DailyListeningService.ACTION_START)
        _uiState.update { it.copy(isListening = true, isPaused = false, elapsedSeconds = 0, eventCount = 0) }
        startTimer()
    }

    /**
     * Stops the listening session completely.
     * Called by user action ("จบงานวันนี้ & สรุปผล" or notification stop).
     */
    fun stopListening() {
        if (!_uiState.value.isListening) return

        sendServiceAction(DailyListeningService.ACTION_STOP)
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isListening = false, isPaused = false) }
    }

    /**
     * Pauses the listening session.
     * Called by user action ("พักการฟัง").
     * Timer stops incrementing, service notification updates.
     */
    fun pauseListening() {
        if (!_uiState.value.isListening || _uiState.value.isPaused) return

        sendServiceAction(DailyListeningService.ACTION_PAUSE)
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isPaused = true) }
    }

    /**
     * Resumes the listening session from paused state.
     * Called by user action ("ฟังต่อ").
     */
    fun resumeListening() {
        if (!_uiState.value.isListening || !_uiState.value.isPaused) return

        sendServiceAction(DailyListeningService.ACTION_RESUME)
        _uiState.update { it.copy(isPaused = false) }
        startTimer()
    }

    /**
     * Marks an event at the current point in time.
     * Used for manual event segmentation ("บันทึกเหตุการณ์สำคัญ").
     */
    fun markEvent() {
        if (!_uiState.value.isListening) return

        _uiState.update { it.copy(eventCount = it.eventCount + 1) }

        // TODO: Task Group E - Save event marker with timestamp
        // TODO: Task Group E - Trigger audio chunk boundary
    }

    /**
     * Updates the scheduled start time.
     */
    fun updateStartTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(startHour = hour, startMinute = minute) }
    }

    /**
     * Updates the scheduled end time.
     */
    fun updateEndTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(endHour = hour, endMinute = minute) }
    }

    /**
     * Sends an action intent to the DailyListeningService.
     */
    private fun sendServiceAction(action: String) {
        val intent = Intent(application, DailyListeningService::class.java).apply {
            this.action = action
        }
        if (action == DailyListeningService.ACTION_START) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
        }
    }

    /**
     * Starts an internal timer that updates elapsed time every second.
     * Timer only runs when actively listening (not paused).
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

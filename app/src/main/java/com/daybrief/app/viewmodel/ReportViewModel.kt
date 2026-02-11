package com.daybrief.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daybrief.app.data.repository.BriefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * UI state for the Report View screen.
 */
data class ReportViewUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val availableDates: List<LocalDate> = emptyList(),
    val showDatePicker: Boolean = false,
    val summary: SummaryUiState = SummaryUiState()
)

/**
 * ViewModel for viewing daily reports.
 * Loads from SQLite (Room). Seeds demo on first launch if DB empty.
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: BriefRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportViewUiState())
    val uiState: StateFlow<ReportViewUiState> = _uiState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("th"))

    init {
        viewModelScope.launch {
            repository.seedDemoIfEmpty()
        }
        viewModelScope.launch {
            repository.getAllDates().collect { dates ->
                val sortedDates = dates.sortedDescending()
                _uiState.update { state ->
                    val selected = state.selectedDate
                    val entity = repository.getBrief(selected)
                    state.copy(
                        availableDates = sortedDates,
                        summary = loadSummaryFromEntity(entity, selected)
                    )
                }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            val entity = repository.getBrief(date)
            _uiState.update {
                it.copy(
                    selectedDate = date,
                    summary = loadSummaryFromEntity(entity, date),
                    showDatePicker = false
                )
            }
        }
    }

    fun toggleDatePicker() {
        _uiState.update { it.copy(showDatePicker = !it.showDatePicker) }
    }

    fun toggleActionItem(itemId: String) {
        _uiState.update { state ->
            state.copy(
                summary = state.summary.copy(
                    actionItems = state.summary.actionItems.map { item ->
                        if (item.id == itemId) item.copy(isCompleted = !item.isCompleted)
                        else item
                    }
                )
            )
        }
    }

    private fun loadSummaryFromEntity(entity: com.daybrief.app.data.db.DailyBriefEntity?, date: LocalDate): SummaryUiState {
        return if (entity == null) {
            SummaryUiState(
                dateDisplay = date.format(formatter),
                overviewSummary = "",
                actionItems = emptyList(),
                pendingIssues = emptyList(),
                events = emptyList()
            )
        } else {
            SummaryUiState(
                dateDisplay = LocalDate.ofEpochDay(entity.dateEpochDay).format(formatter),
                listeningDuration = entity.listeningDuration,
                eventCount = entity.events.size,
                overviewSummary = entity.overviewSummary,
                actionItems = entity.actionItems,
                pendingIssues = entity.pendingIssues,
                events = entity.events,
                isSaved = true
            )
        }
    }
}

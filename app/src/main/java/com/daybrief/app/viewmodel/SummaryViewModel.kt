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

// =============================================================================
// Data Models for Daily Summary
// =============================================================================

/**
 * A single action item extracted from the day's conversations.
 */
data class ActionItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

/**
 * A pending/unresolved issue identified during the day.
 */
data class PendingIssue(
    val id: String,
    val text: String,
    val context: String = ""
)

/**
 * An event captured during the day (via Mark Event or auto-segmentation).
 */
data class DayEvent(
    val id: String,
    val time: String,
    val title: String,
    val summary: String
)

/**
 * Complete UI state for the Daily Summary screen.
 */
data class SummaryUiState(
    /** Whether summary is being generated */
    val isLoading: Boolean = false,

    /** Today's date formatted for display */
    val dateDisplay: String = LocalDate.now().format(
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("th"))
    ),

    /** Total listening time formatted as HH:MM */
    val listeningDuration: String = "00:00",

    /** Total number of events captured */
    val eventCount: Int = 0,

    /** AI-generated overview summary */
    val overviewSummary: String = "",

    /** Action items extracted by AI */
    val actionItems: List<ActionItem> = emptyList(),

    /** Pending/unresolved issues identified by AI */
    val pendingIssues: List<PendingIssue> = emptyList(),

    /** Individual events captured during the day */
    val events: List<DayEvent> = emptyList(),

    /** Whether the summary has been saved */
    val isSaved: Boolean = false,

    /** Whether delete confirmation dialog is showing */
    val showDeleteConfirm: Boolean = false
)

/**
 * ViewModel for the Daily Summary / Review screen.
 *
 * Responsibilities:
 * - Display the end-of-day AI brief
 * - Allow user to review, edit, and manage summary
 * - Toggle action item completion
 * - Save or delete the day's summary
 *
 * Privacy compliance (.cursorrules):
 * - User can review and delete all data
 * - No data is shared automatically
 * - User decides what to keep
 *
 * NOTE: Currently uses mock data. Will be connected to actual
 * AI summarization pipeline in Task Group F.
 */
@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: BriefRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    /**
     * Loads the day's summary.
     * First tries DB; if empty, uses mock data (until AI pipeline in Task Group F).
     */
    private fun loadSummary() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val today = LocalDate.now()
            val saved = repository.getBrief(today)
            if (saved != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        listeningDuration = saved.listeningDuration,
                        eventCount = saved.events.size,
                        overviewSummary = saved.overviewSummary,
                        actionItems = saved.actionItems,
                        pendingIssues = saved.pendingIssues,
                        events = saved.events,
                        isSaved = true
                    )
                }
                return@launch
            }
            // Fallback: mock data (will be replaced by AI in Task Group F)
            val mockActionItems = listOf(
                ActionItem("a1", "ส่งรายงานสรุปไตรมาส 1 ให้ทีมบัญชีภายในวันศุกร์"),
                ActionItem("a2", "นัดประชุม follow-up กับทีม Marketing เรื่องแคมเปญใหม่"),
                ActionItem("a3", "ตรวจสอบ proposal ที่ทีมพัฒนาส่งมา แล้วส่ง feedback กลับ")
            )

            val mockPendingIssues = listOf(
                PendingIssue(
                    "p1",
                    "งบประมาณโปรเจกต์ B ยังไม่ได้รับอนุมัติ",
                    "รอ CFO ลงนาม"
                ),
                PendingIssue(
                    "p2",
                    "ยังไม่ได้ตอบอีเมลจากลูกค้า XYZ เรื่องเลื่อนกำหนดส่งงาน",
                    ""
                )
            )

            val mockEvents = listOf(
                DayEvent(
                    "e1",
                    "09:15",
                    "ประชุมทีม Morning Standup",
                    "ทีมรายงานความคืบหน้า sprint ปัจจุบัน มี 2 task ติดปัญหา API"
                ),
                DayEvent(
                    "e2",
                    "10:30",
                    "โทรคุยกับลูกค้า ABC Corp",
                    "ลูกค้าพอใจกับ demo ต้องการเพิ่มฟีเจอร์ export PDF"
                ),
                DayEvent(
                    "e3",
                    "13:00",
                    "ประชุมงบประมาณไตรมาส 2",
                    "ปรับลดงบ Marketing 10% เพิ่มงบ R&D สำหรับ AI project"
                ),
                DayEvent(
                    "e4",
                    "15:30",
                    "Review แผนแคมเปญ",
                    "แคมเปญ Social Media เปิดตัวเดือนหน้า ต้องเตรียม content"
                )
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    listeningDuration = "08:32",
                    eventCount = mockEvents.size,
                    overviewSummary = "วันนี้มีการประชุมหลัก 3 ครั้ง เน้นเรื่องความคืบหน้าโปรเจกต์และงบประมาณ " +
                            "ลูกค้า ABC Corp ตอบรับ demo ดี มี action items สำคัญ 3 เรื่อง " +
                            "และมีเรื่องค้างคาที่ต้องติดตาม 2 เรื่อง",
                    actionItems = mockActionItems,
                    pendingIssues = mockPendingIssues,
                    events = mockEvents
                )
            }
        }
    }

    /**
     * Toggles an action item's completion status.
     */
    fun toggleActionItem(itemId: String) {
        _uiState.update { state ->
            state.copy(
                actionItems = state.actionItems.map { item ->
                    if (item.id == itemId) item.copy(isCompleted = !item.isCompleted)
                    else item
                }
            )
        }
    }

    /**
     * Saves the day's summary to SQLite (Room).
     */
    fun saveSummary() {
        viewModelScope.launch {
            val s = _uiState.value
            repository.saveBrief(
                date = LocalDate.now(),
                listeningDuration = s.listeningDuration,
                overviewSummary = s.overviewSummary,
                actionItems = s.actionItems,
                pendingIssues = s.pendingIssues,
                events = s.events
            )
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    /**
     * Shows the delete confirmation dialog.
     */
    fun requestDelete() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    /**
     * Dismisses the delete confirmation dialog.
     */
    fun dismissDelete() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    /**
     * Deletes the day's summary permanently from DB.
     * Privacy compliance: user has full control over data deletion.
     */
    fun confirmDelete() {
        viewModelScope.launch {
            repository.deleteByDate(LocalDate.now())
            _uiState.update {
                SummaryUiState(
                    isLoading = false,
                    overviewSummary = "",
                    actionItems = emptyList(),
                    pendingIssues = emptyList(),
                    events = emptyList(),
                    showDeleteConfirm = false
                )
            }
        }
    }
}

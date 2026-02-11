package com.daybrief.app

import com.daybrief.app.viewmodel.ListeningUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for ListeningUiState.
 *
 * These tests verify the UI state data class behavior,
 * particularly the formatted elapsed time computation.
 *
 * Note: ViewModel tests with service interaction require
 * instrumented tests (androidTest) with Hilt test runner.
 */
class ListeningUiStateTest {

    @Test
    fun `default state is not listening and not paused`() {
        val state = ListeningUiState()

        assertFalse(state.isListening)
        assertFalse(state.isPaused)
        assertEquals(0, state.eventCount)
        assertEquals(0L, state.elapsedSeconds)
    }

    @Test
    fun `formatted time shows 00-00-00 at start`() {
        val state = ListeningUiState(elapsedSeconds = 0)

        assertEquals("00:00:00", state.formattedElapsedTime)
    }

    @Test
    fun `formatted time shows seconds correctly`() {
        val state = ListeningUiState(elapsedSeconds = 45)

        assertEquals("00:00:45", state.formattedElapsedTime)
    }

    @Test
    fun `formatted time shows minutes and seconds`() {
        val state = ListeningUiState(elapsedSeconds = 125) // 2 min 5 sec

        assertEquals("00:02:05", state.formattedElapsedTime)
    }

    @Test
    fun `formatted time shows hours minutes seconds`() {
        val state = ListeningUiState(elapsedSeconds = 3661) // 1h 1m 1s

        assertEquals("01:01:01", state.formattedElapsedTime)
    }

    @Test
    fun `formatted time handles full workday`() {
        val state = ListeningUiState(elapsedSeconds = 36000) // 10 hours

        assertEquals("10:00:00", state.formattedElapsedTime)
    }

    @Test
    fun `default schedule is 8-00 to 18-00`() {
        val state = ListeningUiState()

        assertEquals(8, state.startHour)
        assertEquals(0, state.startMinute)
        assertEquals(18, state.endHour)
        assertEquals(0, state.endMinute)
    }

    @Test
    fun `event count starts at zero`() {
        val state = ListeningUiState()

        assertEquals(0, state.eventCount)
    }

    @Test
    fun `state can track multiple events`() {
        val state = ListeningUiState(eventCount = 5)

        assertEquals(5, state.eventCount)
    }

    @Test
    fun `listening state with pause`() {
        val state = ListeningUiState(isListening = true, isPaused = true)

        assertTrue(state.isListening)
        assertTrue(state.isPaused)
    }
}

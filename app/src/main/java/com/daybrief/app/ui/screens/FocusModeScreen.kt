package com.daybrief.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daybrief.app.R
import com.daybrief.app.ui.theme.ListeningActive
import com.daybrief.app.ui.theme.ListeningPulse
import com.daybrief.app.ui.theme.PausedState
import com.daybrief.app.viewmodel.ListeningViewModel

/**
 * Focus Mode Screen - Figma-matched design.
 *
 * Layout (top to bottom):
 * 1. Header: "กำลังช่วยจำ" + Settings gear icon
 * 2. Large teal-green circle with stop icon (pulsing when active)
 * 3. Status text: "กำลังฟังเพื่อจับใจความสำคัญ"
 * 4. "พักการฟัง" button (outlined, rounded)
 * 5. "บันทึกเหตุการณ์สำคัญ" button (outlined, rounded)
 * 6. "จบงานวันนี้ & สรุปผล" text link at bottom
 *
 * Privacy compliance (.cursorrules):
 * - Does NOT display any transcript or captured text
 * - Shows clear listening status indicator
 * - Provides immediate stop / pause capability
 */
@Composable
fun FocusModeScreen(
    onEndDay: () -> Unit,
    viewModel: ListeningViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── Header ───
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.focus_header),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { /* TODO: Navigate to Settings */ }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // ─── Listening Indicator ───
            Spacer(modifier = Modifier.weight(0.15f))

            ListeningIndicator(
                isListening = uiState.isListening,
                isPaused = uiState.isPaused,
                onStopClick = {
                    viewModel.stopListening()
                    onEndDay()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ─── Status Text ───
            Text(
                text = if (uiState.isPaused) {
                    stringResource(R.string.focus_paused_status)
                } else {
                    stringResource(R.string.focus_listening_status)
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (uiState.isPaused) PausedState else ListeningActive,
                textAlign = TextAlign.Center
            )

            // ─── Action Buttons ───
            Spacer(modifier = Modifier.weight(0.2f))

            // Pause / Resume button
            FocusActionButton(
                icon = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                label = if (uiState.isPaused) {
                    stringResource(R.string.focus_resume)
                } else {
                    stringResource(R.string.focus_pause)
                },
                onClick = {
                    if (uiState.isPaused) viewModel.resumeListening()
                    else viewModel.pauseListening()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mark Event button
            FocusActionButton(
                icon = Icons.Default.Bookmark,
                label = stringResource(R.string.focus_mark_event),
                onClick = { viewModel.markEvent() }
            )

            // Events count
            if (uiState.eventCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.focus_events_marked, uiState.eventCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // ─── End Day & Summarize ───
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        viewModel.stopListening()
                        onEndDay()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = null,
                    tint = ListeningActive,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.focus_end_day),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ListeningActive
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Figma-matched listening indicator:
 * Large teal-green circle with pulsing outer ring and a white stop icon.
 */
@Composable
private fun ListeningIndicator(
    isListening: Boolean,
    isPaused: Boolean,
    onStopClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "listening_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val activeColor = if (isPaused) PausedState else ListeningActive
    val ringColor = if (isPaused) PausedState.copy(alpha = 0.3f) else ListeningPulse

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(180.dp)
    ) {
        // Outer pulse ring (only animate when actively listening, not paused)
        if (!isPaused) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(pulseScale)
                    .alpha(pulseAlpha)
                    .background(color = ringColor, shape = CircleShape)
            )
        } else {
            // Static ring when paused
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .alpha(0.2f)
                    .background(color = ringColor, shape = CircleShape)
            )
        }

        // Main circle with stop button
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(color = activeColor)
                .clickable { onStopClick() },
            contentAlignment = Alignment.Center
        ) {
            // White rounded-square stop icon (matching Figma)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

/**
 * Figma-matched outlined action button with icon and label.
 * Rounded corners, subtle border, full-width.
 */
@Composable
private fun FocusActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

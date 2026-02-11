package com.daybrief.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daybrief.app.ui.screens.DailySetupScreen
import com.daybrief.app.ui.screens.DailySummaryScreen
import com.daybrief.app.ui.screens.FocusModeScreen
import com.daybrief.app.ui.screens.ReportViewScreen

/**
 * Navigation routes for DAYBRIEF.
 */
object Routes {
    const val DAILY_SETUP = "daily_setup"
    const val FOCUS_MODE = "focus_mode"
    const val DAILY_SUMMARY = "daily_summary"
    const val REPORTS = "reports"
}

/**
 * Main navigation graph for the app.
 *
 * Flow: DailySetup -> FocusMode -> DailySummary -> DailySetup
 * User starts on the setup screen, enters focus mode,
 * then reviews the daily summary before returning home.
 */
@Composable
fun DayBriefNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DAILY_SETUP
    ) {
        composable(Routes.DAILY_SETUP) {
            DailySetupScreen(
                onStartListening = {
                    navController.navigate(Routes.FOCUS_MODE) {
                        popUpTo(Routes.DAILY_SETUP) { inclusive = false }
                    }
                },
                onViewReports = { navController.navigate(Routes.REPORTS) }
            )
        }

        composable(Routes.REPORTS) {
            ReportViewScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FOCUS_MODE) {
            FocusModeScreen(
                onEndDay = {
                    // Navigate to summary screen after ending the day
                    navController.navigate(Routes.DAILY_SUMMARY) {
                        popUpTo(Routes.DAILY_SETUP) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.DAILY_SUMMARY) {
            DailySummaryScreen(
                onBackToSetup = {
                    navController.navigate(Routes.DAILY_SETUP) {
                        popUpTo(Routes.DAILY_SETUP) { inclusive = true }
                    }
                }
            )
        }
    }
}

package com.daybrief.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daybrief.app.ui.screens.DailySetupScreen
import com.daybrief.app.ui.screens.FocusModeScreen

/**
 * Navigation routes for DAYBRIEF.
 */
object Routes {
    const val DAILY_SETUP = "daily_setup"
    const val FOCUS_MODE = "focus_mode"
}

/**
 * Main navigation graph for the app.
 *
 * Flow: DailySetup -> FocusMode
 * User starts on the setup screen and navigates to focus mode
 * when they tap "Start Today".
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
                        // Don't keep setup screen in back stack during active session
                        popUpTo(Routes.DAILY_SETUP) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.FOCUS_MODE) {
            FocusModeScreen(
                onStopListening = {
                    navController.navigate(Routes.DAILY_SETUP) {
                        popUpTo(Routes.DAILY_SETUP) { inclusive = true }
                    }
                }
            )
        }
    }
}

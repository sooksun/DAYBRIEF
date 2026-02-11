package com.daybrief.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.daybrief.app.navigation.DayBriefNavGraph
import com.daybrief.app.ui.theme.DayBriefTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for DAYBRIEF.
 *
 * Uses Jetpack Compose with Material 3.
 * Hilt provides dependency injection via @AndroidEntryPoint.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DayBriefTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    DayBriefNavGraph(navController = navController)
                }
            }
        }
    }
}

package com.jinnylee.runnningtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jinnylee.runnningtracker.core.routing.NavigationRoot
import com.jinnylee.runnningtracker.ui.theme.RunnningTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunnningTrackerTheme {
                NavigationRoot()
            }
        }
    }
}

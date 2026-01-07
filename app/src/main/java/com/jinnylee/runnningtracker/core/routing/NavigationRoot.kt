package com.jinnylee.runnningtracker.core.routing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jinnylee.runnningtracker.presentation.screen.main.MainRoot
import com.jinnylee.runnningtracker.presentation.screen.record.RecordScreen

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Route.Main)

    val currentRoute : Route =
        backStack.lastOrNull() as? Route ?: Route.Main

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Main> {
                MainRoot()
            }
            entry<Route.Record> {
               RecordScreen()
            }
        }
    )


}
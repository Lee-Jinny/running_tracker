package com.jinnylee.runnningtracker.presentation.screen.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecordRoot(
    modifier: Modifier = Modifier,
    viewModel: RecordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                RecordEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    RecordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

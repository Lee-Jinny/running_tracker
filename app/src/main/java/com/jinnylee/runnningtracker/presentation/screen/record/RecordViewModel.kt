package com.jinnylee.runnningtracker.presentation.screen.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jinnylee.runnningtracker.domain.usecase.GetAllRunsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    getAllRunsUseCase: GetAllRunsUseCase
) : ViewModel() {

    val state: StateFlow<RecordState> = getAllRunsUseCase()
        .map { runs -> RecordState(runs = runs) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecordState()
        )

    private val _event = MutableSharedFlow<RecordEvent>()
    val event = _event.asSharedFlow()

    fun onAction(action: RecordAction) {
        when (action) {
            RecordAction.OnBackClick -> {
                viewModelScope.launch {
                    _event.emit(RecordEvent.NavigateBack)
                }
            }
        }
    }
}
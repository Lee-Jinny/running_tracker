package com.jinnylee.runnningtracker.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jinnylee.runnningtracker.ui.theme.Background
import com.jinnylee.runnningtracker.ui.theme.White

@Composable
fun SnackbarComponent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
        snackbar = { data: SnackbarData ->
            Snackbar(
                snackbarData = data,
                containerColor = Background,
                contentColor = White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(16.dp)
            )
        }
    )
}

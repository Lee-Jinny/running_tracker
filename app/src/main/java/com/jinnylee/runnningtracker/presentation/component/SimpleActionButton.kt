package com.jinnylee.runnningtracker.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jinnylee.runnningtracker.ui.theme.Anton
import com.jinnylee.runnningtracker.ui.theme.Background

@Composable
fun SimpleActionButton(
    text: String,
    color: Color,
    textColor: Color = Background,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(90.dp), // Ripple 버튼보다 약간 작게
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp, // 글자 크기도 약간 조정
            fontWeight = FontWeight.Bold,
            fontFamily = Anton
        )
    }
}
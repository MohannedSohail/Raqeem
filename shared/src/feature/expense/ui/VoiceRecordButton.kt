package org.moh.raqeem.project.feature.expense.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.moh.raqeem.project.feature.expense.RecordingStatus
import org.moh.raqeem.project.theme.CoralExpense
import org.moh.raqeem.project.theme.EmeraldPrimary

@Composable
fun VoiceRecordButton(
    status: RecordingStatus,
    amplitude: Float,
    durationSec: Int,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording = status == RecordingStatus.RECORDING
    val isProcessing = status == RecordingStatus.PROCESSING

    // Infinite wave pulse animation when recording
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f + (amplitude * 0.4f) else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (isRecording) {
            Text(
                text = "جاري التسجيل... 00:${durationSec.toString().padStart(2, '0')}",
                color = CoralExpense,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        } else if (isProcessing) {
            Text(
                text = "جاري الفهم بالذكاء الاصطناعي...",
                color = EmeraldPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        } else {
            Text(
                text = "المس وتحدث بمصاريفك",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(96.dp)
        ) {
            // Outer pulsing ring
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(CoralExpense.copy(alpha = 0.25f))
                )
            }

            // Central Action Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isRecording -> CoralExpense
                            isProcessing -> EmeraldPrimary.copy(alpha = 0.8f)
                            else -> EmeraldPrimary
                        }
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        when (status) {
                            RecordingStatus.IDLE -> onStartRecording()
                            RecordingStatus.RECORDING -> onStopRecording()
                            else -> {}
                        }
                    }
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = if (isRecording) "⏹️" else "🎙️",
                        fontSize = 28.sp
                    )
                }
            }
        }
    }
}


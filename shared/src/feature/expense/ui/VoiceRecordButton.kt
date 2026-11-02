package org.moh.raqeem.project.feature.expense.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.moh.raqeem.project.theme.MicrophoneVectorIcon

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

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f + (amplitude.coerceIn(0.1f, 1f) * 0.45f),
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
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
                text = "جاري الاستماع... ${durationSec} ثانية",
                color = CoralExpense,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else if (isProcessing) {
            Text(
                text = "تحليل فوري محلياً عبر Whisper.cpp...",
                color = EmeraldPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
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
                        modifier = Modifier.size(30.dp),
                        strokeWidth = 3.dp
                    )
                } else if (isRecording) {
                    // Crisp Stop Square Vector
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                    )
                } else {
                    // Crisp Microphone Vector (No Emojis!)
                    MicrophoneVectorIcon(
                        color = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

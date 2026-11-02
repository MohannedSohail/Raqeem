package org.moh.raqeem.project.feature.splash.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.moh.raqeem.project.theme.EmeraldDark
import org.moh.raqeem.project.theme.EmeraldPrimary

/**
 * Pixel-accurate Splash Screen matching raqeem_screen_splash.jpg.
 * Holographic circular emblem, glowing acoustic concentric rings,
 * 3D Arabic 'R' ligature with waveform equalizer bars, and luxury dark emerald gradient.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(2200) // 2.2s splash duration
        onSplashFinished()
    }

    // Gentle pulsing animation for the outer acoustic orbital rings
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val waveHeightAnim by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF031A18), // Deep emerald apex
                        Color(0xFF071922),
                        Color(0xFF0B132B)  // Deep obsidian night base
                    )
                )
            )
    ) {
        // Main Emblem & Typography Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            // Concentric Glowing Emblem matching raqeem_screen_splash.jpg
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .scale(pulseScale)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w / 2f, h / 2f)

                    // 1. Ambient outer radial glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EmeraldPrimary.copy(alpha = 0.28f),
                                EmeraldDark.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = w * 0.48f
                        ),
                        radius = w * 0.48f,
                        center = center
                    )

                    // 2. Faint outer orbital ring with subtle tick marks
                    drawCircle(
                        color = Color(0xFF34D399).copy(alpha = 0.20f),
                        radius = w * 0.44f,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // 3. Main glowing luminous ring
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF10B981),
                                Color(0xFF34D399),
                                Color(0xFF064E3B),
                                Color(0xFF10B981)
                            ),
                            center = center
                        ),
                        radius = w * 0.36f,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // 4. Inner glass orb background
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF0A3631),
                                Color(0xFF04201E)
                            ),
                            center = center,
                            radius = w * 0.32f
                        ),
                        radius = w * 0.32f,
                        center = center
                    )

                    // 5. Vertical sound waveform equalizer bars across the center
                    val barCount = 7
                    val barWidth = 4.dp.toPx()
                    val barSpacing = 11.dp.toPx()
                    val totalBarsWidth = barCount * barWidth + (barCount - 1) * (barSpacing - barWidth)
                    val startX = (w - totalBarsWidth) / 2f

                    val heights = listOf(18.dp, 32.dp, 48.dp, 62.dp, 48.dp, 32.dp, 18.dp)

                    for (i in 0 until barCount) {
                        val currentHeight = heights[i].toPx() * (if (i % 2 == 0) waveHeightAnim else (2.0f - waveHeightAnim))
                        val x = startX + i * barSpacing
                        val y = (h - currentHeight) / 2f

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF6EE7B7),
                                    Color(0xFF10B981)
                                )
                            ),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, currentHeight),
                            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                        )
                    }

                    // 6. Fluid Arabic 'R' (ر) ligature contour intertwined with the soundwave
                    val letterPath = Path().apply {
                        moveTo(w * 0.54f, h * 0.35f)
                        lineTo(w * 0.54f, h * 0.52f)
                        cubicTo(
                            w * 0.54f, h * 0.65f,
                            w * 0.46f, h * 0.70f,
                            w * 0.38f, h * 0.68f
                        )
                    }
                    drawPath(
                        path = letterPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFF6EE7B7),
                                Color(0xFF059669)
                            )
                        ),
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Wordmark: "رقيم" in radiant emerald-to-mint gradient
            Text(
                text = "رقيم",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color(0xFFF8FAFC)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Tagline
            Text(
                text = "سجّل مصاريفك بصوتك",
                color = Color(0xFF94A3B8),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom Progress Indicator & Local Whisper Engine Tag
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            CircularProgressIndicator(
                color = EmeraldPrimary,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "معالجة محلية ذكية • Whisper.cpp 🔒",
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

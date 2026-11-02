package org.moh.raqeem.project.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.moh.raqeem.project.model.ExpenseCategory
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Professional vector icons suite for Raqeem Fintech.
 * Replaces all emojis with crisp, elegant geometric vector icons.
 */
object RaqeemIcons

@Composable
fun CategoryIcon(
    category: ExpenseCategory,
    color: Color = Color.White,
    modifier: Modifier = Modifier.size(22.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        when (category) {
            ExpenseCategory.GROCERIES -> {
                // Shopping basket / cart vector
                val path = Path().apply {
                    moveTo(w * 0.15f, h * 0.25f)
                    lineTo(w * 0.3f, h * 0.25f)
                    lineTo(w * 0.45f, h * 0.65f)
                    lineTo(w * 0.85f, h * 0.65f)
                    lineTo(w * 0.92f, h * 0.35f)
                    lineTo(w * 0.35f, h * 0.35f)
                }
                drawPath(path, color, style = Stroke(width = strokeW, cap = StrokeCap.Round))
                drawCircle(color, radius = strokeW * 1.1f, center = Offset(w * 0.48f, h * 0.82f))
                drawCircle(color, radius = strokeW * 1.1f, center = Offset(w * 0.80f, h * 0.82f))
            }
            ExpenseCategory.DINING -> {
                // Coffee / cup vector
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.22f, h * 0.3f),
                    size = Size(w * 0.5f, h * 0.45f),
                    cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
                    style = Stroke(width = strokeW)
                )
                // Cup handle
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.62f, h * 0.36f),
                    size = Size(w * 0.22f, h * 0.30f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Steam line
                drawLine(
                    color,
                    Offset(w * 0.47f, h * 0.14f),
                    Offset(w * 0.47f, h * 0.22f),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }
            ExpenseCategory.TRANSPORTATION -> {
                // Car / Fuel transport vector
                val carPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.65f)
                    lineTo(w * 0.22f, h * 0.45f)
                    lineTo(w * 0.40f, h * 0.30f)
                    lineTo(w * 0.68f, h * 0.30f)
                    lineTo(w * 0.82f, h * 0.45f)
                    lineTo(w * 0.90f, h * 0.65f)
                    close()
                }
                drawPath(carPath, color, style = Stroke(width = strokeW, cap = StrokeCap.Round))
                drawCircle(color, radius = strokeW * 1.2f, center = Offset(w * 0.32f, h * 0.72f))
                drawCircle(color, radius = strokeW * 1.2f, center = Offset(w * 0.73f, h * 0.72f))
            }
            ExpenseCategory.HEALTHCARE -> {
                // Medical Plus Cross vector
                val crossW = w * 0.22f
                drawRoundRect(
                    color,
                    topLeft = Offset((w - crossW) / 2f, h * 0.18f),
                    size = Size(crossW, h * 0.64f),
                    cornerRadius = CornerRadius(strokeW, strokeW)
                )
                drawRoundRect(
                    color,
                    topLeft = Offset(w * 0.18f, (h - crossW) / 2f),
                    size = Size(w * 0.64f, crossW),
                    cornerRadius = CornerRadius(strokeW, strokeW)
                )
            }
            ExpenseCategory.HOME_UTILITIES -> {
                // Modern House vector
                val roof = Path().apply {
                    moveTo(w * 0.15f, h * 0.48f)
                    lineTo(w * 0.50f, h * 0.18f)
                    lineTo(w * 0.85f, h * 0.48f)
                }
                drawPath(roof, color, style = Stroke(width = strokeW, cap = StrokeCap.Round))
                drawRect(
                    color,
                    topLeft = Offset(w * 0.25f, h * 0.48f),
                    size = Size(w * 0.50f, h * 0.38f),
                    style = Stroke(width = strokeW)
                )
            }
            ExpenseCategory.SHOPPING -> {
                // Shopping Bag vector
                drawRoundRect(
                    color,
                    topLeft = Offset(w * 0.22f, h * 0.32f),
                    size = Size(w * 0.56f, h * 0.54f),
                    cornerRadius = CornerRadius(strokeW * 1.5f, strokeW * 1.5f),
                    style = Stroke(width = strokeW)
                )
                // Bag handle
                drawArc(
                    color,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.36f, h * 0.16f),
                    size = Size(w * 0.28f, h * 0.28f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }
            ExpenseCategory.ENTERTAINMENT -> {
                // Gamepad / Entertainment vector
                drawRoundRect(
                    color,
                    topLeft = Offset(w * 0.16f, h * 0.32f),
                    size = Size(w * 0.68f, h * 0.44f),
                    cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
                    style = Stroke(width = strokeW)
                )
                // D-pad plus
                drawLine(color, Offset(w * 0.32f, h * 0.46f), Offset(w * 0.42f, h * 0.46f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.37f, h * 0.41f), Offset(w * 0.37f, h * 0.51f), strokeWidth = strokeW, cap = StrokeCap.Round)
                // Action dots
                drawCircle(color, radius = strokeW * 0.7f, center = Offset(w * 0.66f, h * 0.44f))
                drawCircle(color, radius = strokeW * 0.7f, center = Offset(w * 0.74f, h * 0.50f))
            }
            ExpenseCategory.OTHER -> {
                // Clean package / box vector
                drawRoundRect(
                    color,
                    topLeft = Offset(w * 0.20f, h * 0.22f),
                    size = Size(w * 0.60f, h * 0.56f),
                    cornerRadius = CornerRadius(strokeW, strokeW),
                    style = Stroke(width = strokeW)
                )
                drawLine(color, Offset(w * 0.20f, h * 0.42f), Offset(w * 0.80f, h * 0.42f), strokeWidth = strokeW)
                drawLine(color, Offset(w * 0.50f, h * 0.42f), Offset(w * 0.50f, h * 0.78f), strokeWidth = strokeW)
            }
        }
    }
}

@Composable
fun LockShieldIcon(
    color: Color = Color.White,
    modifier: Modifier = Modifier.size(16.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = (w * 0.1f).coerceAtLeast(1.5f)

        // Shield outline
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.62f)
            cubicTo(w * 0.75f, h * 0.80f, w * 0.60f, h * 0.90f, w * 0.5f, h * 0.94f)
            cubicTo(w * 0.40f, h * 0.90f, w * 0.25f, h * 0.80f, w * 0.18f, h * 0.62f)
            lineTo(w * 0.12f, h * 0.28f)
            close()
        }
        drawPath(shieldPath, color, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Checkmark inside shield
        val checkPath = Path().apply {
            moveTo(w * 0.34f, h * 0.50f)
            lineTo(w * 0.46f, h * 0.64f)
            lineTo(w * 0.68f, h * 0.38f)
        }
        drawPath(checkPath, color, style = Stroke(width = strokeW * 1.1f, cap = StrokeCap.Round))
    }
}

@Composable
fun MicrophoneVectorIcon(
    color: Color = Color.White,
    modifier: Modifier = Modifier.size(24.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(2f)

        // Mic capsule
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.35f, h * 0.15f),
            size = Size(w * 0.30f, h * 0.45f),
            cornerRadius = CornerRadius(w * 0.15f, w * 0.15f)
        )
        // Mic cradle arc
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.22f, h * 0.25f),
            size = Size(w * 0.56f, h * 0.45f),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
        // Mic stem & base
        drawLine(color, Offset(w * 0.5f, h * 0.70f), Offset(w * 0.5f, h * 0.85f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.35f, h * 0.85f), Offset(w * 0.65f, h * 0.85f), strokeWidth = strokeW, cap = StrokeCap.Round)
    }
}

@Composable
fun MailSmsIcon(
    color: Color = Color.White,
    modifier: Modifier = Modifier.size(20.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = (w * 0.08f).coerceAtLeast(1.8f)

        // Envelope body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.12f, h * 0.22f),
            size = Size(w * 0.76f, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = Stroke(width = strokeW)
        )
        // Flap
        val flap = Path().apply {
            moveTo(w * 0.14f, h * 0.25f)
            lineTo(w * 0.50f, h * 0.55f)
            lineTo(w * 0.86f, h * 0.25f)
        }
        drawPath(flap, color, style = Stroke(width = strokeW, cap = StrokeCap.Round))
    }
}

@Composable
fun SettingsGearIcon(
    color: Color = Color.White,
    modifier: Modifier = Modifier.size(20.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = (w * 0.09f).coerceAtLeast(1.8f)

        drawCircle(color, radius = w * 0.22f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = strokeW))

        // Gear teeth
        for (i in 0 until 6) {
            val angle = i * 60f * (PI / 180.0)
            val cosVal = cos(angle).toFloat()
            val sinVal = sin(angle).toFloat()
            val start = Offset(w * 0.5f + cosVal * w * 0.24f, h * 0.5f + sinVal * h * 0.24f)
            val end = Offset(w * 0.5f + cosVal * w * 0.38f, h * 0.5f + sinVal * h * 0.38f)
            drawLine(color, start, end, strokeWidth = strokeW * 1.3f, cap = StrokeCap.Round)
        }
    }
}


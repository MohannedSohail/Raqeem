package org.moh.raqeem.project.feature.onboarding.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.moh.raqeem.project.theme.EmeraldDark
import org.moh.raqeem.project.theme.EmeraldPrimary
import org.moh.raqeem.project.theme.SlateBackgroundDark

data class OnboardingStepData(
    val id: Int,
    val title: String,
    val description: String,
    val tag: String
)

val OnboardingStepsList = listOf(
    OnboardingStepData(
        id = 1,
        title = "سجّل بصوتك وبلهجتك العادية",
        description = "تكلّم بعفويتك كما تتحدث مع أصدقائك: 'دفعت 50 شيكل بنزين ومحمد حاسب 20 شيكل قهوة'. التطبيق يستمع ويفهم تلقائياً دون كتابة أو تعقيد.",
        tag = "تسجيل صوتي في 3 ثوانٍ"
    ),
    OnboardingStepData(
        id = 2,
        title = "ذكاء اصطناعي محلي بخصوصية تامة",
        description = "يعتمد رقيم على محرك Whisper.cpp محلياً بالكامل على معالج جهازك بدون إنترنت. تسجيلاتك الصوتية وأرقامك المالية لا تغادر هاتفك نهائياً.",
        tag = "أمان مالي • 100% Offline"
    ),
    OnboardingStepData(
        id = 3,
        title = "رادار الميزانية وتوزيع \"مين دفع كم\"",
        description = "تابع نفقاتك الأسبوعية بالشيكل (₪) بدقة، واعرف كم صُرف ومقدار الوفر المتبقي، مع تفكيك فوري للمصاريف المشتركة بين الأصدقاء والأسرة.",
        tag = "إدارة مالية دقيقة بالشيكل (₪)"
    )
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = OnboardingStepsList[currentStepIndex]
    val isLastStep = currentStepIndex == OnboardingStepsList.size - 1

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackgroundDark)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar: Step Counter & Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "خطوة ${currentStepIndex + 1} من ${OnboardingStepsList.size}",
                        color = EmeraldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isLastStep) {
                    TextButton(onClick = onFinishOnboarding) {
                        Text(
                            text = "تخطي",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Central Visual Illustration & Content
            AnimatedContent(
                targetState = currentStepIndex,
                transitionSpec = {
                    (slideInHorizontally { width -> -width } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                },
                modifier = Modifier.weight(1f)
            ) { stepIdx ->
                val currentStep = OnboardingStepsList[stepIdx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    // Professional Canvas Hero Graphic (No Emojis!)
                    OnboardingStepIllustration(stepId = currentStep.id)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Feature Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentStep.tag,
                            color = EmeraldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = currentStep.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = currentStep.description,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom Navigation: Indicator Dots & Next / Start Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Step Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    OnboardingStepsList.forEachIndexed { index, _ ->
                        val isSelected = index == currentStepIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) EmeraldPrimary else Color.White.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                // Primary CTA Button
                Button(
                    onClick = {
                        if (isLastStep) {
                            onFinishOnboarding()
                        } else {
                            currentStepIndex++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isLastStep) "ابدأ الآن وتحكم بمصاريفك" else "التالي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingStepIllustration(
    stepId: Int,
    modifier: Modifier = Modifier.size(190.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // Outer ambient glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    EmeraldPrimary.copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = center,
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = center
        )

        // Background container rounded square
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E293B),
                    Color(0xFF0F172A)
                )
            ),
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size = Size(w * 0.76f, h * 0.76f),
            cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )

        when (stepId) {
            1 -> {
                // Vector Graphic: Microphone & Expanding Acoustic Rings
                val micColor = Color(0xFF10B981)
                val strokeW = 3.dp.toPx()

                // Concentric Soundwaves
                drawCircle(micColor.copy(alpha = 0.25f), radius = w * 0.30f, center = center, style = Stroke(width = 1.5.dp.toPx()))
                drawCircle(micColor.copy(alpha = 0.40f), radius = w * 0.22f, center = center, style = Stroke(width = 1.8.dp.toPx()))

                // Mic capsule
                drawRoundRect(
                    color = micColor,
                    topLeft = Offset(w * 0.43f, h * 0.35f),
                    size = Size(w * 0.14f, h * 0.22f),
                    cornerRadius = CornerRadius(w * 0.07f, w * 0.07f)
                )
                // Mic cradle
                drawArc(
                    color = Color.White,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.37f, h * 0.40f),
                    size = Size(w * 0.26f, h * 0.20f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Mic stand
                drawLine(Color.White, Offset(w * 0.5f, h * 0.60f), Offset(w * 0.5f, h * 0.68f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(Color.White, Offset(w * 0.42f, h * 0.68f), Offset(w * 0.58f, h * 0.68f), strokeWidth = strokeW, cap = StrokeCap.Round)
            }
            2 -> {
                // Vector Graphic: Security Shield & Offline Lock (Whisper.cpp)
                val shieldColor = Color(0xFF10B981)
                val strokeW = 3.dp.toPx()

                val shield = Path().apply {
                    moveTo(w * 0.50f, h * 0.28f)
                    lineTo(w * 0.68f, h * 0.36f)
                    lineTo(w * 0.64f, h * 0.58f)
                    cubicTo(w * 0.60f, h * 0.68f, w * 0.54f, h * 0.74f, w * 0.50f, h * 0.76f)
                    cubicTo(w * 0.46f, h * 0.74f, w * 0.40f, h * 0.68f, w * 0.36f, h * 0.58f)
                    lineTo(w * 0.32f, h * 0.36f)
                    close()
                }
                drawPath(shield, shieldColor, style = Stroke(width = strokeW, cap = StrokeCap.Round))

                // Checkmark inside shield
                val check = Path().apply {
                    moveTo(w * 0.44f, h * 0.52f)
                    lineTo(w * 0.48f, h * 0.57f)
                    lineTo(w * 0.57f, h * 0.45f)
                }
                drawPath(check, Color.White, style = Stroke(width = strokeW, cap = StrokeCap.Round))
            }
            3 -> {
                // Vector Graphic: Shekel Currency Gauge & Balance Chart
                val gaugeColor = Color(0xFF34D399)
                val strokeW = 3.dp.toPx()

                // Gauge Arc
                drawArc(
                    color = Color.White.copy(alpha = 0.2f),
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = Offset(w * 0.30f, h * 0.30f),
                    size = Size(w * 0.40f, h * 0.40f),
                    style = Stroke(width = strokeW * 1.5f, cap = StrokeCap.Round)
                )
                // Filled progress
                drawArc(
                    color = gaugeColor,
                    startAngle = 150f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(w * 0.30f, h * 0.30f),
                    size = Size(w * 0.40f, h * 0.40f),
                    style = Stroke(width = strokeW * 1.5f, cap = StrokeCap.Round)
                )

                // Financial Shekel symbol center bars
                drawLine(Color.White, Offset(w * 0.42f, h * 0.46f), Offset(w * 0.42f, h * 0.60f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(gaugeColor, Offset(w * 0.50f, h * 0.42f), Offset(w * 0.50f, h * 0.60f), strokeWidth = strokeW, cap = StrokeCap.Round)
                drawLine(Color.White, Offset(w * 0.58f, h * 0.48f), Offset(w * 0.58f, h * 0.60f), strokeWidth = strokeW, cap = StrokeCap.Round)
            }
        }
    }
}

package org.moh.raqeem.project.feature.onboarding.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.moh.raqeem.project.theme.EmeraldDark
import org.moh.raqeem.project.theme.EmeraldPrimary
import org.moh.raqeem.project.theme.SlateBackgroundDark

data class OnboardingStep(
    val emoji: String,
    val title: String,
    val description: String,
    val tag: String
)

val OnboardingSteps = listOf(
    OnboardingStep(
        emoji = "🎙️",
        title = "تكلم بعفوية بلهجتك الدارجة",
        description = "لا داعي للكتابة أو ملء الجداول الطويلة. اضغط الميكروفون وتكلم كما تتكلم مع أصدقائك، واشرح ما اشتريته ومن دفع الحساب.",
        tag = "تسجيل صوتي في 5 ثوانٍ"
    ),
    OnboardingStep(
        emoji = "⚡",
        title = "فهم فوري بذكاء Gemini 2.5 Flash",
        description = "يقوم الذكاء الاصطناعي بتفكيك مشترياتك المتعددة، وتحديد المبالغ، وتصنيف الفئات بدقة تفوق 95% ومراجعتها بلمسة واحدة.",
        tag = "مخرجات مهيكلة بدون أخطاء"
    ),
    OnboardingStep(
        emoji = "👥",
        title = "رادار الأسبوع وتوزيع \"مين دفع كم\"",
        description = "تتبع فوري لميزانيتك الأسبوعية، مع كشف حساب دقيق للمصاريف المشتركة بين الأصدقاء أو العائلة وتحديد الرصيد المتبقي.",
        tag = "تحكم كامل بمصاريفك"
    )
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = OnboardingSteps[currentStepIndex]
    val isLastStep = currentStepIndex == OnboardingSteps.size - 1

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
            // Top Bar: Skip button
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
                        text = "خطوة ${currentStepIndex + 1} من ${OnboardingSteps.size}",
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

            // Center Content with Animation
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { width -> width / 2 })
                        .togetherWith(fadeOut() + slideOutHorizontally { width -> -width / 2 })
                },
                modifier = Modifier.weight(1f)
            ) { targetStep ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Feature Icon Card
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        EmeraldPrimary.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(text = targetStep.emoji, fontSize = 48.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldDark.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = targetStep.tag,
                            color = EmeraldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = targetStep.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = targetStep.description,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bottom Navigation & Indicators
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Step Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(OnboardingSteps.size) { index ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == currentStepIndex) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (index == currentStepIndex) EmeraldPrimary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                // CTA Button
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
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isLastStep) "ابدأ الآن 🚀" else "التالي",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


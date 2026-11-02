package org.moh.raqeem.project

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.moh.raqeem.project.feature.expense.ExpenseViewModel
import org.moh.raqeem.project.feature.expense.ui.RaqeemDashboardScreen
import org.moh.raqeem.project.feature.onboarding.ui.OnboardingScreen
import org.moh.raqeem.project.feature.splash.ui.SplashScreen
import org.moh.raqeem.project.theme.RaqeemTheme

enum class AppDestination {
    SPLASH,
    ONBOARDING,
    DASHBOARD
}

@Composable
@Preview
@DevelopmentEntryPoint
fun App() {
    val viewModel = remember { ExpenseViewModel() }
    var currentDestination by remember { mutableStateOf(AppDestination.SPLASH) }

    RaqeemTheme {
        Crossfade(targetState = currentDestination) { destination ->
            when (destination) {
                AppDestination.SPLASH -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentDestination = AppDestination.ONBOARDING
                        }
                    )
                }
                AppDestination.ONBOARDING -> {
                    OnboardingScreen(
                        onFinishOnboarding = {
                            currentDestination = AppDestination.DASHBOARD
                        }
                    )
                }
                AppDestination.DASHBOARD -> {
                    RaqeemDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}

package org.moh.raqeem.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.moh.raqeem.project.feature.expense.ExpenseViewModel
import org.moh.raqeem.project.feature.expense.ui.RaqeemDashboardScreen
import org.moh.raqeem.project.theme.RaqeemTheme

@Composable
@Preview
@DevelopmentEntryPoint
fun App() {
    val viewModel = remember { ExpenseViewModel() }

    RaqeemTheme {
        RaqeemDashboardScreen(viewModel = viewModel)
    }
}

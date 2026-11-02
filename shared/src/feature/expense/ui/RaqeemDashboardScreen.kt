package org.moh.raqeem.project.feature.expense.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.moh.raqeem.project.feature.expense.*
import org.moh.raqeem.project.theme.CoralExpense
import org.moh.raqeem.project.theme.EmeraldDark
import org.moh.raqeem.project.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaqeemDashboardScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var pastedSmsText by remember { mutableStateOf("") }
    var inputApiKey by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExpenseEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ExpenseEffect.RequestAudioPermission -> {
                    // Handled at platform host layer
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "رقيم",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (state.isOnlineAiActive) EmeraldPrimary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (state.isOnlineAiActive) "Gemini 2.5 Flash ⚡" else "وضع ذكي محلي",
                                fontSize = 11.sp,
                                color = if (state.isOnlineAiActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Bank SMS Notification Paste Button
                    IconButton(onClick = { viewModel.onAction(ExpenseAction.SetBankNotificationSheetVisible(true)) }) {
                        Text("📩", fontSize = 20.sp)
                    }

                    // Settings / API Key Button
                    IconButton(onClick = { viewModel.onAction(ExpenseAction.SetApiKeyDialogVisible(true)) }) {
                        Text("⚙️", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 24.dp, top = 8.dp)
            ) {
                VoiceRecordButton(
                    status = state.recordingStatus,
                    amplitude = state.recordingAmplitude,
                    durationSec = state.recordingDurationSec,
                    onStartRecording = { viewModel.onAction(ExpenseAction.StartVoiceRecording) },
                    onStopRecording = { viewModel.onAction(ExpenseAction.StopVoiceRecording) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Weekly Pulse Radar Header
            item {
                WeeklyPulseHeader(
                    dailyTotal = state.dailyTotal,
                    weeklyBudget = state.weeklyBudgetTotal,
                    currency = state.currency
                )
            }

            // 1.5 Weekly Analytics & Payer Distribution Card
            state.weeklyAnalytics?.let { analytics ->
                item {
                    WeeklyAnalyticsCard(
                        analytics = analytics,
                        currency = state.currency
                    )
                }
            }

            // 2. Extracted Smart Expense Card (Animated popup)
            item {
                AnimatedVisibility(
                    visible = state.pendingReviewBatch != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    state.pendingReviewBatch?.let { batch ->
                        ExpenseReviewCard(
                            batch = batch,
                            onConfirm = { viewModel.onAction(ExpenseAction.ConfirmBatch(it)) },
                            onDismiss = { viewModel.onAction(ExpenseAction.DismissPendingBatch) }
                        )
                    }
                }
            }

            // 3. Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مصاريف اليوم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${state.recentExpenses.size} عمليات",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // 4. List of Transactions
            if (state.recentExpenses.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    ) {
                        Text(
                            text = "لا توجد مصاريف مسجلة لليوم.\nاضغط زر الميكروفون وتكلم بحرية أو الصق إشعار البنك!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                items(state.recentExpenses, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.background)
                                ) {
                                    Text(text = item.category.iconEmoji, fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "دفعها: ${item.paidBy} • ${item.category.arabicLabel}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "-${item.amount.toInt()} ${state.currency}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CoralExpense
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.onAction(ExpenseAction.DeleteExpense(item.id)) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text(text = "✕", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bank Notification Paste Dialog
    if (state.isBankNotificationSheetOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ExpenseAction.SetBankNotificationSheetVisible(false)) },
            title = {
                Text(text = "📩 استخراج من إشعار بنكي", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "الصق نص رسالة البنك (SMS) ليتم استخراج المصروف تلقائياً:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pastedSmsText,
                        onValueChange = { pastedSmsText = it },
                        placeholder = { Text("مثال: تم خصم مبلغ 65.00 ريال لدى كودو...") },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onAction(ExpenseAction.ParseBankNotification(pastedSmsText))
                        pastedSmsText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("استخراج المصروف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ExpenseAction.SetBankNotificationSheetVisible(false)) }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Gemini API Key Dialog
    if (state.isApiKeyDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ExpenseAction.SetApiKeyDialogVisible(false)) },
            title = {
                Text(text = "⚡ إعداد Google Gemini 2.5 Flash", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "أدخل مفتاح Gemini API Key لتفعيل الفهم الصوتي الفوري عبر السحابة:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputApiKey,
                        onValueChange = { inputApiKey = it },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ExpenseAction.SaveApiKey(inputApiKey)) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("حفظ وتفعيل", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ExpenseAction.SetApiKeyDialogVisible(false)) }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

package org.moh.raqeem.project.feature.expense.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import org.moh.raqeem.project.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaqeemDashboardScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var pastedSmsText by remember { mutableStateOf("") }
    var realVoiceInputText by remember { mutableStateOf("") }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.onAction(ExpenseAction.SetEngineSettingsVisible(true))
                        }
                    ) {
                        Text(
                            text = "رقيم",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            LockShieldIcon(
                                color = EmeraldPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Whisper.cpp محلي",
                                fontSize = 11.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Quick Voice/Text Dictation Input Button
                    IconButton(onClick = { viewModel.onAction(ExpenseAction.SetVoiceInputSheetVisible(true)) }) {
                        MicrophoneVectorIcon(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bank SMS Notification Paste Button
                    IconButton(onClick = { viewModel.onAction(ExpenseAction.SetBankNotificationSheetVisible(true)) }) {
                        MailSmsIcon(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Local Whisper Settings Button
                    IconButton(onClick = { viewModel.onAction(ExpenseAction.SetEngineSettingsVisible(true)) }) {
                        SettingsGearIcon(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Weekly Pulse Radar Header
            item {
                WeeklyPulseHeader(
                    dailyTotal = state.dailyTotal,
                    weeklyBudget = state.weeklyBudgetTotal,
                    currency = state.currency
                )
            }

            // 2. Pending Review Card (Animates in when Voice / SMS parses an ExpenseBatch)
            item {
                AnimatedVisibility(
                    visible = state.pendingReviewBatch != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
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

            // 3. Weekly Multi-Payer & Category Analytics
            item {
                state.weeklyAnalytics?.let { analytics ->
                    WeeklyAnalyticsCard(analytics = analytics, currency = state.currency)
                }
            }

            // 4. Section Title: Recent Transactions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مصروفات اليوم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${state.recentExpenses.size} عمليات",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // 5. Recent Expenses List
            items(state.recentExpenses, key = { it.id }) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                CategoryIcon(
                                    category = item.category,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "دفع: ${item.paidBy} • مستفيد: ${item.beneficiary}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${item.amount.toInt()} ${state.currency}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CoralExpense
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = { viewModel.onAction(ExpenseAction.DeleteExpense(item.id)) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("✕", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }

            // Bottom space for floating audio button
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Direct Voice/Text Input Modal (For entering or dictating real spoken sentences)
    if (state.isVoiceInputSheetOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ExpenseAction.SetVoiceInputSheetVisible(false)) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MicrophoneVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "إدخال صوتي وكتابي مباشر", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "اكتب أو أملِ ما صرفته بأي لهجة عامية (مثال: 'دفعت 50 شيكل بنزين ومحمد حاسب 20 شيكل قهوة'):",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = realVoiceInputText,
                        onValueChange = { realVoiceInputText = it },
                        placeholder = { Text("عبيت بنزين 91 بخمسين شيكل وحاسبت قهوة بـ 18...") },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onAction(ExpenseAction.SubmitRealVoiceText(realVoiceInputText))
                        realVoiceInputText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("تحليل فوري محلياً", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ExpenseAction.SetVoiceInputSheetVisible(false)) }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Bank SMS Paste Dialog (Offline Local)
    if (state.isBankNotificationSheetOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ExpenseAction.SetBankNotificationSheetVisible(false)) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MailSmsIcon(color = EmeraldPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "تحليل إشعار بنكي محلياً", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "الصق نص رسالة البنك ليتم استخراج المبلغ بالشيكل (₪) والتصنيف محلياً فوراً:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pastedSmsText,
                        onValueChange = { pastedSmsText = it },
                        placeholder = { Text("مثال: تم خصم مبلغ 65.00 شيكل لدى سوبرماركت...") },
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
                    Text("استخراج محلياً", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ExpenseAction.SetBankNotificationSheetVisible(false)) }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Local Whisper.cpp Settings Dialog
    if (state.isEngineSettingsOpen) {
        LocalWhisperSettingsDialog(
            currentModel = state.activeWhisperModel,
            onModelSelected = { model ->
                viewModel.onAction(ExpenseAction.SelectWhisperModel(model))
            },
            onDismiss = {
                viewModel.onAction(ExpenseAction.SetEngineSettingsVisible(false))
            }
        )
    }
}

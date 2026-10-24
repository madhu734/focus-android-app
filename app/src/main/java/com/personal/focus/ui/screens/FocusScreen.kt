package com.personal.focus.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.personal.focus.FocusApplication
import com.personal.focus.data.model.FocusMode
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.services.MyMonitorService
import com.personal.focus.ui.components.PermissionChecklistCard
import com.personal.focus.ui.launcher.ProxyLauncherActivity
import com.personal.focus.ui.overlay.AppBlockOverlayManager
import com.personal.focus.ui.theme.DarkSurface
import com.personal.focus.ui.theme.ErrorToastBackground
import com.personal.focus.ui.theme.GiantTimerTextStyle
import com.personal.focus.ui.theme.Gold
import com.personal.focus.ui.theme.Neutral0
import com.personal.focus.ui.theme.Neutral10
import com.personal.focus.ui.theme.Neutral100
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen
import com.personal.focus.ui.theme.SecondaryOrange
import kotlinx.coroutines.launch

/**
 * Flexible 2-layer verification matcher for ending a focus session early.
 * Ignores case, extra whitespace, and all punctuation marks (.,!?' etc.).
 */
fun isGiveUpVerificationPhraseMatched(input: String): Boolean {
    val cleaned = input
        .lowercase()
        .replace(Regex("[^a-z0-9\\s]"), " ")
        .trim()
        .replace(Regex("\\s+"), " ")
    return cleaned == "sorry syllabus sesh hoynai" ||
            cleaned.replace(" ", "") == "sorrysyllabusseshhoynai"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    remember(context) { FocusApplication.ensureInitialized(context) }
    val focusRepo = FocusStateRepository.focusTimerRepository ?: FocusApplication.instance.focusTimerRepository
    val ytRepo = FocusStateRepository.youTubeStudyRepository ?: FocusApplication.instance.youTubeStudyRepository

    val activeSession by focusRepo.activeSession.collectAsState()
    val remainingMillis by focusRepo.remainingTimeMillis.collectAsState()
    val isStudyModeActive by ytRepo.isStudyModeEnabled.collectAsState()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            MyMonitorService.startService(context)
        }
    }

    // Default mode is COUNTDOWN (first option on the left)
    var selectedMode by remember { mutableStateOf(FocusMode.COUNTDOWN) }
    var durationMinutes by remember { mutableLongStateOf(25L) }

    // Ultra Focus Settings & Break States
    var isLockedIn by remember { mutableStateOf(false) }
    var disableEndButton by remember { mutableStateOf(false) }
    var isUninstallBlocked by remember { mutableStateOf(true) }
    var isLauncherBlocked by remember { mutableStateOf(false) }
    var blockNotifications by remember { mutableStateOf(true) }
    var breaksAllowedCount by remember { mutableIntStateOf(1) }

    // Bottom Sheet & Give Up Confirmation Dialog Control
    var showUltraFocusSheet by remember { mutableStateOf(false) }
    var showGiveUpDialog by remember { mutableStateOf(false) }
    var giveUpInputText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral0)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // =========================================================================
        // PERMISSION SETUP CHECKLIST CARD (Compact)
        // =========================================================================
        PermissionChecklistCard()

        Spacer(modifier = Modifier.height(10.dp))

        // =========================================================================
        // HEADER ROW: INSTANT FOCUS (Left) + Ultra Focus ⚙ (Right, #83D65C)
        // =========================================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "INSTANT FOCUS",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                if (isLockedIn) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Gold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "STRICT",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Button(
                onClick = { showUltraFocusSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("configure_ultra_focus_button")
            ) {
                Text(
                    text = "Ultra Focus ⚙",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeSession != null && (activeSession?.sessionStatus == SessionStatus.ACTIVE || activeSession?.sessionStatus == SessionStatus.PAUSED)) {
            // =========================================================================
            // ACTIVE SESSION RUNTIME VIEW
            // =========================================================================
            val session = activeSession!!
            val minutes = (remainingMillis / 1000L) / 60
            val seconds = (remainingMillis / 1000L) % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)
            val breaksRemaining = (session.breaksAllowedCount - session.breaksTakenCount).coerceAtLeast(0)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (session.isInBreak) {
                        Text(
                            text = "SHORT BREAK TIME",
                            color = SecondaryOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    } else {
                        Text(
                            text = if (session.isLockedIn) "STRICT LOCK-IN ACTIVE" else "SESSION IN PROGRESS",
                            color = if (session.isLockedIn) ErrorToastBackground else PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formattedTime,
                        style = GiantTimerTextStyle,
                        color = if (session.isInBreak) SecondaryOrange else PrimaryGreen
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (session.mode == FocusMode.POMODORO) {
                            "Mode: ${session.mode.name} • Round ${session.currentRound} / ${session.roundsPerCycle}"
                        } else {
                            "Mode: ${session.mode.name}"
                        },
                        color = Neutral90,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (session.isLockedIn) {
                            "Breaks: Locked (Strict Mode)"
                        } else if (session.isInBreak) {
                            "Break ${session.breaksTakenCount} of ${session.breaksAllowedCount} active • Focus resumes automatically"
                        } else {
                            "Breaks Remaining: $breaksRemaining / ${session.breaksAllowedCount}"
                        },
                        color = if (session.isInBreak) SecondaryOrange else Neutral90,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (session.isLockedIn) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Strict Mode Enforced • Tampering Protected",
                            color = Gold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Session Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!session.isInBreak && breaksRemaining > 0 && !session.isLockedIn) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val took = focusRepo.takeBreak()
                                        if (took) {
                                            AppBlockOverlayManager.dismissOverlay()
                                        } else {
                                            Toast.makeText(context, "No breaks remaining", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("take_break_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Take Break ($breaksRemaining)",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else if (session.isInBreak) {
                            Button(
                                onClick = { scope.launch { focusRepo.endBreak() } },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("end_break_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "End Break & Resume Focus",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        if (!session.isLockedIn && !session.isInBreak) {
                            if (session.sessionStatus == SessionStatus.ACTIVE) {
                                Button(
                                    onClick = { scope.launch { focusRepo.pauseSession() } },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("pause_session_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Neutral10),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "Pause", color = Neutral100)
                                }
                            } else {
                                Button(
                                    onClick = { scope.launch { focusRepo.resumeSession() } },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("resume_session_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "Resume", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Give Up / Complete
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!session.isLockedIn || !session.disableEndButtonShown) {
                        OutlinedButton(
                            onClick = {
                                giveUpInputText = ""
                                showGiveUpDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("give_up_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Give Up / End Session", color = Neutral90)
                        }
                    } else {
                        Text(
                            text = "End button disabled by Strict Mode",
                            color = Neutral90,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        } else {
            // =========================================================================
            // COMPACT TIMER SETUP & GIANT DIGITS PREVIEW (Fits on 1 screen without scroll)
            // =========================================================================

            val previewMinutes = if (selectedMode == FocusMode.STOPWATCH) 0L else durationMinutes
            val previewSeconds = 0L
            val previewFormatted = String.format("%02d:%02d", previewMinutes, previewSeconds)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (selectedMode) {
                            FocusMode.COUNTDOWN -> "COUNTDOWN TIMER"
                            FocusMode.STOPWATCH -> "STOPWATCH COUNT-UP"
                            FocusMode.POMODORO -> "POMODORO INTERVAL"
                        },
                        color = Neutral90,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (selectedMode == FocusMode.STOPWATCH) "00:00" else previewFormatted,
                        style = GiantTimerTextStyle.copy(
                            fontSize = 60.sp,
                            lineHeight = 64.sp
                        ),
                        color = PrimaryGreen
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (selectedMode == FocusMode.STOPWATCH) {
                            "Runs indefinitely until stopped"
                        } else {
                            "$durationMinutes minutes focused work"
                        },
                        color = Neutral90,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Mode Selector: COUNTDOWN (left) | STOPWATCH (middle) | POMODORO (right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(FocusMode.COUNTDOWN, FocusMode.STOPWATCH, FocusMode.POMODORO).forEach { mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PrimaryGreen else Color.Transparent)
                            .clickable { selectedMode = mode }
                            .padding(vertical = 10.dp)
                            .testTag("mode_tab_${mode.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name,
                            color = if (isSelected) Color.Black else Neutral90,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Duration Selector (for Countdown / Pomodoro): 15m, 25m, 45m, 60m, 90m, 120m
            if (selectedMode != FocusMode.STOPWATCH) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                        Text(
                            text = "FOCUS DURATION",
                            color = Neutral90,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(15L, 25L, 45L, 60L, 90L, 120L).forEach { mins ->
                                val isSelected = durationMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryGreen else Neutral10)
                                        .clickable { durationMinutes = mins }
                                        .padding(vertical = 9.dp)
                                        .testTag("duration_${mins}m"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        color = if (isSelected) Color.Black else Neutral100,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 3. Break Selection Card (0 to 4 Short Breaks, locked when Strict Mode is ON)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BREAKS ALLOWED (5M SHORT BREAK)",
                            color = Neutral90,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isLockedIn) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Gold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LOCKED IN STRICT MODE",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val effectiveBreaks = if (isLockedIn) 0 else breaksAllowedCount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0, 1, 2, 3, 4).forEach { count ->
                            val isSelected = effectiveBreaks == count
                            val isEnabled = !isLockedIn
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isSelected && isEnabled -> PrimaryGreen
                                            isSelected && !isEnabled -> Neutral10
                                            else -> Neutral10
                                        }
                                    )
                                    .clickable(enabled = isEnabled) {
                                        breaksAllowedCount = count
                                    }
                                    .padding(vertical = 8.dp)
                                    .testTag("break_option_$count"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count",
                                    color = when {
                                        isSelected && isEnabled -> Color.Black
                                        isSelected && !isEnabled -> Gold
                                        !isEnabled -> Neutral90.copy(alpha = 0.45f)
                                        else -> Neutral100
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when {
                            isLockedIn -> "Breaks are locked to 0 while Strict Mode is enabled"
                            effectiveBreaks == 0 -> "No breaks allowed during this focus session"
                            else -> "Up to $effectiveBreaks short break${if (effectiveBreaks > 1) "s" else ""} (5m each); focus resumes automatically"
                        },
                        color = if (isLockedIn) Gold else Neutral90,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Start Focus Button
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!isGranted) {
                            try {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } catch (_: Throwable) {
                            }
                        }
                    }
                    scope.launch {
                        val durationMs = if (selectedMode == FocusMode.STOPWATCH) null else durationMinutes * 60 * 1000L
                        focusRepo.startSession(
                            mode = selectedMode,
                            durationInMillis = durationMs,
                            isLockedIn = isLockedIn,
                            disableEndButtonShown = disableEndButton,
                            isUninstallBlocked = isUninstallBlocked,
                            isLauncherBlocked = isLauncherBlocked,
                            blockNotifications = blockNotifications,
                            breaksAllowedCount = if (isLockedIn) 0 else breaksAllowedCount
                        )
                        if (isLauncherBlocked) {
                            ProxyLauncherActivity.setComponentEnabled(context, true)
                        }
                        MyMonitorService.startService(context)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_focus_session_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "START FOCUS SESSION",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }

    // =========================================================================
    // 2-LAYER GIVE UP CONFIRMATION DIALOG ("sorry syllabus sesh hoynai")
    // =========================================================================
    if (showGiveUpDialog) {
        val isPhraseMatched = isGiveUpVerificationPhraseMatched(giveUpInputText)

        AlertDialog(
            onDismissRequest = {
                showGiveUpDialog = false
                giveUpInputText = ""
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "End Focus Session Early?",
                    color = Neutral100,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Do you really want to stop your focus session before completion? Your elapsed focus time so far will still be saved to your stats.",
                        color = Neutral90,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "To confirm, type the phrase below:",
                        color = Neutral90,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"sorry syllabus sesh hoynai\"",
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = giveUpInputText,
                        onValueChange = { giveUpInputText = it },
                        placeholder = {
                            Text(
                                text = "sorry syllabus sesh hoynai",
                                color = Neutral90.copy(alpha = 0.5f),
                                fontSize = 13.sp
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Neutral100,
                            unfocusedTextColor = Neutral100,
                            focusedBorderColor = if (isPhraseMatched) PrimaryGreen else Gold,
                            unfocusedBorderColor = Neutral10,
                            cursorColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("give_up_verification_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isPhraseMatched) {
                            scope.launch {
                                val result = focusRepo.giveUpSession()
                                if (result.isFailure) {
                                    Toast.makeText(
                                        context,
                                        result.exceptionOrNull()?.message ?: "Cannot end session",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    showGiveUpDialog = false
                                    giveUpInputText = ""
                                    ProxyLauncherActivity.setComponentEnabled(context, false)
                                    AppBlockOverlayManager.dismissOverlay()
                                }
                            }
                        }
                    },
                    enabled = isPhraseMatched,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorToastBackground,
                        disabledContainerColor = Neutral10
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_give_up_button")
                ) {
                    Text(
                        text = "Confirm & Stop",
                        color = if (isPhraseMatched) Neutral100 else Neutral90.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showGiveUpDialog = false
                        giveUpInputText = ""
                    }
                ) {
                    Text(
                        text = "Keep Focusing",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        )
    }

    // =========================================================================
    // ULTRA FOCUS SETTINGS MODAL BOTTOM SHEET
    // =========================================================================
    if (showUltraFocusSheet) {
        ModalBottomSheet(
            onDismissRequest = { showUltraFocusSheet = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ULTRA FOCUS SETTINGS",
                        color = PrimaryGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Close",
                        color = Neutral90,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { showUltraFocusSheet = false }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Toggle 1: Strict Mode
                SettingToggleRow(
                    title = "Strict Mode (Locked-In)",
                    subtitle = "Prevents early cancel, locks breaks to 0, and protects against clock tampering",
                    checked = isLockedIn,
                    onCheckedChange = {
                        isLockedIn = it
                        if (it) {
                            disableEndButton = true
                            breaksAllowedCount = 0
                            isUninstallBlocked = true
                        } else if (breaksAllowedCount == 0) {
                            breaksAllowedCount = 1
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle 2: YouTube Study Mode
                SettingToggleRow(
                    title = "YouTube Study Mode",
                    subtitle = "Blocks Shorts via spatial heuristic and deflects Home algorithm feed",
                    checked = isStudyModeActive,
                    onCheckedChange = {
                        ytRepo.setStudyModeEnabled(it)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle 3: Uninstall & Anti-Tampering Protection
                SettingToggleRow(
                    title = "Uninstall & Tampering Protection",
                    subtitle = "Shields Android Settings App Info & prevents Device Admin deactivation",
                    checked = isUninstallBlocked,
                    onCheckedChange = { isUninstallBlocked = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle 4: Silence Notifications
                SettingToggleRow(
                    title = "Silence Distracting Notifications",
                    subtitle = "Silently cancels incoming distractions while preserving phone calls",
                    checked = blockNotifications,
                    onCheckedChange = { blockNotifications = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle 5: Trap Home Screen
                SettingToggleRow(
                    title = "Block Home Screen Navigation",
                    subtitle = "Traps launcher home navigation with ProxyLauncher overlay during focus",
                    checked = isLauncherBlocked,
                    onCheckedChange = { isLauncherBlocked = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showUltraFocusSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Apply Settings",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Neutral100,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                color = Neutral90,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Neutral0,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = Neutral90,
                uncheckedTrackColor = Neutral10
            )
        )
    }
}

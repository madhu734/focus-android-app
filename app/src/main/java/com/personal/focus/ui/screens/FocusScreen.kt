package com.personal.focus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.focus.FocusApplication
import com.personal.focus.data.model.FocusMode
import com.personal.focus.data.model.SessionStatus
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val app = FocusApplication.instance
    val focusRepo = app.focusTimerRepository
    val ytRepo = app.youTubeStudyRepository

    val activeSession by focusRepo.activeSession.collectAsState()
    val remainingMillis by focusRepo.remainingTimeMillis.collectAsState()
    val isStudyModeActive by ytRepo.isStudyModeEnabled.collectAsState()

    var selectedMode by remember { mutableStateOf(FocusMode.POMODORO) }
    var durationMinutes by remember { mutableLongStateOf(25L) }

    // Ultra Focus Settings States
    var isLockedIn by remember { mutableStateOf(false) }
    var disableEndButton by remember { mutableStateOf(false) }
    var isUninstallBlocked by remember { mutableStateOf(true) }
    var isLauncherBlocked by remember { mutableStateOf(false) }
    var blockNotifications by remember { mutableStateOf(true) }
    var breaksAllowedCount by remember { mutableIntStateOf(1) }

    // Bottom Sheet Control
    var showUltraFocusSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral0)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // =========================================================================
        // PERMISSION SETUP CHECKLIST CARD
        // =========================================================================
        PermissionChecklistCard()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "INSTANT FOCUS",
            color = PrimaryGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSession != null && (activeSession?.sessionStatus == SessionStatus.ACTIVE || activeSession?.sessionStatus == SessionStatus.PAUSED)) {
            // =========================================================================
            // ACTIVE SESSION RUNTIME VIEW
            // =========================================================================
            val session = activeSession!!
            val minutes = (remainingMillis / 1000L) / 60
            val seconds = (remainingMillis / 1000L) % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (session.isInBreak) {
                        Text(
                            text = "BREAK TIME",
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = formattedTime,
                        style = GiantTimerTextStyle,
                        color = if (session.isInBreak) SecondaryOrange else PrimaryGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Mode: ${session.mode.name} • Round ${session.currentRound} / ${session.roundsPerCycle}",
                        color = Neutral90,
                        fontSize = 14.sp
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Session Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!session.isInBreak && session.breaksTakenCount < session.breaksAllowedCount && !session.isLockedIn) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val took = focusRepo.takeBreak()
                                        if (!took) {
                                            Toast.makeText(context, "No breaks remaining", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Neutral10),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Take Break", color = Neutral100)
                            }
                        } else if (session.isInBreak) {
                            Button(
                                onClick = { scope.launch { focusRepo.endBreak() } },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "End Break", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (!session.isLockedIn) {
                            if (session.sessionStatus == SessionStatus.ACTIVE) {
                                Button(
                                    onClick = { scope.launch { focusRepo.pauseSession() } },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Neutral10),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "Pause", color = Neutral100)
                                }
                            } else {
                                Button(
                                    onClick = { scope.launch { focusRepo.resumeSession() } },
                                    modifier = Modifier.weight(1f),
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
                                scope.launch {
                                    val result = focusRepo.giveUpSession()
                                    if (result.isFailure) {
                                        Toast.makeText(
                                            context,
                                            result.exceptionOrNull()?.message ?: "Cannot end session",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        ProxyLauncherActivity.setComponentEnabled(context, false)
                                        AppBlockOverlayManager.dismissOverlay()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
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
            // TIMER SETUP & GIANT DIGITS PREVIEW (Anton Typography)
            // =========================================================================

            // Preview Display
            val previewMinutes = if (selectedMode == FocusMode.STOPWATCH) 0L else durationMinutes
            val previewSeconds = 0L
            val previewFormatted = String.format("%02d:%02d", previewMinutes, previewSeconds)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (selectedMode) {
                            FocusMode.POMODORO -> "POMODORO INTERVAL"
                            FocusMode.STOPWATCH -> "STOPWATCH COUNT-UP"
                            FocusMode.COUNTDOWN -> "COUNTDOWN TIMER"
                        },
                        color = Neutral90,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (selectedMode == FocusMode.STOPWATCH) "00:00" else previewFormatted,
                        style = GiantTimerTextStyle,
                        color = PrimaryGreen
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (selectedMode == FocusMode.STOPWATCH) {
                            "Runs indefinitely until stopped"
                        } else {
                            "$durationMinutes minutes focused work"
                        },
                        color = Neutral90,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(FocusMode.POMODORO, FocusMode.STOPWATCH, FocusMode.COUNTDOWN).forEach { mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryGreen else Color.Transparent)
                            .clickable { selectedMode = mode }
                            .padding(vertical = 12.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Duration Selector (for Pomodoro / Countdown)
            if (selectedMode != FocusMode.STOPWATCH) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "FOCUS DURATION",
                            color = Neutral90,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15L, 25L, 45L, 60L, 90L).forEach { mins ->
                                val isSelected = durationMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryGreen else Neutral10)
                                        .clickable { durationMinutes = mins }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        color = if (isSelected) Color.Black else Neutral100,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Ultra Focus Settings Entry Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showUltraFocusSheet = true },
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ULTRA FOCUS SETTINGS",
                                color = PrimaryGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = buildString {
                                append(if (isLockedIn) "Strict: ON • " else "Strict: OFF • ")
                                append(if (isStudyModeActive) "YouTube Study: ON • " else "YT Study: OFF • ")
                                append(if (isUninstallBlocked) "Shield: ON" else "Shield: OFF")
                            },
                            color = Neutral90,
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = { showUltraFocusSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Neutral10),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Configure", color = PrimaryGreen, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Focus Button
            Button(
                onClick = {
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
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "START FOCUS SESSION",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }
        }
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

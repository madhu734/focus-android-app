package com.personal.focus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.focus.FocusApplication
import com.personal.focus.data.model.AppBlockRule
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.data.repository.SelectableAppInfo
import com.personal.focus.ui.theme.DarkSurface
import com.personal.focus.ui.theme.ErrorToastBackground
import com.personal.focus.ui.theme.Gold
import com.personal.focus.ui.theme.Neutral0
import com.personal.focus.ui.theme.Neutral10
import com.personal.focus.ui.theme.Neutral100
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    remember(context) { FocusApplication.ensureInitialized(context) }
    val blockRepo = FocusStateRepository.appBlockRepository ?: FocusApplication.instance.appBlockRepository
    val ytRepo = FocusStateRepository.youTubeStudyRepository ?: FocusApplication.instance.youTubeStudyRepository

    val allRules by blockRepo.allRules.collectAsState()
    val isStudyModeEnabled by ytRepo.isStudyModeEnabled.collectAsState()
    val isShortsBlocked by ytRepo.isShortsBlocked.collectAsState()
    val isHomePageDiversion by ytRepo.isHomePageDiversionEnabled.collectAsState()

    var showAppPickerSheet by remember { mutableStateOf(false) }
    val pickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val installedApps = remember { mutableStateListOf<SelectableAppInfo>() }
    var isLoadingApps by remember { mutableStateOf(false) }

    // Crucial: Only show rules for applications that are actually installed on the device
    val installedRules = remember(allRules, context) {
        val pm = context.packageManager
        allRules.filter { FocusStateRepository.isPackageInstalled(pm, it.packageName) }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            FocusStateRepository.syncInstalledAppsAndDefaults(context)
        }
    }

    // Load installed user applications (excluding system apps) when picker opens
    LaunchedEffect(showAppPickerSheet) {
        if (showAppPickerSheet) {
            isLoadingApps = true
            withContext(Dispatchers.IO) {
                val selectedSet = allRules.map { it.packageName }.toSet()
                val apps = FocusStateRepository.querySelectableUserApps(context, selectedSet)
                withContext(Dispatchers.Main) {
                    installedApps.clear()
                    installedApps.addAll(apps)
                    isLoadingApps = false
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral0)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        item {
            Text(
                text = "BLOCKS & USAGE LIMITS",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // =========================================================================
        // YOUTUBE STUDY MODE CARD (Shorts & Home Recommendation Diversion Only)
        // =========================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "YOUTUBE STUDY MODE",
                        color = PrimaryGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "Enable Study Mode",
                        subtitle = "Blocks YouTube Shorts & diverts homepage feed while allowing regular videos",
                        checked = isStudyModeEnabled,
                        onCheckedChange = { ytRepo.setStudyModeEnabled(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "Block YouTube Shorts",
                        subtitle = "Spatial cluster heuristics detect and deflect short-form reels",
                        checked = isShortsBlocked,
                        enabled = isStudyModeEnabled,
                        onCheckedChange = { ytRepo.setShortsBlocked(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "Divert Home Recommendation Feed",
                        subtitle = "Deflects algorithm recommendations on YouTube homepage",
                        checked = isHomePageDiversion,
                        enabled = isStudyModeEnabled,
                        onCheckedChange = { ytRepo.setHomePageDiversionEnabled(it) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // =========================================================================
        // DISTRACTING APPS & SCREEN LIMITS SECTION
        // =========================================================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISTRACTING APPS & LIMITS",
                    color = Neutral90,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAppPickerSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("select_distracting_apps_button")
                ) {
                    Text(
                        text = "Select Distracting Apps",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (installedRules.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Distracting Apps Selected",
                            color = Neutral100,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'Select Distracting Apps' to choose installed user apps and set daily screentime limits or focus blocks.",
                            color = Neutral90,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(installedRules, key = { it.packageName }) { rule ->
                AppBlockRuleCard(
                    rule = rule,
                    onBlockToggled = { blocked ->
                        scope.launch {
                            blockRepo.setAppBlocked(rule.packageName, rule.appName, blocked, rule.category)
                        }
                    },
                    onLimitChanged = { limitMins ->
                        scope.launch {
                            blockRepo.setDailyLimit(rule.packageName, rule.appName, limitMins, rule.category)
                        }
                    },
                    onWhitelistToggled = { whitelisted ->
                        scope.launch {
                            blockRepo.setWhitelistedDuringFocus(rule.packageName, rule.appName, whitelisted)
                        }
                    },
                    onDelete = {
                        scope.launch {
                            blockRepo.deleteRule(rule)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // =========================================================================
    // DISTRACTING APP PICKER MODAL BOTTOM SHEET (WITH CHECKBOXES)
    // =========================================================================
    if (showAppPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAppPickerSheet = false },
            sheetState = pickerSheetState,
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            var searchQuery by remember { mutableStateOf("") }
            val existingPackages = remember(installedRules) { installedRules.map { it.packageName }.toSet() }

            val filteredApps = remember(searchQuery, installedApps.toList()) {
                if (searchQuery.isBlank()) {
                    installedApps
                } else {
                    installedApps.filter {
                        it.appName.contains(searchQuery, ignoreCase = true) ||
                                it.packageName.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT DISTRACTING APPS",
                        color = PrimaryGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Done",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { showAppPickerSheet = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search installed user apps...", color = Neutral90) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Neutral100,
                        unfocusedTextColor = Neutral100,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Neutral10,
                        focusedContainerColor = Neutral10,
                        unfocusedContainerColor = Neutral10
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoadingApps) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Scanning installed applications...", color = Neutral90, fontSize = 14.sp)
                    }
                } else if (filteredApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No installed user applications found", color = Neutral90, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { appInfo ->
                            val isSelected = existingPackages.contains(appInfo.packageName)
                            val toggleAppSelection: (Boolean) -> Unit = { shouldSelect ->
                                scope.launch {
                                    if (shouldSelect) {
                                        blockRepo.insertOrUpdateRule(
                                            AppBlockRule(
                                                packageName = appInfo.packageName,
                                                appName = appInfo.appName,
                                                isBlocked = true,
                                                dailyLimitMinutes = 30,
                                                category = if (appInfo.isGame) "Game" else "Distracting"
                                            )
                                        )
                                    } else {
                                        blockRepo.deleteRuleByPackage(appInfo.packageName)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Neutral10)
                                    .clickable { toggleAppSelection(!isSelected) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("app_picker_row_${appInfo.packageName}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = appInfo.appName.take(1).uppercase(),
                                            color = PrimaryGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = appInfo.appName,
                                                color = Neutral100,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            )
                                            if (appInfo.isGame) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Gold)
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "GAME",
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = appInfo.packageName,
                                            color = Neutral90,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked -> toggleAppSelection(checked) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryGreen,
                                        checkmarkColor = Color.Black,
                                        uncheckedColor = Neutral90
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBlockRuleCard(
    rule: AppBlockRule,
    onBlockToggled: (Boolean) -> Unit,
    onLimitChanged: (Int) -> Unit,
    onWhitelistToggled: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var limitMinutes by remember(rule.dailyLimitMinutes) { mutableIntStateOf(rule.dailyLimitMinutes) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = rule.appName,
                            color = Neutral100,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (rule.category.equals("Game", ignoreCase = true)) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Gold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "GAME",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = rule.packageName,
                        color = Neutral90,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (rule.isBlocked) "Blocked" else "Allowed",
                        color = if (rule.isBlocked) PrimaryGreen else Neutral90,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = rule.isBlocked,
                        onCheckedChange = onBlockToggled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Neutral0,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedThumbColor = Neutral90,
                            uncheckedTrackColor = Neutral10
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Daily Quota Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Screentime Limit:",
                    color = Neutral90,
                    fontSize = 13.sp
                )
                Text(
                    text = if (limitMinutes == 0) "No Limit" else "${limitMinutes}m",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Slider(
                value = limitMinutes.toFloat(),
                onValueChange = { limitMinutes = it.toInt() },
                onValueChangeFinished = { onLimitChanged(limitMinutes) },
                valueRange = 0f..120f,
                steps = 7,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryGreen,
                    activeTrackColor = PrimaryGreen,
                    inactiveTrackColor = Neutral10
                )
            )

            // Whitelist during Focus Toggle & Remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rule.whitelistDuringFocus,
                        onCheckedChange = onWhitelistToggled,
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryGreen,
                            checkmarkColor = Neutral0,
                            uncheckedColor = Neutral90
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Allow during focus",
                        color = Neutral100,
                        fontSize = 12.sp
                    )
                }

                TextButton(onClick = onDelete) {
                    Text(text = "Remove", color = ErrorToastBackground, fontSize = 12.sp)
                }
            }
        }
    }
}

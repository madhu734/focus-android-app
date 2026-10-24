package com.personal.focus.ui.components

import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.personal.focus.receivers.FocusDeviceAdminReceiver
import com.personal.focus.services.MyMonitorInAppBlockService
import com.personal.focus.ui.theme.DarkSurface
import com.personal.focus.ui.theme.Gold
import com.personal.focus.ui.theme.Neutral0
import com.personal.focus.ui.theme.Neutral10
import com.personal.focus.ui.theme.Neutral100
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen
import com.personal.focus.ui.theme.SecondaryOrange

data class PermissionItem(
    val id: String,
    val title: String,
    val description: String,
    val isGranted: Boolean,
    val onGrant: () -> Unit
)

@Composable
fun PermissionChecklistCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasUsageAccess by remember { mutableStateOf(checkUsageAccess(context)) }
    var hasAccessibility by remember { mutableStateOf(checkAccessibility(context)) }
    var hasNotificationListener by remember { mutableStateOf(checkNotificationListener(context)) }
    var hasOverlayPermission by remember { mutableStateOf(checkOverlayPermission(context)) }
    var hasDeviceAdmin by remember { mutableStateOf(checkDeviceAdmin(context)) }
    var isExpanded by remember { mutableStateOf(false) }

    val adminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        hasDeviceAdmin = checkDeviceAdmin(context)
    }

    // Re-check permissions when returning to app from System Settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasUsageAccess = checkUsageAccess(context)
                hasAccessibility = checkAccessibility(context)
                hasNotificationListener = checkNotificationListener(context)
                hasOverlayPermission = checkOverlayPermission(context)
                hasDeviceAdmin = checkDeviceAdmin(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissions = listOf(
        PermissionItem(
            id = "usage",
            title = "Usage Access",
            description = "Monitors app screentime and enforces daily limits",
            isGranted = hasUsageAccess,
            onGrant = {
                try {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Throwable) {
                }
            }
        ),
        PermissionItem(
            id = "accessibility",
            title = "Accessibility Service",
            description = "Enforces YouTube Study Mode & Anti-Tampering shielding",
            isGranted = hasAccessibility,
            onGrant = {
                try {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Throwable) {
                }
            }
        ),
        PermissionItem(
            id = "notification",
            title = "Notification Blocker",
            description = "Silences distracting notifications during active focus",
            isGranted = hasNotificationListener,
            onGrant = {
                try {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Throwable) {
                }
            }
        ),
        PermissionItem(
            id = "overlay",
            title = "Display Over Other Apps",
            description = "Instantly intercepts blocked apps with zero animation",
            isGranted = hasOverlayPermission,
            onGrant = {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Throwable) {
                    try {
                        val fallback = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(fallback)
                    } catch (_: Throwable) {
                    }
                }
            }
        ),
        PermissionItem(
            id = "admin",
            title = "Device Admin Protection",
            description = "Protects Focus from uninstallation during Strict Mode",
            isGranted = hasDeviceAdmin,
            onGrant = {
                try {
                    val component = ComponentName(context, FocusDeviceAdminReceiver::class.java)
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
                        putExtra(
                            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "Protects Focus from uninstallation and tampering during Strict Focus sessions."
                        )
                    }
                    adminLauncher.launch(intent)
                } catch (_: Throwable) {
                }
            }
        )
    )

    val grantedCount = permissions.count { it.isGranted }
    val totalCount = permissions.size
    val allGranted = grantedCount == totalCount

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(if (allGranted) PrimaryGreen else SecondaryOrange)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (allGranted) "ALL PERMISSIONS ACTIVE" else "PERMISSION SETUP CHECKLIST",
                            color = if (allGranted) PrimaryGreen else SecondaryOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (allGranted) {
                            "Full system protection & blocking enabled ($grantedCount/$totalCount)"
                        } else {
                            "$grantedCount of $totalCount permissions granted • Tap Setup to grant"
                        },
                        color = Neutral90,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = if (isExpanded) "Hide ▲" else "Setup ▼",
                    color = PrimaryGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { grantedCount.toFloat() / totalCount.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryGreen,
                trackColor = Neutral10
            )

            // Collapsible items list (collapsed by default so Focus tab fits on one screen)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    permissions.forEach { item ->
                        PermissionItemRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItemRow(item: PermissionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Neutral10)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Checkmark / Cross Icon
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (item.isGranted) PrimaryGreen.copy(alpha = 0.2f) else Neutral0)
                .border(
                    width = 1.dp,
                    color = if (item.isGranted) PrimaryGreen else Neutral90.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (item.isGranted) "✓" else "!",
                color = if (item.isGranted) PrimaryGreen else Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = Neutral100,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = item.description,
                color = Neutral90,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (item.isGranted) {
            Text(
                text = "Active",
                color = PrimaryGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        } else {
            Button(
                onClick = item.onGrant,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 12.dp,
                    vertical = 4.dp
                ),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Grant",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun checkUsageAccess(context: Context): Boolean {
    return try {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        mode == AppOpsManager.MODE_ALLOWED
    } catch (e: Exception) {
        false
    }
}

private fun checkAccessibility(context: Context): Boolean {
    if (MyMonitorInAppBlockService.isAlive) return true
    return try {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        enabledServices.contains(context.packageName)
    } catch (e: Exception) {
        false
    }
}

private fun checkNotificationListener(context: Context): Boolean {
    return try {
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    } catch (e: Exception) {
        false
    }
}

private fun checkOverlayPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(context)
    } else {
        true
    }
}

private fun checkDeviceAdmin(context: Context): Boolean {
    return try {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val component = ComponentName(context, FocusDeviceAdminReceiver::class.java)
        dpm.isAdminActive(component)
    } catch (e: Exception) {
        false
    }
}

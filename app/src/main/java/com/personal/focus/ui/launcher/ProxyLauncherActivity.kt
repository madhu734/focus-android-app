package com.personal.focus.ui.launcher

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.personal.focus.FocusApplication
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.ui.overlay.FTOverlayActivity

/**
 * Proxy Home Launcher Activity registered with CATEGORY_HOME.
 * Traps home button navigation during active locked focus sessions and routes to FTOverlayActivity.
 * When not locked, seamlessly forwards to the device's real launcher.
 */
class ProxyLauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FocusApplication.ensureInitialized(this)

        val activeSession = FocusStateRepository.activeSession.value
            ?: FocusApplication.instanceOrNull?.focusTimerRepository?.activeSession?.value
        val isLauncherBlocked = activeSession != null &&
                activeSession.sessionStatus == SessionStatus.ACTIVE &&
                activeSession.isLauncherBlocked &&
                !activeSession.isInBreak

        if (isLauncherBlocked) {
            try {
                val overlayIntent = Intent(this, FTOverlayActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION
                }
                startActivity(overlayIntent)
            } catch (_: Throwable) {
            }
            finish()
        } else {
            setComponentEnabled(this, false)
            forwardToRealLauncher()
        }
    }

    private fun forwardToRealLauncher() {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            var resolveInfos = packageManager.queryIntentActivities(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            var realLauncher = resolveInfos.firstOrNull { it.activityInfo.packageName != packageName }

            if (realLauncher == null) {
                resolveInfos = packageManager.queryIntentActivities(homeIntent, 0)
                realLauncher = resolveInfos.firstOrNull { it.activityInfo.packageName != packageName }
            }

            if (realLauncher != null) {
                val targetIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    component = ComponentName(
                        realLauncher.activityInfo.packageName,
                        realLauncher.activityInfo.name
                    )
                }
                startActivity(targetIntent)
            }
        } catch (_: Throwable) {
        }
        finish()
    }

    companion object {
        fun setComponentEnabled(context: Context, enabled: Boolean) {
            try {
                val component = ComponentName(context, ProxyLauncherActivity::class.java)
                val newState = if (enabled) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }
                context.packageManager.setComponentEnabledSetting(
                    component,
                    newState,
                    PackageManager.DONT_KILL_APP
                )
            } catch (_: Throwable) {
            }
        }
    }
}

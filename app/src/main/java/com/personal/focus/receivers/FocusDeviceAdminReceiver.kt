package com.personal.focus.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.personal.focus.R
import com.personal.focus.FocusApplication

/**
 * Device Administrator Receiver enforcing uninstallation and tampering protection
 * during active Strict Mode focus sessions.
 */
class FocusDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        val app = try { FocusApplication.instance } catch (e: Exception) { null }
        val isStrictActive = app?.focusTimerRepository?.isStrictActive?.value ?: false
        val isUninstallBlocked = app?.focusTimerRepository?.activeSession?.value?.isUninstallBlocked ?: false

        return if (isStrictActive || isUninstallBlocked) {
            context.getString(R.string.device_admin_disable_warning)
        } else {
            "Disabling Device Admin will remove Focus's uninstallation and tampering protection."
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }
}

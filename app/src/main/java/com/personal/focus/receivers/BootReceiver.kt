package com.personal.focus.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.personal.focus.services.MyMonitorService

/**
 * Restores background watchdog monitoring on device reboot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            MyMonitorService.startService(context)
        }
    }
}

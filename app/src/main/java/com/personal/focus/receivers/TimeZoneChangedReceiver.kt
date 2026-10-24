package com.personal.focus.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.personal.focus.FocusApplication

/**
 * Time zone tampering receiver listening for ACTION_TIMEZONE_CHANGED.
 */
class TimeZoneChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_TIMEZONE_CHANGED) {
            val app = try { FocusApplication.instance } catch (e: Exception) { null }
            app?.focusTimerRepository?.verifyTimeIntegrity()
        }
    }
}

package com.personal.focus.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.personal.focus.FocusApplication

/**
 * Clock tampering defense receiver listening for ACTION_TIME_SET.
 * Ensures session time validation relies on SystemClock.elapsedRealtime()
 * so tampering with device wall-clock does not skip strict lockouts.
 */
class TimeChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_TIME_CHANGED || action == "android.intent.action.TIME_SET" || action == Intent.ACTION_TIME_TICK) {
            val app = try { FocusApplication.instance } catch (e: Exception) { null }
            val repo = app?.focusTimerRepository ?: return
            
            // Check time integrity against SystemClock.elapsedRealtime()
            repo.verifyTimeIntegrity()
        }
    }
}

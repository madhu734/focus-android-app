package com.personal.focus.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.SystemClock
import com.personal.focus.FocusApplication
import com.personal.focus.data.model.SessionStatus
import com.personal.focus.ui.overlay.AppBlockOverlayManager
import com.personal.focus.ui.overlay.BlockProtectionActivity
import com.personal.focus.ui.overlay.BlockShortsOverlayActivity
import com.personal.focus.ui.overlay.OverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

/**
 * Real-Time UI & In-App Content Inspection Accessibility Service.
 * Implements:
 * 1. 150ms debounced window content parsing.
 * 2. YouTube Study Mode (Shorts spatial heuristics with clusterX >= maxRight * 0.75d,
 *    video pausing via play/pause button view ID, channel whitelist inspection, home feed diversion).
 * 3. Settings tampering & uninstall protection launching BlockProtectionActivity.
 */
class MyMonitorInAppBlockService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var debounceJob: Job? = null

    private val lastCheckTime = AtomicLong(0L)
    private var lastInterceptionTime: Long = 0L
    private var lastInterceptionType: String? = null
    private var lastBlockedPackage: String? = null
    private var lastBlockTime: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        isAlive = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val eventType = event.eventType
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {
            return
        }

        val packageName = event.packageName?.toString() ?: return

        // 1. Safety & exclusion checks: ignore Focus, SystemUI, and keyboards
        if (packageName == applicationContext.packageName) {
            AppBlockOverlayManager.dismissOverlay()
            return
        }
        if (packageName == "com.android.systemui" || isKeyboardPackage(packageName)) {
            return
        }
        if (isLauncherPackage(packageName)) {
            AppBlockOverlayManager.dismissOverlay()
            return
        }

        // 2. Atomic timestamp check to prevent event flooding (250ms threshold)
        val now = SystemClock.elapsedRealtime()
        val last = lastCheckTime.get()
        if (now - last < 250L) {
            return
        }
        lastCheckTime.set(now)

        // 3. Dispatch to background coroutine scope
        debounceJob?.cancel()
        debounceJob = serviceScope.launch(Dispatchers.Default) {
            handleDebouncedInspection(packageName)
        }
    }

    private fun isKeyboardPackage(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower.contains("inputmethod") ||
                lower.contains("keyboard") ||
                lower.contains("ime") ||
                lower.contains("latin") ||
                lower.contains("honeyboard") ||
                lower.contains("swiftkey") ||
                lower.contains("gboard")
    }

    private fun isLauncherPackage(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower.contains("launcher") ||
                lower.contains("nexuslauncher") ||
                lower.contains("trebuchet") ||
                lower.contains("home")
    }

    private suspend fun handleDebouncedInspection(packageName: String) {
        val app = try { FocusApplication.instance } catch (e: Exception) { return }
        val focusRepo = app.focusTimerRepository
        val blockRepo = app.appBlockRepository
        val usageRepo = app.usageStatsRepository
        val ytRepo = app.youTubeStudyRepository

        val activeSession = focusRepo.activeSession.value
        val isFocusActive = activeSession != null &&
                activeSession.sessionStatus == SessionStatus.ACTIVE &&
                !activeSession.isInBreak

        // A. Special case: YouTube
        if (packageName == PACKAGE_YOUTUBE) {
            val isStudyModeActive = ytRepo.isStudyModeEnabled.value || isFocusActive || focusRepo.isStrictActive.value
            if (isStudyModeActive) {
                val rootNode = rootInActiveWindow ?: return
                try {
                    inspectYouTubeContent(rootNode, isFocusActive)
                } finally {
                    rootNode.recycle()
                }
                return
            }
        }

        // B. Special case: Settings Tampering & Uninstall Protection
        if (packageName.contains("settings", ignoreCase = true)) {
            val isProtectionActive = focusRepo.isStrictActive.value || (activeSession?.isUninstallBlocked == true)
            if (isProtectionActive) {
                val rootNode = rootInActiveWindow ?: return
                try {
                    inspectSettingsTampering(rootNode)
                } finally {
                    rootNode.recycle()
                }
            }
            return
        }

        // C. General App Interception Logic (DUAL INTERCEPTION)
        // 1. Focus Session Enforcement
        if (isFocusActive) {
            if (blockRepo.isBlockedDuringFocus(packageName)) {
                blockApp(packageName, OverlayActivity.REASON_FOCUS_ACTIVE)
                return
            }
        } else {
            // Permanent App Blocklist enforcement outside focus session
            if (blockRepo.isAppBlockedSync(packageName)) {
                blockApp(packageName, OverlayActivity.REASON_APP_BLOCKED)
                return
            }
        }

        // 2. Daily App Usage Quota Enforcement
        val limitMinutes = blockRepo.getDailyLimitMinutesSync(packageName)
        if (limitMinutes > 0) {
            val usageMs = usageRepo.getPackageTodayUsageMillis(packageName)
            val limitMs = limitMinutes * 60 * 1000L
            if (usageMs >= limitMs) {
                blockApp(packageName, OverlayActivity.REASON_LIMIT_EXCEEDED)
                return
            }
        }
    }

    private fun blockApp(packageName: String, blockReason: String) {
        val now = SystemClock.elapsedRealtime()
        if (packageName == lastBlockedPackage && (now - lastBlockTime) < 1500L) {
            return
        }
        lastBlockedPackage = packageName
        lastBlockTime = now

        // 1. Draw instant full-screen WindowManager overlay directly (immune to BAL)
        AppBlockOverlayManager.showOverlay(this, packageName, blockReason)

        // 2. Also trigger OverlayActivity (AccessibilityService is exempt from BAL restrictions)
        try {
            val intent = Intent(this, OverlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
                putExtra(OverlayActivity.EXTRA_PACKAGE_NAME, packageName)
                putExtra(OverlayActivity.EXTRA_BLOCK_REASON, blockReason)
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Handled by WindowManager overlay
        }
    }

    // =========================================================================
    // YOUTUBE STUDY MODE & SHORTS DETECTION HEURISTICS
    // =========================================================================

    private fun inspectYouTubeContent(rootNode: AccessibilityNodeInfo, isFocusActive: Boolean = false) {
        val app = FocusApplication.instance
        val ytRepo = app.youTubeStudyRepository
        val focusRepo = app.focusTimerRepository

        val isStudyModeActive = ytRepo.isStudyModeEnabled.value || isFocusActive || focusRepo.isStrictActive.value
        if (!isStudyModeActive) return

        val rootBounds = Rect()
        rootNode.getBoundsInScreen(rootBounds)
        val screenWidth = if (rootBounds.right > 0) rootBounds.right else resources.displayMetrics.widthPixels.coerceAtLeast(1)
        val maxRight = screenWidth

        // 1. YouTube Home Feed Diversion Check
        if ((ytRepo.isHomePageDiversionEnabled.value || isFocusActive) && isYTHomePage(rootNode)) {
            triggerShortsOverlay(BlockShortsOverlayActivity.DIVERSION_HOME_FEED)
            return
        }

        // 2. YouTube Shorts Spatial Heuristic Check
        if ((ytRepo.isShortsBlocked.value || isFocusActive) && isShortsContent(rootNode, maxRight)) {
            pauseYouTubeVideo(rootNode)
            triggerShortsOverlay(BlockShortsOverlayActivity.DIVERSION_SHORTS)
            performGlobalAction(GLOBAL_ACTION_BACK)
            return
        }

        // 3. YouTube Channel Whitelist Check
        val channelName = resolveYouTubeChannelName(rootNode)
        if (channelName != null && !ytRepo.isChannelWhitelistedSync(channelName)) {
            pauseYouTubeVideo(rootNode)
            triggerBlockedChannelOverlay(channelName)
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    /**
     * Confirms YouTube Home feed by inspecting for the YouTube logo
     * and presence of "explore menu" or selected "Home" navigation tab.
     */
    private fun isYTHomePage(rootNode: AccessibilityNodeInfo): Boolean {
        // If a video player is active, user is watching a video, not on home feed
        val playButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_PLAY_PAUSE)
        if (playButtons.isNotEmpty()) {
            playButtons.forEach { it.recycle() }
            return false
        }
        val floatyButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_FLOATY_PLAY_PAUSE)
        if (floatyButtons.isNotEmpty()) {
            floatyButtons.forEach { it.recycle() }
            return false
        }

        val allTexts = collectAllNodeTexts(rootNode)
        val hasLogo = allTexts.any { it.contains("YouTube", ignoreCase = true) || it.contains("yt_logo", ignoreCase = true) }
        val hasHomeIndicators = allTexts.any { 
            it.equals("Home", ignoreCase = true) || 
            it.contains("explore menu", ignoreCase = true) ||
            it.equals("Explore", ignoreCase = true) ||
            it.contains("Compass", ignoreCase = true)
        }
        return hasLogo && hasHomeIndicators
    }

    /**
     * Shorts Spatial Heuristic matching Section 3.3 of CLEAN_ARCHITECTURE.md:
     * Actions list: ["like", "dislike", "comment", "share", "remix", "try remix", "like button",
     *                "view comments", "send", "whatsapp", "reactions", "comments", "shares"]
     * Horizontal alignment: clusterX >= maxRight * 0.75d (rightmost 25% of screen width)
     */
    private fun isShortsContent(rootNode: AccessibilityNodeInfo, maxRight: Int): Boolean {
        val actionMatches = mutableListOf<Rect>()
        collectShortsActionBounds(rootNode, actionMatches)

        if (actionMatches.size < 2) return false

        // Check if cluster's horizontal center is on the rightmost 25% of the screen
        val averageClusterX = actionMatches.map { it.centerX() }.average()
        val threshold = maxRight.toDouble() * 0.75

        return averageClusterX >= threshold
    }

    private fun collectShortsActionBounds(node: AccessibilityNodeInfo?, results: MutableList<Rect>) {
        if (node == null) return

        val text = node.text?.toString()?.trim()?.lowercase()
        val contentDesc = node.contentDescription?.toString()?.trim()?.lowercase()

        val matchesText = text != null && SHORTS_ACTION_KEYWORDS.any { text.contains(it) }
        val matchesDesc = contentDesc != null && SHORTS_ACTION_KEYWORDS.any { contentDesc.contains(it) }

        if (matchesText || matchesDesc) {
            val rect = Rect()
            node.getBoundsInScreen(rect)
            results.add(rect)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            collectShortsActionBounds(child, results)
            child?.recycle()
        }
    }

    /**
     * Pauses currently playing YouTube video by finding and clicking
     * the play/pause button view ID.
     */
    private fun pauseYouTubeVideo(rootNode: AccessibilityNodeInfo): Boolean {
        val playPauseButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_PLAY_PAUSE)
        for (btn in playPauseButtons) {
            var clicked = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (!clicked && btn.parent != null) {
                clicked = btn.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            btn.recycle()
            if (clicked) return true
        }

        val floatyButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_FLOATY_PLAY_PAUSE)
        for (btn in floatyButtons) {
            var clicked = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (!clicked && btn.parent != null) {
                clicked = btn.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            btn.recycle()
            if (clicked) return true
        }
        return false
    }

    private fun resolveYouTubeChannelName(rootNode: AccessibilityNodeInfo): String? {
        // 1. Direct view ID lookups in YouTube player/channel headers
        for (viewId in listOf(
            "com.google.android.youtube:id/channel_name",
            "com.google.android.youtube:id/channel_title",
            "com.google.android.youtube:id/owner_name"
        )) {
            val nodes = rootNode.findAccessibilityNodeInfosByViewId(viewId)
            for (node in nodes) {
                val text = node.text?.toString()?.trim()
                node.recycle()
                if (!text.isNullOrBlank()) return text
            }
        }

        val texts = collectAllNodeTexts(rootNode)

        // 2. Look for explicit contentDescription matching "Subscribe to <Channel>" / "Subscribed to <Channel>"
        for (text in texts) {
            val lower = text.lowercase()
            if (lower.startsWith("subscribe to ")) {
                return text.substring("subscribe to ".length).trimEnd('.').trim()
            }
            if (lower.startsWith("subscribed to ")) {
                return text.substring("subscribed to ".length).trimEnd('.').trim()
            }
            if (lower.startsWith("unsubscribe from ")) {
                return text.substring("unsubscribe from ".length).trimEnd('.').trim()
            }
        }

        // 3. Find handles starting with @ (e.g. @veritasium)
        val handle = texts.firstOrNull { it.startsWith("@") && it.length > 2 }
        if (handle != null) return handle

        // 4. Find channel name near Subscribe button
        val subscribeIndex = texts.indexOfFirst { it.equals("Subscribe", ignoreCase = true) || it.equals("Subscribed", ignoreCase = true) }
        if (subscribeIndex > 0) {
            return texts[subscribeIndex - 1]
        }
        return null
    }

    private fun triggerShortsOverlay(diversionType: String) {
        val now = System.currentTimeMillis()
        if (diversionType == lastInterceptionType && (now - lastInterceptionTime) < 1500L) {
            return
        }
        lastInterceptionTime = now
        lastInterceptionType = diversionType

        val reason = if (diversionType == BlockShortsOverlayActivity.DIVERSION_SHORTS) {
            "YouTube Shorts is restricted during Study Mode"
        } else {
            "YouTube Home feed is deflected during Study Mode"
        }
        AppBlockOverlayManager.showOverlay(this, PACKAGE_YOUTUBE, reason)

        val intent = Intent(this, BlockShortsOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NO_ANIMATION
            putExtra(BlockShortsOverlayActivity.EXTRA_DIVERSION_TYPE, diversionType)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {}
    }

    private fun triggerBlockedChannelOverlay(channelName: String) {
        val now = System.currentTimeMillis()
        if ((now - lastInterceptionTime) < 1500L) return
        lastInterceptionTime = now

        val reason = "Channel '$channelName' is not on your approved Study Whitelist"
        AppBlockOverlayManager.showOverlay(this, PACKAGE_YOUTUBE, reason)

        val intent = Intent(this, OverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NO_ANIMATION
            putExtra(OverlayActivity.EXTRA_PACKAGE_NAME, PACKAGE_YOUTUBE)
            putExtra(OverlayActivity.EXTRA_BLOCK_REASON, reason)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {}
    }

    // =========================================================================
    // ANTI-TAMPERING & SETTINGS UNINSTALL PROTECTION
    // =========================================================================

    private fun inspectSettingsTampering(rootNode: AccessibilityNodeInfo) {
        val app = FocusApplication.instance
        val focusRepo = app.focusTimerRepository
        val activeSession = focusRepo.activeSession.value

        // Only enforce when Strict Mode or Uninstall Protection is engaged
        val isProtectionActive = focusRepo.isStrictActive.value || (activeSession?.isUninstallBlocked == true)
        if (!isProtectionActive) return

        if (isProtectedFocusSettingsPage(rootNode)) {
            val now = System.currentTimeMillis()
            if ((now - lastInterceptionTime) < 1500L) return
            lastInterceptionTime = now

            val intent = Intent(this, BlockProtectionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
            }
            startActivity(intent)
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    /**
     * Checks if current settings page targets Focus uninstall, force stop,
     * or device admin deactivation according to Section 3.5 of CLEAN_ARCHITECTURE.md.
     */
    private fun isProtectedFocusSettingsPage(rootNode: AccessibilityNodeInfo): Boolean {
        val allTexts = collectAllNodeTexts(rootNode).map { it.lowercase() }

        val hasFocus = allTexts.any { it.contains("focus") }
        if (!hasFocus) return false

        val hasAppInfoPatterns = allTexts.any { it.contains("uninstall") || it.contains("force stop") || it.contains("app info") }
        val hasAdminPatterns = allTexts.any { it.contains("device admin") || it.contains("device administrator") || it.contains("deactivate") }

        return hasAppInfoPatterns || hasAdminPatterns
    }

    private fun collectAllNodeTexts(node: AccessibilityNodeInfo?): List<String> {
        val list = mutableListOf<String>()
        if (node == null) return list

        node.text?.toString()?.takeIf { it.isNotBlank() }?.let { list.add(it) }
        node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let { list.add(it) }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            list.addAll(collectAllNodeTexts(child))
            child?.recycle()
        }
        return list
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        isAlive = false
        debounceJob?.cancel()
        super.onDestroy()
    }

    companion object {
        @Volatile
        var isAlive: Boolean = false
            private set

        private const val DEBOUNCE_DELAY_MS = 150L
        private const val PACKAGE_YOUTUBE = "com.google.android.youtube"
        private const val ID_YT_PLAY_PAUSE = "com.google.android.youtube:id/player_control_play_pause_replay_button"
        private const val ID_YT_FLOATY_PLAY_PAUSE = "com.google.android.youtube:id/floaty_play_pause_button"

        private val SHORTS_ACTION_KEYWORDS = listOf(
            "like", "dislike", "comment", "share", "remix", "try remix",
            "like button", "view comments", "send", "whatsapp", "reactions", "comments", "shares"
        )
    }
}

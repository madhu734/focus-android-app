package com.personal.focus.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.personal.focus.FocusApplication
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.ui.overlay.AppBlockOverlayManager
import com.personal.focus.ui.overlay.BlockProtectionActivity
import com.personal.focus.ui.overlay.BlockShortsOverlayActivity
import com.personal.focus.ui.overlay.OverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Real-Time UI & In-App Content Inspection Accessibility Service.
 * Implements:
 * 1. Immediate window state change interception + 200ms debounced window content parsing.
 * 2. YouTube Study Mode (Shorts spatial heuristics with clusterX >= maxRight * 0.75d,
 *    video pausing via play/pause button view ID, and home recommendation feed diversion).
 * 3. Settings tampering & uninstall protection launching BlockProtectionActivity.
 * 4. Dual-layer blocked app interception during active focus sessions.
 */
class MyMonitorInAppBlockService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var debounceJob: Job? = null

    private var lastInterceptionTime: Long = 0L
    private var lastInterceptionType: String? = null
    private var lastBlockedPackage: String? = null
    private var lastBlockTime: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        isAlive = true
        FocusApplication.ensureInitialized(applicationContext)
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
            return
        }
        if (packageName == "com.android.systemui" || isKeyboardPackage(packageName)) {
            return
        }
        if (isLauncherPackage(packageName)) {
            AppBlockOverlayManager.dismissOverlay()
            return
        }

        // 2. Immediate inspection on TYPE_WINDOW_STATE_CHANGED, 200ms debounce on TYPE_WINDOW_CONTENT_CHANGED
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            debounceJob?.cancel()
            debounceJob = serviceScope.launch {
                handleDebouncedInspection(packageName)
            }
        } else {
            debounceJob?.cancel()
            debounceJob = serviceScope.launch {
                delay(DEBOUNCE_DELAY_MS)
                handleDebouncedInspection(packageName)
            }
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

    private fun handleDebouncedInspection(packageName: String) {
        try {
            FocusApplication.ensureInitialized(applicationContext)
        } catch (_: Throwable) {
        }

        val activeSession = FocusStateRepository.activeSession.value
        val isFocusActive = FocusStateRepository.isSessionActiveNow()
        val blockRepo = FocusStateRepository.appBlockRepository
        val usageRepo = FocusStateRepository.usageStatsRepository
        val ytRepo = FocusStateRepository.youTubeStudyRepository

        // A. Special case: YouTube (Shorts Spatial Heuristic + Home Feed Diversion + Optional Full App Block)
        if (packageName == PACKAGE_YOUTUBE) {
            val isStudyModeActive = (ytRepo?.isStudyModeEnabled?.value == true)
            val rootNode = try { rootInActiveWindow } catch (_: Throwable) { null }
            if (rootNode != null) {
                try {
                    if (isStudyModeActive) {
                        val interceptedInApp = inspectYouTubeContent(rootNode)
                        if (interceptedInApp) return
                    }
                    if (isFocusActive && FocusStateRepository.isPackageBlockedDuringFocus(PACKAGE_YOUTUBE)) {
                        pauseYouTubeVideo(rootNode)
                    }
                } finally {
                    try { rootNode.recycle() } catch (_: Throwable) {}
                }
            }
            if (isFocusActive && FocusStateRepository.isPackageBlockedDuringFocus(PACKAGE_YOUTUBE)) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                blockApp(PACKAGE_YOUTUBE, OverlayActivity.REASON_FOCUS_ACTIVE)
                return
            }
        }

        // B. Special case: Settings Tampering & Uninstall Protection
        if (packageName.contains("settings", ignoreCase = true)) {
            val isProtectionActive = FocusStateRepository.isStrictActive.value || (activeSession?.isUninstallBlocked == true)
            if (isProtectionActive) {
                val rootNode = try { rootInActiveWindow } catch (_: Throwable) { null } ?: return
                try {
                    inspectSettingsTampering(rootNode)
                } finally {
                    try { rootNode.recycle() } catch (_: Throwable) {}
                }
            }
            return
        }

        // C. General App Interception Logic (DUAL-LAYER INTERCEPTION)
        // Focus Session Enforcement ONLY (Daily Screentime Limit removed)
        if (isFocusActive && FocusStateRepository.isPackageBlockedDuringFocus(packageName)) {
            blockApp(packageName, OverlayActivity.REASON_FOCUS_ACTIVE)
            return
        }
    }

    private fun blockApp(packageName: String, blockReason: String) {
        val now = SystemClock.elapsedRealtime()
        if (packageName == lastBlockedPackage && (now - lastBlockTime) < 1200L) {
            return
        }
        lastBlockedPackage = packageName
        lastBlockTime = now

        // 1. Draw instant full-screen WindowManager overlay directly (immune to BAL)
        AppBlockOverlayManager.showOverlay(this, packageName, blockReason)

        // 2. Launch OverlayActivity with required flags
        try {
            val intent = Intent(this, OverlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
                putExtra(OverlayActivity.EXTRA_PACKAGE_NAME, packageName)
                putExtra(OverlayActivity.EXTRA_BLOCK_REASON, blockReason)
            }
            startActivity(intent)
        } catch (_: Throwable) {
            // Handled by WindowManager overlay fallback
        }
    }

    // =========================================================================
    // YOUTUBE STUDY MODE: SHORTS DETECTION & HOME FEED DIVERSION ONLY
    // (Regular long-form videos are freely allowed regardless of channel)
    // =========================================================================

    private fun inspectYouTubeContent(rootNode: AccessibilityNodeInfo): Boolean {
        val ytRepo = FocusStateRepository.youTubeStudyRepository ?: return false
        if (!ytRepo.isStudyModeEnabled.value) return false

        val rootBounds = Rect()
        rootNode.getBoundsInScreen(rootBounds)
        val screenWidth = if (rootBounds.right > 0) rootBounds.right else resources.displayMetrics.widthPixels.coerceAtLeast(1)
        val maxRight = screenWidth

        // 1. YouTube Shorts Spatial Heuristic Check (clusterX >= maxRight * 0.75)
        if (ytRepo.isShortsBlocked.value && isShortsContent(rootNode, maxRight)) {
            pauseYouTubeVideo(rootNode)
            performGlobalAction(GLOBAL_ACTION_BACK)
            triggerShortsOverlay(BlockShortsOverlayActivity.DIVERSION_SHORTS)
            return true
        }

        // 2. YouTube Home Feed Diversion Check
        if (ytRepo.isHomePageDiversionEnabled.value && isYTHomePage(rootNode)) {
            triggerShortsOverlay(BlockShortsOverlayActivity.DIVERSION_HOME_FEED)
            return true
        }

        return false
    }

    /**
     * Confirms YouTube Home feed by inspecting for the YouTube logo
     * and presence of "explore menu" or selected "Home" navigation tab.
     */
    private fun isYTHomePage(rootNode: AccessibilityNodeInfo): Boolean {
        val playButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_PLAY_PAUSE)
        if (playButtons.isNotEmpty()) {
            playButtons.forEach { try { it.recycle() } catch (_: Throwable) {} }
            return false
        }
        val floatyButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_FLOATY_PLAY_PAUSE)
        if (floatyButtons.isNotEmpty()) {
            floatyButtons.forEach { try { it.recycle() } catch (_: Throwable) {} }
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
            try { child?.recycle() } catch (_: Throwable) {}
        }
    }

    /**
     * Pauses currently playing YouTube video by finding and clicking
     * com.google.android.youtube:id/player_control_play_pause_replay_button.
     */
    private fun pauseYouTubeVideo(rootNode: AccessibilityNodeInfo): Boolean {
        val playPauseButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_PLAY_PAUSE)
        for (btn in playPauseButtons) {
            var clicked = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (!clicked && btn.parent != null) {
                clicked = btn.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            try { btn.recycle() } catch (_: Throwable) {}
            if (clicked) return true
        }

        val floatyButtons = rootNode.findAccessibilityNodeInfosByViewId(ID_YT_FLOATY_PLAY_PAUSE)
        for (btn in floatyButtons) {
            var clicked = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (!clicked && btn.parent != null) {
                clicked = btn.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            try { btn.recycle() } catch (_: Throwable) {}
            if (clicked) return true
        }
        return false
    }

    private fun triggerShortsOverlay(diversionType: String) {
        val now = System.currentTimeMillis()
        if (diversionType == lastInterceptionType && (now - lastInterceptionTime) < 1200L) {
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
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_NO_ANIMATION
            putExtra(BlockShortsOverlayActivity.EXTRA_DIVERSION_TYPE, diversionType)
        }
        try {
            startActivity(intent)
        } catch (_: Throwable) {
        }
    }

    // =========================================================================
    // ANTI-TAMPERING & SETTINGS UNINSTALL PROTECTION
    // =========================================================================

    private fun inspectSettingsTampering(rootNode: AccessibilityNodeInfo) {
        val activeSession = FocusStateRepository.activeSession.value
        val isProtectionActive = FocusStateRepository.isStrictActive.value || (activeSession?.isUninstallBlocked == true)
        if (!isProtectionActive) return

        if (isProtectedFocusSettingsPage(rootNode)) {
            val now = System.currentTimeMillis()
            if ((now - lastInterceptionTime) < 1200L) return
            lastInterceptionTime = now

            performGlobalAction(GLOBAL_ACTION_BACK)
            val intent = Intent(this, BlockProtectionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
            }
            try {
                startActivity(intent)
            } catch (_: Throwable) {
            }
        }
    }

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
            try { child?.recycle() } catch (_: Throwable) {}
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

        private const val DEBOUNCE_DELAY_MS = 200L
        private const val PACKAGE_YOUTUBE = "com.google.android.youtube"
        private const val ID_YT_PLAY_PAUSE = "com.google.android.youtube:id/player_control_play_pause_replay_button"
        private const val ID_YT_FLOATY_PLAY_PAUSE = "com.google.android.youtube:id/floaty_play_pause_button"

        private val SHORTS_ACTION_KEYWORDS = listOf(
            "like", "dislike", "comment", "share", "remix", "try remix",
            "like button", "view comments", "send", "whatsapp", "reactions", "comments", "shares"
        )
    }
}

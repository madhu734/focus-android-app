package com.personal.focus

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests the Settings anti-tampering heuristics matching Section 3.5 of CLEAN_ARCHITECTURE.md.
 */
class SettingsProtectionHeuristicTest {

    private fun isProtectedFocusSettingsPage(nodeTexts: List<String>): Boolean {
        val allTexts = nodeTexts.map { it.lowercase() }
        val hasFocus = allTexts.any { it.contains("focus") }
        if (!hasFocus) return false

        val hasAppInfoPatterns = allTexts.any { it.contains("uninstall") || it.contains("force stop") || it.contains("app info") }
        val hasAdminPatterns = allTexts.any { it.contains("device admin") || it.contains("device administrator") || it.contains("deactivate") }

        return hasAppInfoPatterns || hasAdminPatterns
    }

    @Test
    fun `test app info page with uninstall or force stop triggers protection`() {
        val appInfoNodes = listOf(
            "Settings",
            "App Info",
            "Focus",
            "Version 1.0.0",
            "Uninstall",
            "Force stop",
            "Storage & cache"
        )
        assertTrue(isProtectedFocusSettingsPage(appInfoNodes))
    }

    @Test
    fun `test device admin page with deactivate triggers protection`() {
        val adminNodes = listOf(
            "Device admin apps",
            "Focus Watchdog",
            "Deactivate this device admin app",
            "Cancel"
        )
        assertTrue(isProtectedFocusSettingsPage(adminNodes))
    }

    @Test
    fun `test other apps app info does not trigger focus protection`() {
        val otherAppNodes = listOf(
            "Settings",
            "App Info",
            "Instagram",
            "Uninstall",
            "Force stop"
        )
        assertFalse(isProtectedFocusSettingsPage(otherAppNodes))
    }

    @Test
    fun `test generic settings page does not trigger protection`() {
        val generalSettings = listOf(
            "Settings",
            "Network & internet",
            "Connected devices",
            "Battery",
            "Display"
        )
        assertFalse(isProtectedFocusSettingsPage(generalSettings))
    }
}

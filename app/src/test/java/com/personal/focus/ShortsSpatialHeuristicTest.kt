package com.personal.focus

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests the YouTube Shorts spatial heuristic and action cluster bounds
 * matching Section 3.3 of CLEAN_ARCHITECTURE.md.
 */
class ShortsSpatialHeuristicTest {

    private val shortsKeywords = listOf(
        "like", "dislike", "comment", "share", "remix", "try remix",
        "like button", "view comments", "send", "whatsapp", "reactions", "comments", "shares"
    )

    private fun isShortsSpatialMatch(actionClusterX: Double, maxRight: Int): Boolean {
        return actionClusterX >= (maxRight.toDouble() * 0.75)
    }

    private fun isShortsActionKeyword(text: String): Boolean {
        val clean = text.trim().lowercase()
        return shortsKeywords.any { clean.contains(it) }
    }

    @Test
    fun `test action keywords match standard reels text`() {
        assertTrue(isShortsActionKeyword("like button"))
        assertTrue(isShortsActionKeyword("1.2M likes"))
        assertTrue(isShortsActionKeyword("Comments 4,321"))
        assertTrue(isShortsActionKeyword("Share"))
        assertTrue(isShortsActionKeyword("Remix"))
        assertTrue(isShortsActionKeyword("Dislike"))

        // Non-reels texts
        assertFalse(isShortsActionKeyword("Subscribe"))
        assertFalse(isShortsActionKeyword("Download video"))
        assertFalse(isShortsActionKeyword("Save to playlist"))
    }

    @Test
    fun `test clusterX spatial threshold at 75 percent bounds`() {
        val screenWidth = 1080
        val threshold75 = 1080 * 0.75 // 810.0

        // Exactly at 75% -> Confirmed Shorts
        assertTrue(isShortsSpatialMatch(810.0, screenWidth))

        // Rightmost edge (e.g. 950px in 1080px screen) -> Confirmed Shorts
        assertTrue(isShortsSpatialMatch(950.0, screenWidth))

        // Center / Left content (e.g. 540px or 800px) -> Not Shorts cluster
        assertFalse(isShortsSpatialMatch(809.9, screenWidth))
        assertFalse(isShortsSpatialMatch(540.0, screenWidth))
        assertFalse(isShortsSpatialMatch(100.0, screenWidth))
    }

    @Test
    fun `test tablet and widescreen spatial threshold`() {
        val tabletWidth = 1600
        val threshold = 1600 * 0.75 // 1200.0

        assertTrue(isShortsSpatialMatch(1300.0, tabletWidth))
        assertFalse(isShortsSpatialMatch(1150.0, tabletWidth))
    }

    @Test
    fun `test 720p and 480p compact phone spatial threshold`() {
        val phoneWidth720 = 720
        val threshold720 = 720 * 0.75 // 540.0
        // Action cluster at 600px on a 720px screen is a confirmed match
        assertTrue(isShortsSpatialMatch(600.0, phoneWidth720))
        assertFalse(isShortsSpatialMatch(500.0, phoneWidth720))

        val phoneWidth480 = 480
        val threshold480 = 480 * 0.75 // 360.0
        assertTrue(isShortsSpatialMatch(400.0, phoneWidth480))
        assertFalse(isShortsSpatialMatch(300.0, phoneWidth480))
    }
}

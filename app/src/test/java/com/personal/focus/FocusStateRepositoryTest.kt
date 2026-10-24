package com.personal.focus

import com.personal.focus.data.repository.FocusStateRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FocusStateRepositoryTest {

    @Test
    fun `test FocusStateRepository pre-populates updated default blocked packages excluding Chrome WhatsApp YouTube`() {
        val blocked = FocusStateRepository.DEFAULT_BLOCKED_PACKAGES
        assertTrue(blocked.contains("com.facebook.katana"))
        assertTrue(blocked.contains("com.facebook.lite"))
        assertTrue(blocked.contains("com.facebook.orca"))
        assertTrue(blocked.contains("com.facebook.mlite"))
        assertTrue(blocked.contains("com.imo.android.imoim"))
        assertTrue(blocked.contains("com.imo.android.imoimhd"))
        assertTrue(blocked.contains("com.instagram.android"))
        assertTrue(blocked.contains("com.instagram.lite"))
        assertTrue(blocked.contains("com.netflix.mediaclient"))
        assertTrue(blocked.contains("com.twitter.android"))
        assertTrue(blocked.contains("com.reddit.frontpage"))
        assertTrue(blocked.contains("com.snapchat.android"))
        assertTrue(blocked.contains("com.zhiliaoapp.musically"))
        assertTrue(blocked.contains("com.ss.android.ugc.trill"))

        // Explicitly removed from default list
        assertFalse(blocked.contains("com.android.chrome"))
        assertFalse(blocked.contains("com.whatsapp"))
        assertFalse(blocked.contains("com.google.android.youtube"))
    }

    @Test
    fun `test FocusStateRepository isPackageBlockedDuringFocus matches updated defaults`() {
        assertTrue(FocusStateRepository.isPackageBlockedDuringFocus("com.instagram.android"))
        assertTrue(FocusStateRepository.isPackageBlockedDuringFocus("com.facebook.katana"))
        assertTrue(FocusStateRepository.isPackageBlockedDuringFocus("com.imo.android.imoim"))
        assertTrue(FocusStateRepository.isPackageBlockedDuringFocus("com.zhiliaoapp.musically"))
        assertFalse(FocusStateRepository.isPackageBlockedDuringFocus("com.android.chrome"))
        assertFalse(FocusStateRepository.isPackageBlockedDuringFocus("com.whatsapp"))
        assertFalse(FocusStateRepository.isPackageBlockedDuringFocus("com.google.android.youtube"))
        assertFalse(FocusStateRepository.isPackageBlockedDuringFocus("com.android.calculator2"))
    }

    @Test
    fun `test all font files in res_font have valid TrueType or OpenType headers`() {
        val fontDir = File("src/main/res/font").takeIf { it.exists() }
            ?: File("app/src/main/res/font")
        assertTrue("Font directory must exist", fontDir.exists())

        val fontFiles = fontDir.listFiles { file ->
            file.extension.lowercase() in listOf("ttf", "otf")
        }.orEmpty()
        assertTrue("Expected font files in res/font", fontFiles.isNotEmpty())

        for (file in fontFiles) {
            val bytes = file.readBytes()
            assertTrue("Font ${file.name} should not be empty", bytes.size > 64)
            val isTrueType = bytes[0] == 0x00.toByte() && bytes[1] == 0x01.toByte() &&
                    bytes[2] == 0x00.toByte() && bytes[3] == 0x00.toByte()
            val isOpenType = String(bytes, 0, 4, Charsets.US_ASCII) == "OTTO"
            assertTrue(
                "Font ${file.name} must have valid TTF or OTF magic bytes",
                isTrueType || isOpenType
            )
        }
    }
}

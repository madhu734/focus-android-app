package com.personal.focus

import com.personal.focus.data.dao.StudyChannelDao
import com.personal.focus.data.model.StudyChannel
import com.personal.focus.data.repository.YouTubeStudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeStudyChannelDao(private val initialChannels: MutableList<StudyChannel> = mutableListOf()) : StudyChannelDao {
    override fun getAllChannelsFlow(): Flow<List<StudyChannel>> = flowOf(initialChannels)

    override suspend fun getAllWhitelistedChannels(): List<StudyChannel> = initialChannels.filter { it.isWhitelisted }

    override suspend fun findChannel(name: String, handle: String): StudyChannel? {
        return initialChannels.firstOrNull { it.channelName.equals(name, ignoreCase = true) || it.channelHandle.equals(handle, ignoreCase = true) }
    }

    override suspend fun insertChannel(channel: StudyChannel): Long {
        initialChannels.add(channel)
        return initialChannels.size.toLong()
    }

    override suspend fun deleteChannel(id: Long) {
        initialChannels.removeAll { it.id == id }
    }
}

class YouTubeStudyRepositoryTest {

    @Test
    fun `test channel whitelist matching with handles and names`() = runTest {
        val channels = listOf(
            StudyChannel(id = 1, channelName = "Veritasium", channelHandle = "@veritasium"),
            StudyChannel(id = 2, channelName = "freeCodeCamp.org", channelHandle = "@freecodecamp"),
            StudyChannel(id = 3, channelName = "Huberman Lab", channelHandle = "@hubermanlab")
        )
        val fakeDao = FakeStudyChannelDao(channels.toMutableList())
        val repository = YouTubeStudyRepository(fakeDao, backgroundScope, initialChannels = channels)
        testScheduler.advanceUntilIdle()

        // Matching by exact handle
        assertTrue(repository.isChannelWhitelistedSync("@veritasium"))
        assertTrue(repository.isChannelWhitelistedSync("@freecodecamp"))

        // Matching by name in text
        assertTrue(repository.isChannelWhitelistedSync("Veritasium - 15.2M subscribers"))
        assertTrue(repository.isChannelWhitelistedSync("freeCodeCamp.org"))
        assertTrue(repository.isChannelWhitelistedSync("Huberman Lab Podcast"))

        // Case insensitivity
        assertTrue(repository.isChannelWhitelistedSync("@VERITASIUM"))
        assertTrue(repository.isChannelWhitelistedSync("freecodecamp.org"))

        // Matching without @ prefix
        assertTrue(repository.isChannelWhitelistedSync("veritasium"))
        assertTrue(repository.isChannelWhitelistedSync("freecodecamp"))

        // Extracted subscribe text matching
        assertTrue(repository.isChannelWhitelistedSync("Subscribe to Veritasium"))
        assertTrue(repository.isChannelWhitelistedSync("Subscribed to freeCodeCamp.org"))

        // Non-whitelisted channels
        assertFalse(repository.isChannelWhitelistedSync("Random Gaming Channel"))
        assertFalse(repository.isChannelWhitelistedSync("@entertainmentdaily"))
        assertFalse(repository.isChannelWhitelistedSync(""))
    }

    @Test
    fun `test study mode settings toggles`() = runTest {
        val fakeDao = FakeStudyChannelDao()
        val repository = YouTubeStudyRepository(fakeDao)

        assertTrue(repository.isStudyModeEnabled.value)
        repository.setStudyModeEnabled(false)
        assertFalse(repository.isStudyModeEnabled.value)

        assertTrue(repository.isShortsBlocked.value)
        repository.setShortsBlocked(false)
        assertFalse(repository.isShortsBlocked.value)

        assertTrue(repository.isHomePageDiversionEnabled.value)
        repository.setHomePageDiversionEnabled(false)
        assertFalse(repository.isHomePageDiversionEnabled.value)
    }
}

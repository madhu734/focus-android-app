package com.personal.focus.data.repository

import com.personal.focus.data.dao.StudyChannelDao
import com.personal.focus.data.model.StudyChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Repository managing YouTube Study Mode configuration,
 * whitelisted study channels, shorts blocking, and homepage diversion.
 */
class YouTubeStudyRepository(
    private val studyChannelDao: StudyChannelDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    initialChannels: List<StudyChannel> = emptyList()
) {
    private val _whitelistedChannels = MutableStateFlow<List<StudyChannel>>(initialChannels)
    val whitelistedChannels: StateFlow<List<StudyChannel>> = _whitelistedChannels.asStateFlow()

    private val _isStudyModeEnabled = MutableStateFlow(true)
    val isStudyModeEnabled: StateFlow<Boolean> = _isStudyModeEnabled.asStateFlow()

    private val _isShortsBlocked = MutableStateFlow(true)
    val isShortsBlocked: StateFlow<Boolean> = _isShortsBlocked.asStateFlow()

    private val _isHomePageDiversionEnabled = MutableStateFlow(true)
    val isHomePageDiversionEnabled: StateFlow<Boolean> = _isHomePageDiversionEnabled.asStateFlow()

    init {
        scope.launch {
            studyChannelDao.getAllChannelsFlow().collect { channels ->
                _whitelistedChannels.value = channels
            }
        }
    }

    suspend fun addChannel(channelName: String, channelHandle: String): Long {
        val cleanHandle = if (channelHandle.startsWith("@")) channelHandle else "@$channelHandle"
        val channel = StudyChannel(
            channelName = channelName.trim(),
            channelHandle = cleanHandle.trim(),
            isWhitelisted = true
        )
        val id = studyChannelDao.insertChannel(channel)
        val withId = channel.copy(id = id)
        _whitelistedChannels.value = _whitelistedChannels.value + withId
        return id
    }

    suspend fun removeChannel(id: Long) {
        _whitelistedChannels.value = _whitelistedChannels.value.filter { it.id != id }
        studyChannelDao.deleteChannel(id)
    }

    fun setStudyModeEnabled(enabled: Boolean) {
        _isStudyModeEnabled.value = enabled
    }

    fun setShortsBlocked(blocked: Boolean) {
        _isShortsBlocked.value = blocked
    }

    fun setHomePageDiversionEnabled(enabled: Boolean) {
        _isHomePageDiversionEnabled.value = enabled
    }

    /**
     * Case-insensitive verification whether a given channel text or handle
     * matches any whitelisted study channel.
     */
    fun isChannelWhitelistedSync(channelText: String): Boolean {
        if (channelText.isBlank()) return false
        val clean = channelText.trim().lowercase()
        val channels = whitelistedChannels.value
        return channels.any { item ->
            val nameClean = item.channelName.trim().lowercase()
            val handleClean = item.channelHandle.trim().lowercase()
            val nameMatch = nameClean.isNotBlank() && (clean.contains(nameClean) || nameClean.contains(clean))
            val handleMatch = handleClean.isNotBlank() && (
                clean.contains(handleClean) ||
                handleClean.contains(clean) ||
                clean.removePrefix("@") == handleClean.removePrefix("@")
            )
            nameMatch || handleMatch
        }
    }

    suspend fun seedDefaultsIfEmpty() {
        if (studyChannelDao.getAllWhitelistedChannels().isEmpty()) {
            addChannel("freeCodeCamp.org", "@freecodecamp")
            addChannel("MIT OpenCourseWare", "@mitocw")
            addChannel("3Blue1Brown", "@3blue1brown")
            addChannel("Veritasium", "@veritasium")
            addChannel("Huberman Lab", "@hubermanlab")
        }
    }
}

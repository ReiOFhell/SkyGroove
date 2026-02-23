package com.skygroove

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.skygroove.data.local.entity.PlaylistEntity
import com.skygroove.data.media.MediaScanner
import com.skygroove.data.repo.ImperialRepository
import com.skygroove.domain.model.ImperialProfile
import com.skygroove.domain.model.PlaybackUiState
import com.skygroove.domain.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MainState(
    val hasPermission: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val query: String = "",
    val favorites: Set<Long> = emptySet(),
    val profile: ImperialProfile = ImperialProfile(),
    val playlists: List<PlaylistEntity> = emptyList(),
    val playback: PlaybackUiState = PlaybackUiState()
) {
    val currentTrack: Track?
        get() = tracks.find { it.id == playback.currentTrackId }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scanner: MediaScanner,
    private val repo: ImperialRepository,
    private val player: ExoPlayer
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private var progressJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) = publishPlaybackState()
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = publishPlaybackState()
        override fun onPlaybackStateChanged(playbackState: Int) = publishPlaybackState()
        override fun onRepeatModeChanged(repeatMode: Int) = publishPlaybackState()
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) = publishPlaybackState()
        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) = publishPlaybackState()
    }

    init {
        player.addListener(playerListener)
        startPositionTicker()
        viewModelScope.launch { repo.observeFavorites().collect { _state.update { s -> s.copy(favorites = it) } } }
        viewModelScope.launch { repo.observeProfile().collect { _state.update { s -> s.copy(profile = it) } } }
        viewModelScope.launch { repo.observePlaylists().collect { _state.update { s -> s.copy(playlists = it) } } }
        publishPlaybackState()
    }

    fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasPermission = granted) }
        if (granted) loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _state.update { it.copy(tracks = scanner.scan()) }
            publishPlaybackState()
        }
    }

    fun play(track: Track) {
        val list = filteredTracks()
        val index = list.indexOfFirst { it.id == track.id }.takeIf { it >= 0 } ?: 0
        playFromList(list, index)
    }

    fun playFromList(list: List<Track>, index: Int) {
        if (list.isEmpty() || index !in list.indices) return
        val mediaItems = list.map {
            MediaItem.Builder().setMediaId(it.id.toString()).setUri(it.uri).build()
        }
        player.setMediaItems(mediaItems, index, 0)
        player.prepare()
        player.playWhenReady = true
        viewModelScope.launch { repo.registerPlayback(list[index].id, list[index].durationMs) }
        publishPlaybackState()
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
        publishPlaybackState()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs.coerceAtLeast(0))
        publishPlaybackState()
    }

    fun toggleFavorite(trackId: Long) = viewModelScope.launch { repo.toggleFavorite(trackId, !_state.value.favorites.contains(trackId)) }
    fun setQuery(query: String) { _state.update { it.copy(query = query) } }
    fun createPlaylist(name: String) = viewModelScope.launch { if (name.isNotBlank()) repo.createPlaylist(name) }

    fun filteredTracks() = state.value.tracks.filter { listOf(it.title, it.artist, it.album).any { text -> text.contains(state.value.query, true) } }

    private fun publishPlaybackState() {
        val queue = (0 until player.mediaItemCount)
            .mapNotNull { idx -> player.getMediaItemAt(idx).mediaId.toLongOrNull() }
        val currentId = player.currentMediaItem?.mediaId?.toLongOrNull()
        _state.update {
            it.copy(
                playback = PlaybackUiState(
                    currentTrackId = currentId,
                    isPlaying = player.isPlaying,
                    positionMs = player.currentPosition.coerceAtLeast(0),
                    durationMs = player.duration.takeIf { d -> d > 0 } ?: 0,
                    queue = queue,
                    queueIndex = player.currentMediaItemIndex.coerceAtLeast(0),
                    repeatMode = player.repeatMode,
                    shuffleOn = player.shuffleModeEnabled
                )
            )
        }
    }

    private fun startPositionTicker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                if (player.isPlaying) publishPlaybackState()
                delay(500)
            }
        }
    }

    override fun onCleared() {
        progressJob?.cancel()
        player.removeListener(playerListener)
        super.onCleared()
    }
}

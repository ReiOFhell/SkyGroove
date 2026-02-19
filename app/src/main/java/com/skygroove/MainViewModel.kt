package com.skygroove

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.skygroove.data.local.entity.PlaylistEntity
import com.skygroove.data.media.MediaScanner
import com.skygroove.data.repo.ImperialRepository
import com.skygroove.domain.model.ImperialProfile
import com.skygroove.domain.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
    val hasPermission: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val query: String = "",
    val currentTrack: Track? = null,
    val favorites: Set<Long> = emptySet(),
    val profile: ImperialProfile = ImperialProfile(),
    val playlists: List<PlaylistEntity> = emptyList()
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scanner: MediaScanner,
    private val repo: ImperialRepository,
    private val player: ExoPlayer
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        viewModelScope.launch { repo.observeFavorites().collect { _state.update { s -> s.copy(favorites = it) } } }
        viewModelScope.launch { repo.observeProfile().collect { _state.update { s -> s.copy(profile = it) } } }
        viewModelScope.launch { repo.observePlaylists().collect { _state.update { s -> s.copy(playlists = it) } } }
    }

    fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasPermission = granted) }
        if (granted) loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _state.update { it.copy(tracks = scanner.scan()) }
        }
    }

    fun play(track: Track) {
        val item = MediaItem.Builder().setMediaId(track.id.toString()).setUri(track.uri).build()
        player.setMediaItem(item)
        player.prepare()
        player.playWhenReady = true
        _state.update { it.copy(currentTrack = track) }
        viewModelScope.launch { repo.registerPlayback(track.id, track.durationMs) }
    }

    fun toggleFavorite(trackId: Long) = viewModelScope.launch { repo.toggleFavorite(trackId, !_state.value.favorites.contains(trackId)) }
    fun setQuery(query: String) { _state.update { it.copy(query = query) } }
    fun createPlaylist(name: String) = viewModelScope.launch { if (name.isNotBlank()) repo.createPlaylist(name) }

    fun filteredTracks() = state.value.tracks.filter { listOf(it.title, it.artist, it.album).any { text -> text.contains(state.value.query, true) } }
}

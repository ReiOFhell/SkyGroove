package com.skygroove.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skygroove.MainState
import com.skygroove.domain.model.Track

@Composable
fun SplashScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("✦ SkyGroove ✦", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OnboardingScreen(onGrant: () -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text("SkyGroove", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("SkyGroove precisa acessar sua biblioteca para invocar suas relíquias sonoras.")
            Spacer(Modifier.height(12.dp))
            Button(onClick = onGrant) { Text("Conceder Permissão Imperial") }
            Text("Negou? Continue no modo demonstração e retorne quando quiser liberar as relíquias.")
        }
    }
}

@Composable
fun MiniPlayerBar(state: MainState, onOpenPlayer: () -> Unit, onTogglePlayPause: () -> Unit) {
    val current = state.currentTrack ?: return
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).clickable { onOpenPlayer() }) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(current.title, maxLines = 1)
                Text(current.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            IconButton(onClick = onTogglePlayPause) {
                Text(if (state.playback.isPlaying) "⏸" else "▶")
            }
        }
    }
}

@Composable
fun HomeScreen(state: MainState, modifier: Modifier = Modifier, onPlay: (Track) -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(12.dp)) {
        item { Text("Trono", style = MaterialTheme.typography.headlineMedium) }
        state.currentTrack?.let { current ->
            item {
                ElevatedCard { ListItem(headlineContent = { Text("Agora Tocando") }, supportingContent = { Text("${current.title} • ${current.artist}") }) }
            }
        }
        item { Spacer(Modifier.height(8.dp)); Text("Favoritas") }
        items(state.tracks.filter { state.favorites.contains(it.id) }.take(5)) {
            ListItem(headlineContent = { Text(it.title) }, supportingContent = { Text(it.artist) }, trailingContent = { TextButton(onClick = { onPlay(it) }) { Text("Tocar") } })
        }
    }
}

@Composable
fun LibraryScreen(state: MainState, modifier: Modifier = Modifier, onQuery: (String) -> Unit, tracksProvider: () -> List<Track>, onPlay: (Track) -> Unit, onFavorite: (Long) -> Unit) {
    Column(modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(value = state.query, onValueChange = onQuery, label = { Text("Buscar (música/artista/álbum)") }, modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(tracksProvider(), key = { it.id }) { track ->
                ListItem(
                    headlineContent = { Text(track.title) },
                    supportingContent = { Text("${track.artist} • ${track.album}") },
                    trailingContent = { Row { TextButton(onClick = { onFavorite(track.id) }) { Text(if (state.favorites.contains(track.id)) "★" else "☆") }; TextButton(onClick = { onPlay(track) }) { Text("Tocar") } } }
                )
                Divider()
            }
        }
    }
}

@Composable
fun PlayerScreen(state: MainState, modifier: Modifier = Modifier, onFavorite: (Long) -> Unit, onTogglePlayPause: () -> Unit, onSeek: (Long) -> Unit) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        Text("Player Supremo", style = MaterialTheme.typography.headlineMedium)
        state.currentTrack?.let {
            val duration = state.playback.durationMs.coerceAtLeast(1)
            val progress = (state.playback.positionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
            Text(it.title, style = MaterialTheme.typography.titleLarge)
            Text(it.artist)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row {
                Button(onClick = onTogglePlayPause) { Text(if (state.playback.isPlaying) "Pausar" else "Tocar") }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { onSeek(state.playback.positionMs + 15_000) }) { Text("+15s") }
            }
            Spacer(Modifier.height(8.dp))
            Row { Button(onClick = { onFavorite(it.id) }) { Text("Favoritar") }; Spacer(Modifier.width(8.dp)); OutlinedButton(onClick = {}) { Text("Adicionar ao Grimório") } }
            Text("Fila ativa: ${state.playback.queue.size} • Índice: ${state.playback.queueIndex}")
        } ?: Text("Nenhuma relíquia invocada.")
    }
}

@Composable
fun PlaylistsScreen(state: MainState, modifier: Modifier = Modifier, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Grimórios")
        Row {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Novo grimório") }, modifier = Modifier.weight(1f))
            Button(onClick = { onCreate(name); name = "" }) { Text("Criar") }
        }
        LazyColumn { items(state.playlists) { Text("• ${it.name}") } }
    }
}

@Composable
fun ProfileScreen(state: MainState, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        Text("Perfil Imperial", style = MaterialTheme.typography.headlineMedium)
        Text("XP Total: ${state.profile.xp}")
        Text("Nível: ${state.profile.level}")
        Text("Moedas Imperiais: ${state.profile.coins}")
        Text("Tempo total ouvido: ${state.profile.listenedMs / 1000}s")
    }
}

@Composable
fun StatsScreen(state: MainState, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Estatísticas persistidas: histórico, mais tocadas geral e mensal (estrutura pronta para expansão).") }
}

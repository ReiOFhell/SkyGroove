package com.skygroove

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.skygroove.ui.screens.*
import com.skygroove.ui.theme.SkyGrooveTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyGrooveTheme {
                val state by vm.state.collectAsState()
                var tab by remember { mutableStateOf(0) }
                var splash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) { delay(1100); splash = false }

                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { vm.onPermissionResult(it) }
                when {
                    splash -> SplashScreen()
                    !state.hasPermission -> OnboardingScreen {
                        launcher.launch(if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    else -> Scaffold(
                        bottomBar = {
                            NavigationBar {
                                listOf("Trono","Biblioteca","Player Supremo","Grimórios","Perfil","Estatísticas").forEachIndexed { i, label ->
                                    NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = {}, label = { Text(label) })
                                }
                            }
                        }
                    ) { padding ->
                        when (tab) {
                            0 -> HomeScreen(state, Modifier.padding(padding), onPlay = vm::play)
                            1 -> LibraryScreen(state, Modifier.padding(padding), vm::setQuery, vm::filteredTracks, vm::play, vm::toggleFavorite)
                            2 -> PlayerScreen(state, Modifier.padding(padding), vm::toggleFavorite)
                            3 -> PlaylistsScreen(state, Modifier.padding(padding), vm::createPlaylist)
                            4 -> ProfileScreen(state, Modifier.padding(padding))
                            else -> StatsScreen(state, Modifier.padding(padding))
                        }
                    }
                }
            }
        }
    }
}

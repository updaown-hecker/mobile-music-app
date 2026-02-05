package com.updaown.musicapp.ui

import android.app.Application
import android.content.ComponentName
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.updaown.musicapp.data.AppDatabase
import com.updaown.musicapp.data.ImportRepository
import com.updaown.musicapp.data.Song
import com.updaown.musicapp.service.MusicService
import com.updaown.musicapp.ui.theme.AppleDarkGray
import com.updaown.musicapp.ui.theme.AppleWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val importRepository = ImportRepository(application, db)
    private val settingsRepository = com.updaown.musicapp.data.SettingsRepository(application)

    // Settings state
    var settings by mutableStateOf<com.updaown.musicapp.data.SettingsEntity?>(null)
        private set

    // Internal source of truth from DB
    private var allDbSongs = listOf<Song>()

    // Exposed state for UI (can be filtered)
    var songs = mutableStateListOf<Song>()
        private set

    // Search
    var searchQuery by mutableStateOf("")
        private set

    // Current playback queue (library, folder, playlist, favorites)
    private var activeQueue = listOf<Song>()

    init {
        // Ensure settings row exists and observe changes
        viewModelScope.launch {
            val existing = settingsRepository.getSettingsSync()
            settings = existing
            settingsRepository.settingsFlow.collect { settingsEntity ->
                settings = settingsEntity
                applyPlaybackSettings(settingsEntity)
                repeatMode = settingsEntity.repeatMode
                isShuffleEnabled = settingsEntity.shuffleEnabled
                applyFilter()
            }
        }

        // Observe DB for library songs
        viewModelScope.launch {
            db.songDao().getAllSongs().collect { entities ->
                val songList =
                        entities.map { entity ->
                            Song(
                                    id = entity.id,
                                    title = entity.title,
                                    artist = entity.artist,
                                    album = entity.album,
                                    contentUri = android.net.Uri.parse(entity.contentUri),
                                    albumArtUri = android.net.Uri.parse(entity.albumArtUri),
                                    duration = entity.duration,
                                    folderName = entity.folderName,
                                    customTitle = entity.customTitle,
                                    customArtist = entity.customArtist,
                                    customAlbum = entity.customAlbum,
                                    customAlbumArtPath = entity.customAlbumArtPath,
                                    customYear = entity.customYear,
                                    customGenre = entity.customGenre
                            )
                        }
                allDbSongs = songList
                applyFilter()
            }
        }
    }

    // This is now purely for the "Import Screen"
    suspend fun getDeviceSongsForImport(): List<Song> {
        return importRepository.getDeviceSongs()
    }

    fun importSongs(newSongs: List<Song>) {
        viewModelScope.launch { importRepository.importSongs(newSongs) }
    }

    // Deprecated direct load, we rely on Flow collection now but keeping empty for compat if needed
    fun loadSongs() {
        // No-op
    }

    private fun applyFilter() {
        val filtered =
                if (searchQuery.isBlank()) {
                    allDbSongs
                } else {
                    allDbSongs.filter {
                        it.displayTitle.contains(searchQuery, ignoreCase = true) ||
                                it.displayArtist.contains(searchQuery, ignoreCase = true)
                    }
                }

        val sorted =
                when (settings?.sortOrder ?: "Title") {
                    "Artist" -> filtered.sortedBy { it.displayArtist.lowercase() }
                    "Album" -> filtered.sortedBy { it.displayAlbum.lowercase() }
                    "DateAdded" -> filtered.sortedByDescending { it.id }
                    else -> filtered.sortedBy { it.displayTitle.lowercase() }
                }

        songs.clear()
        songs.addAll(sorted)
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        applyFilter()
    }

    // Playback State
    var currentSong by mutableStateOf<Song?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var isShuffleEnabled by mutableStateOf(false)
        private set

    var repeatMode by mutableStateOf(Player.REPEAT_MODE_OFF)
        private set

    // Sleep Timer
    var sleepTimerMinutesRemaining by mutableIntStateOf(0)
        private set
    var sleepTimerActive by mutableStateOf(false)
        private set
    private var sleepTimerJob: Job? = null

    // Favorites
    private val _favorites = mutableStateListOf<Long>()
    val favorites: List<Long>
        get() = _favorites

    // UI Colors
    var dominantColor by mutableStateOf(AppleWhite)
        private set

    var onDominantColor by mutableStateOf(AppleDarkGray)
        private set

    // Progress (0f to 1f)
    var progress by mutableFloatStateOf(0f)
        private set

    var currentPosition by mutableLongStateOf(0L)
        private set

    var permissionGranted by mutableStateOf(false)

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var player: MediaController? = null

    // Playlist / Folder Creation Logic
    fun createPlaylist(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.playlistDao().insertPlaylist(com.updaown.musicapp.data.PlaylistEntity(name = name))
        }
    }

    // Exposed Playlists State
    var playlists = mutableStateListOf<com.updaown.musicapp.data.PlaylistEntity>()
        private set

    init {
        // Observe Playlists
        viewModelScope.launch {
            db.playlistDao().getAllPlaylists().collect {
                playlists.clear()
                playlists.addAll(it)
            }
        }

        val sessionToken =
                SessionToken(application, ComponentName(application, MusicService::class.java))

        controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture?.addListener(
                {
                    player = controllerFuture?.get()
                    setupPlayerListener()
                },
                MoreExecutors.directExecutor()
        )
    }

    fun addSongsToPlaylist(playlistId: Long, songsToAdd: List<Song>) {
        viewModelScope.launch(Dispatchers.IO) {
            songsToAdd.forEach { song ->
                db.playlistDao()
                        .insertPlaylistSongCrossRef(
                                com.updaown.musicapp.data.PlaylistSongCrossRef(playlistId, song.id)
                        )
            }
        }
    }

    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> {
        return db.playlistDao().getSongsForPlaylist(playlistId).map { entities ->
            entities.map { entity ->
                Song(
                        id = entity.id,
                        title = entity.title,
                        artist = entity.artist,
                        album = entity.album,
                        contentUri = android.net.Uri.parse(entity.contentUri),
                        albumArtUri = android.net.Uri.parse(entity.albumArtUri),
                        duration = entity.duration,
                        folderName = entity.folderName,
                        customTitle = entity.customTitle,
                        customArtist = entity.customArtist,
                        customAlbum = entity.customAlbum,
                        customAlbumArtPath = entity.customAlbumArtPath,
                        customYear = entity.customYear,
                        customGenre = entity.customGenre
                )
            }
        }
    }

    // For "Folder Detail" view
    fun getSongsInFolder(folderName: String): List<Song> {
        return allDbSongs.filter { it.folderName == folderName }
    }

    private fun setupPlayerListener() {
        player?.let { p ->
            p.addListener(
                    object : Player.Listener {
                        override fun onIsPlayingChanged(playing: Boolean) {
                            isPlaying = playing
                            if (playing) {
                                startProgressLoop()
                            }
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            mediaItem?.mediaId?.let { id ->
                                currentSong = allDbSongs.find { it.id.toString() == id }
                                currentSong?.let { extractColors(it) }
                                progress = 0f
                                currentPosition = 0L
                            }
                        }

                        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                            isShuffleEnabled = shuffleModeEnabled
                        }

                        override fun onRepeatModeChanged(repeatMode: Int) {
                            this@MainViewModel.repeatMode = repeatMode
                        }
                    }
            )

            isShuffleEnabled = p.shuffleModeEnabled
            repeatMode = p.repeatMode

            // Restore saved shuffle and repeat states
            viewModelScope.launch {
                val savedSettings = settingsRepository.getSettingsSync()
                p.shuffleModeEnabled = savedSettings.shuffleEnabled
                isShuffleEnabled = savedSettings.shuffleEnabled
                p.repeatMode = savedSettings.repeatMode
                repeatMode = savedSettings.repeatMode
                applyPlaybackSettings(savedSettings)
            }

            if (p.currentMediaItem != null) {
                val mediaId = p.currentMediaItem?.mediaId
                val song = allDbSongs.find { it.id.toString() == mediaId }
                if (song != null) {
                    currentSong = song
                    extractColors(song)
                    isPlaying = p.isPlaying
                    if (isPlaying) startProgressLoop()

                    val current = p.currentPosition
                    val total = p.duration.coerceAtLeast(1)
                    currentPosition = current
                    progress = current.toFloat() / total.toFloat()
                }
            }
        }
    }

    private fun startProgressLoop() {
        viewModelScope.launch {
            while (isActive && isPlaying) {
                player?.let {
                    val current = it.currentPosition
                    val total = it.duration.coerceAtLeast(1)
                    currentPosition = current
                    progress = current.toFloat() / total.toFloat()
                }
                delay(500)
            }
        }
    }

    fun toggleFavorite(song: Song) {
        if (_favorites.contains(song.id)) {
            _favorites.remove(song.id)
        } else {
            _favorites.add(song.id)
        }
    }

    fun toggleShuffle() {
        player?.let {
            val newMode = !it.shuffleModeEnabled
            it.shuffleModeEnabled = newMode
            isShuffleEnabled = newMode
            viewModelScope.launch { settingsRepository.updateShuffleEnabled(newMode) }
        }
    }

    fun toggleRepeat() {
        val newMode =
                when (repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                    Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                    else -> Player.REPEAT_MODE_OFF
                }
        player?.repeatMode = newMode
        repeatMode = newMode
        // Persist to database
        viewModelScope.launch { settingsRepository.updateRepeatMode(newMode) }
    }

    fun updateSongMetadata(
            songId: Long,
            title: String,
            artist: String,
            album: String,
            albumArtUri: String? = null
    ) {
        val song = allDbSongs.find { it.id == songId } ?: return
        val updatedSong =
                song.copy(
                        customTitle = title.ifBlank { null },
                        customArtist = artist.ifBlank { null },
                        customAlbum = album.ifBlank { null },
                        customAlbumArtPath = albumArtUri
                )

        // Update in database
        viewModelScope.launch(Dispatchers.IO) {
            db.songDao()
                    .updateSongMetadata(
                            songId,
                            title.ifBlank { null },
                            artist.ifBlank { null },
                            album.ifBlank { null },
                            albumArtUri
                    )
        }

        // Update in memory
        val index = allDbSongs.indexOf(song)
        if (index != -1) {
            allDbSongs = allDbSongs.toMutableList().apply { set(index, updatedSong) }
        }

        // Update current song if it's playing
        if (currentSong?.id == songId) {
            currentSong = updatedSong
        }

        // Refresh filtered view
        applyFilter()
    }

    fun playSong(song: Song) {
        playSong(song, songs)
    }

    fun playSong(song: Song, playlist: List<Song>) {
        val index = playlist.indexOf(song)
        if (index == -1) return

        activeQueue = playlist.toList()

        val mediaItems =
                activeQueue.map {
                    MediaItem.Builder().setMediaId(it.id.toString()).setUri(it.contentUri).build()
                }

        player?.setMediaItems(mediaItems, index, 0)
        player?.prepare()

        val currentSettings = settings ?: com.updaown.musicapp.data.SettingsEntity()
        player?.shuffleModeEnabled = currentSettings.shuffleEnabled
        isShuffleEnabled = currentSettings.shuffleEnabled
        player?.repeatMode = currentSettings.repeatMode
        repeatMode = currentSettings.repeatMode
        player?.play()
        currentSong = song
        extractColors(song)
    }

    fun shufflePlay(playlist: List<Song>) {
        if (playlist.isEmpty()) return

        activeQueue = playlist.toList()

        val mediaItems =
                activeQueue.map {
                    MediaItem.Builder().setMediaId(it.id.toString()).setUri(it.contentUri).build()
                }

        player?.setMediaItems(mediaItems)
        player?.shuffleModeEnabled = true
        isShuffleEnabled = true
        player?.prepare()
        player?.repeatMode = Player.REPEAT_MODE_ALL
        repeatMode = Player.REPEAT_MODE_ALL
        player?.play()
        viewModelScope.launch { settingsRepository.updateShuffleEnabled(true) }
    }

    private fun extractColors(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val loader = ImageLoader(context)
            val request =
                    ImageRequest.Builder(context)
                            .data(song.albumArtUri)
                            .allowHardware(false)
                            .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = (result.drawable as BitmapDrawable).bitmap
                val palette = Palette.from(bitmap).generate()
                val dominant = palette.getDominantColor(android.graphics.Color.WHITE)

                launch(Dispatchers.Main) {
                    dominantColor = Color(dominant)
                    onDominantColor =
                            if (androidx.core.graphics.ColorUtils.calculateLuminance(dominant) > 0.5
                            ) {
                                Color.Black
                            } else {
                                Color.White
                            }
                }
            } else {
                launch(Dispatchers.Main) {
                    dominantColor = AppleWhite
                    onDominantColor = AppleDarkGray
                }
            }
        }
    }

    fun togglePlayPause() {
        if (player?.isPlaying == true) {
            player?.pause()
        } else {
            player?.play()
        }
    }

    fun seekTo(value: Float) {
        val duration = player?.duration ?: return
        val position = (value * duration).toLong()
        player?.seekTo(position)
        progress = value
        currentPosition = position
    }

    fun skipNext() {
        if (player?.hasNextMediaItem() == true) {
            player?.seekToNextMediaItem()
        }
    }

    fun skipPrevious() {
        if (player?.hasPreviousMediaItem() == true) {
            player?.seekToPreviousMediaItem()
        } else {
            player?.seekTo(0)
        }
    }

    // Settings methods
    fun updateSleepTimer(minutes: Int) {
        // Cancel existing timer if any
        sleepTimerJob?.cancel()

        if (minutes == 0) {
            sleepTimerActive = false
            sleepTimerMinutesRemaining = 0
            val updated = (settings ?: com.updaown.musicapp.data.SettingsEntity()).copy(sleepTimerMinutes = 0)
            updateAllSettings(updated)
            return
        }

        // Start new timer
        sleepTimerMinutesRemaining = minutes
        sleepTimerActive = true

        sleepTimerJob =
                viewModelScope.launch {
                    var secondsRemaining = minutes * 60
                    while (secondsRemaining > 0 && isActive) {
                        delay(1000) // Update every second
                        secondsRemaining--
                        sleepTimerMinutesRemaining = (secondsRemaining + 59) / 60 // Round up

                        if (secondsRemaining == 0) {
                            // Pause playback
                            player?.pause()
                            sleepTimerActive = false
                            break
                        }
                    }
                }

        val updated = (settings ?: com.updaown.musicapp.data.SettingsEntity()).copy(sleepTimerMinutes = minutes)
        updateAllSettings(updated)
    }

    fun updateEqualizerEnabled(enabled: Boolean) {
        val updated = (settings ?: com.updaown.musicapp.data.SettingsEntity()).copy(equalizerEnabled = enabled)
        updateAllSettings(updated)
    }

    fun updateEqualizerPreset(preset: String) {
        val updated = (settings ?: com.updaown.musicapp.data.SettingsEntity()).copy(equalizerPreset = preset)
        updateAllSettings(updated)
    }

    fun updateBass(value: Int) {
        val updated = (settings ?: com.updaown.musicapp.data.SettingsEntity()).copy(bass = value)
        updateAllSettings(updated)
    }

    fun updateTreble(value: Int) {
        val updated = (settings ?: com.updaown.musicapp.data.SettingsEntity()).copy(treble = value)
        updateAllSettings(updated)
    }

    fun updateMidrange(value: Int) {
        val updated = (settings ?: com.updaown.musicapp.data.SettingsEntity()).copy(midrange = value)
        updateAllSettings(updated)
    }

    fun updateAllSettings(newSettings: com.updaown.musicapp.data.SettingsEntity) {
        val previous = settings ?: com.updaown.musicapp.data.SettingsEntity()
        settings = newSettings

        if (newSettings.shuffleEnabled != previous.shuffleEnabled) {
            player?.shuffleModeEnabled = newSettings.shuffleEnabled
            isShuffleEnabled = newSettings.shuffleEnabled
        }

        if (newSettings.repeatMode != previous.repeatMode) {
            player?.repeatMode = newSettings.repeatMode
            repeatMode = newSettings.repeatMode
        }

        applyPlaybackSettings(newSettings)

        viewModelScope.launch { settingsRepository.updateSettings(newSettings) }
    }

    private fun applyPlaybackSettings(currentSettings: com.updaown.musicapp.data.SettingsEntity) {
        player?.setPlaybackParameters(PlaybackParameters(currentSettings.playbackSpeed.coerceIn(0.5f, 2.0f)))
        // Skip-silence is persisted in settings; runtime toggle requires ExoPlayer-specific API on this controller path.
    }

    fun checkForUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceSongs = importRepository.getDeviceSongs()
            var updatesCount = 0

            deviceSongs.forEach { deviceSong ->
                val cachedSong = allDbSongs.find { it.id == deviceSong.id }

                if (cachedSong != null) {
                    // Check for differences in core metadata
                    if (cachedSong.title != deviceSong.title ||
                                    cachedSong.artist != deviceSong.artist ||
                                    cachedSong.album != deviceSong.album ||
                                    cachedSong.albumArtUri != deviceSong.albumArtUri ||
                                    Math.abs(cachedSong.duration - deviceSong.duration) >
                                            1000 // Allow slight diff
                    ) {
                        db.songDao()
                                .updateCoreMetadata(
                                        songId = deviceSong.id,
                                        title = deviceSong.title,
                                        artist = deviceSong.artist,
                                        album = deviceSong.album,
                                        albumArtUri = deviceSong.albumArtUri.toString(),
                                        duration = deviceSong.duration
                                )
                        updatesCount++
                    }
                }
            }
            // Trigger a refresh/resync of the list if updates occurred
            if (updatesCount > 0) {
                // The DB update will trigger the Flow collection automatically in init block
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let { future -> MediaController.releaseFuture(future) }
    }
}

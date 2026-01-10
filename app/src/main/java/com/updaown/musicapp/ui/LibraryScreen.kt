package com.updaown.musicapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.updaown.musicapp.data.PlaylistEntity
import com.updaown.musicapp.data.Song
import com.updaown.musicapp.ui.theme.*

sealed class SelectedCollection {
        data class Folder(val name: String) : SelectedCollection()
        data class Playlist(val entity: PlaylistEntity) : SelectedCollection()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
        viewModel: MainViewModel,
        onNavigateToImport: () -> Unit,
        onNavigateToSettings: () -> Unit
) {
        var isPlayerExpanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {

                // Folder / Playlist Creation Dialog
                var showCreateFolderDialog by remember { mutableStateOf(false) }
                var newFolderName by remember { mutableStateOf("") }

                // Song Selection Dialog for Playlists
                var showAddSongsDialog by remember { mutableStateOf(false) }

                // Metadata Editor Dialog
                var showMetadataEditor by remember { mutableStateOf(false) }
                var songToEdit by remember { mutableStateOf<Song?>(null) }

                // Navigation State
                var selectedCollection by remember { mutableStateOf<SelectedCollection?>(null) }

                // Hoisted Tab State (Default to Folders)
                var selectedTabIndex by remember { mutableIntStateOf(3) }
                val tabs = listOf("Tracks", "Artists", "Albums", "Folders", "Favorites")

                // Metadata Editor Dialog
                if (showMetadataEditor && songToEdit != null) {
                        MetadataEditorDialog(
                                song = songToEdit!!,
                                viewModel = viewModel,
                                onDismiss = {
                                        showMetadataEditor = false
                                        songToEdit = null
                                }
                        )
                }

                // Create Playlist Dialog
                if (showCreateFolderDialog) {
                        AlertDialog(
                                onDismissRequest = { showCreateFolderDialog = false },
                                containerColor = SamsungDarkGray,
                                titleContentColor = AppleWhite,
                                textContentColor = AppleGray,
                                title = { Text("New Playlist") },
                                text = {
                                        TextField(
                                                value = newFolderName,
                                                onValueChange = { newFolderName = it },
                                                placeholder = { Text("Playlist Name") },
                                                colors =
                                                        TextFieldDefaults.colors(
                                                                focusedContainerColor =
                                                                        SamsungBlack,
                                                                unfocusedContainerColor =
                                                                        SamsungBlack,
                                                                focusedIndicatorColor = SamsungBlue,
                                                                unfocusedIndicatorColor =
                                                                        Color.Transparent,
                                                                focusedTextColor = AppleWhite,
                                                                unfocusedTextColor = AppleWhite
                                                        )
                                        )
                                },
                                confirmButton = {
                                        TextButton(
                                                onClick = {
                                                        if (newFolderName.isNotBlank()) {
                                                                viewModel.createPlaylist(
                                                                        newFolderName
                                                                )
                                                                showCreateFolderDialog = false
                                                                newFolderName = ""
                                                        }
                                                }
                                        ) { Text("Create", color = SamsungBlue) }
                                },
                                dismissButton = {
                                        TextButton(onClick = { showCreateFolderDialog = false }) {
                                                Text("Cancel", color = AppleGray)
                                        }
                                }
                        )
                }

                // Main Background
                Box(modifier = Modifier.fillMaxSize().background(SamsungBlack)) {
                        if (selectedCollection != null) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                        val displaySongs =
                                                when (val c = selectedCollection!!) {
                                                        is SelectedCollection.Folder -> {
                                                                remember(c.name, viewModel.songs) {
                                                                        viewModel.getSongsInFolder(
                                                                                c.name
                                                                        )
                                                                }
                                                        }
                                                        is SelectedCollection.Playlist -> {
                                                                val songs by
                                                                        viewModel
                                                                                .getPlaylistSongs(
                                                                                        c.entity
                                                                                                .playlistId
                                                                                )
                                                                                .collectAsState(
                                                                                        initial =
                                                                                                emptyList()
                                                                                )
                                                                songs
                                                        }
                                                }

                                        Row(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                IconButton(
                                                        onClick = { selectedCollection = null }
                                                ) {
                                                        Icon(
                                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                                contentDescription = "Back",
                                                                tint = SamsungBlue
                                                        )
                                                }

                                                val title =
                                                        when (val c = selectedCollection!!) {
                                                                is SelectedCollection.Folder ->
                                                                        c.name
                                                                is SelectedCollection.Playlist ->
                                                                        c.entity.name
                                                        }

                                                Text(
                                                        text = title,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineMedium.copy(
                                                                        fontWeight = FontWeight.Bold
                                                                ),
                                                        maxLines = 1,
                                                        modifier = Modifier.weight(1f),
                                                        color = AppleWhite
                                                )

                                                if (selectedCollection is
                                                                SelectedCollection.Playlist
                                                ) {
                                                        IconButton(
                                                                onClick = {
                                                                        viewModel.shufflePlay(
                                                                                displaySongs
                                                                        )
                                                                        isPlayerExpanded = true
                                                                }
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Shuffle,
                                                                        contentDescription =
                                                                                "Shuffle Play",
                                                                        tint = SamsungBlue
                                                                )
                                                        }

                                                        IconButton(
                                                                onClick = {
                                                                        showAddSongsDialog = true
                                                                }
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.Add,
                                                                        contentDescription =
                                                                                "Add Songs",
                                                                        tint = SamsungBlue
                                                                )
                                                        }
                                                }
                                        }

                                        SongList(
                                                songs = displaySongs,
                                                onSongClick = {
                                                        viewModel.playSong(it, displaySongs)
                                                        isPlayerExpanded = true
                                                },
                                                onLongClick = {
                                                        songToEdit = it
                                                        showMetadataEditor = true
                                                },
                                                emptyMsg = "No songs here yet"
                                        )
                                }
                        } else {
                                Column(modifier = Modifier.fillMaxSize()) {
                                        Column(
                                                modifier =
                                                        Modifier.padding(
                                                                top = 16.dp,
                                                                start = 16.dp,
                                                                end = 16.dp
                                                        )
                                        ) {
                                                Row(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(bottom = 16.dp),
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween,
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                text = "Library",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .displayLarge.copy(
                                                                                fontSize = 32.sp,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        ),
                                                                color = AppleWhite
                                                        )
                                                        Row {
                                                                IconButton(
                                                                        onClick =
                                                                                onNavigateToSettings
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .Settings,
                                                                                contentDescription =
                                                                                        "Settings",
                                                                                tint = SamsungBlue
                                                                        )
                                                                }
                                                                IconButton(
                                                                        onClick = {
                                                                                viewModel
                                                                                        .checkForUpdates()
                                                                        }
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .Refresh,
                                                                                contentDescription =
                                                                                        "Check for Updates",
                                                                                tint = SamsungBlue
                                                                        )
                                                                }
                                                                IconButton(
                                                                        onClick = {
                                                                                if (selectedTabIndex ==
                                                                                                3
                                                                                ) {
                                                                                        showCreateFolderDialog =
                                                                                                true
                                                                                } else {
                                                                                        onNavigateToImport()
                                                                                }
                                                                        }
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default.Add,
                                                                                contentDescription =
                                                                                        "Add",
                                                                                tint = SamsungBlue
                                                                        )
                                                                }
                                                        }
                                                }

                                                TextField(
                                                        value = viewModel.searchQuery,
                                                        onValueChange = {
                                                                viewModel.onSearchQueryChanged(it)
                                                        },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .clip(
                                                                                RoundedCornerShape(
                                                                                        12.dp
                                                                                )
                                                                        ),
                                                        colors =
                                                                TextFieldDefaults.colors(
                                                                        focusedContainerColor =
                                                                                SamsungDarkGray,
                                                                        unfocusedContainerColor =
                                                                                SamsungDarkGray,
                                                                        disabledContainerColor =
                                                                                SamsungDarkGray,
                                                                        focusedIndicatorColor =
                                                                                Color.Transparent,
                                                                        unfocusedIndicatorColor =
                                                                                Color.Transparent,
                                                                        focusedTextColor =
                                                                                AppleWhite,
                                                                        unfocusedTextColor =
                                                                                AppleWhite
                                                                ),
                                                        placeholder = {
                                                                Text(
                                                                        "Search songs, artists...",
                                                                        color = AppleGray
                                                                )
                                                        },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Search,
                                                                        null,
                                                                        tint = AppleGray
                                                                )
                                                        },
                                                        singleLine = true
                                                )
                                        }

                                        ScrollableTabRow(
                                                selectedTabIndex = selectedTabIndex,
                                                containerColor = SamsungBlack,
                                                contentColor = SamsungBlue,
                                                edgePadding = 16.dp,
                                                divider = {},
                                                indicator = {}
                                        ) {
                                                tabs.forEachIndexed { index, title ->
                                                        val selected = selectedTabIndex == index
                                                        Tab(
                                                                selected = selected,
                                                                onClick = {
                                                                        selectedTabIndex = index
                                                                },
                                                                text = {
                                                                        Text(
                                                                                text = title,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .titleMedium
                                                                                                .copy(
                                                                                                        fontWeight =
                                                                                                                if (selected
                                                                                                                ) {
                                                                                                                        FontWeight
                                                                                                                                .Bold
                                                                                                                } else {
                                                                                                                        FontWeight
                                                                                                                                .Normal
                                                                                                                }
                                                                                                ),
                                                                                color =
                                                                                        if (selected
                                                                                        )
                                                                                                AppleWhite
                                                                                        else
                                                                                                AppleGray
                                                                        )
                                                                }
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Box(modifier = Modifier.weight(1f)) {
                                                when (selectedTabIndex) {
                                                        0 ->
                                                                SongList(
                                                                        songs = viewModel.songs,
                                                                        onSongClick = {
                                                                                viewModel.playSong(
                                                                                        it,
                                                                                        viewModel
                                                                                                .songs
                                                                                )
                                                                                isPlayerExpanded =
                                                                                        true
                                                                        },
                                                                        onLongClick = {
                                                                                songToEdit = it
                                                                                showMetadataEditor =
                                                                                        true
                                                                        }
                                                                )
                                                        1 -> ArtistList(viewModel.songs) {}
                                                        2 -> AlbumList(viewModel.songs) {}
                                                        3 ->
                                                                FolderList(
                                                                        songs = viewModel.songs,
                                                                        playlists =
                                                                                viewModel.playlists,
                                                                        onCollectionClick = {
                                                                                selectedCollection =
                                                                                        it
                                                                        }
                                                                )
                                                        4 -> {
                                                                val favoriteSongs =
                                                                        viewModel.songs.filter {
                                                                                viewModel.favorites
                                                                                        .contains(
                                                                                                it.id
                                                                                        )
                                                                        }
                                                                SongList(
                                                                        songs = favoriteSongs,
                                                                        onSongClick = {
                                                                                viewModel.playSong(
                                                                                        it,
                                                                                        favoriteSongs
                                                                                )
                                                                                isPlayerExpanded =
                                                                                        true
                                                                        },
                                                                        onLongClick = {
                                                                                songToEdit = it
                                                                                showMetadataEditor =
                                                                                        true
                                                                        },
                                                                        emptyMsg =
                                                                                "No favorites yet"
                                                                )
                                                        }
                                                }
                                        }

                                        if (viewModel.currentSong != null) {
                                                Spacer(modifier = Modifier.height(64.dp))
                                        }
                                }
                        }

                        if (viewModel.currentSong != null && !isPlayerExpanded) {
                                MiniPlayer(
                                        song = viewModel.currentSong!!,
                                        isPlaying = viewModel.isPlaying,
                                        progress = viewModel.progress,
                                        onTogglePlay = { viewModel.togglePlayPause() },
                                        onClick = { isPlayerExpanded = true },
                                        onNextClick = { viewModel.skipNext() },
                                        onPreviousClick = { viewModel.skipPrevious() },
                                        onDragUp = { isPlayerExpanded = true },
                                        modifier = Modifier.align(Alignment.BottomCenter)
                                )
                        }

                        AnimatedVisibility(
                                visible = isPlayerExpanded,
                                enter = slideInVertically { it },
                                exit = slideOutVertically { it }
                        ) {
                                PlayerScreen(
                                        viewModel = viewModel,
                                        onCollapse = { isPlayerExpanded = false }
                                )
                        }
                }

                // Apple-style "Sheet" Backdrop and Dialog
                AnimatedVisibility(
                        visible =
                                showAddSongsDialog &&
                                        selectedCollection is SelectedCollection.Playlist,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                ) {
                        if (selectedCollection is SelectedCollection.Playlist) {
                                val playlist =
                                        (selectedCollection as SelectedCollection.Playlist).entity
                                val allSongs = viewModel.songs
                                val selectedSongsToAdd = remember { mutableStateListOf<Song>() }
                                var searchQuery by remember { mutableStateOf("") }

                                val filteredSongs =
                                        remember(allSongs, searchQuery) {
                                                if (searchQuery.isBlank()) allSongs
                                                else
                                                        allSongs.filter {
                                                                it.displayTitle.contains(
                                                                        searchQuery,
                                                                        ignoreCase = true
                                                                ) ||
                                                                        it.displayArtist.contains(
                                                                                searchQuery,
                                                                                ignoreCase = true
                                                                        )
                                                        }
                                        }

                                Box(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .background(
                                                                Color.Black.copy(alpha = 0.5f)
                                                        ) // Dimmed backdrop
                                                        .clickable(
                                                                enabled = true,
                                                                onClick = {}
                                                        ) // consume clicks, block underlying
                                ) {
                                        // The "Sheet" Card
                                        Column(
                                                modifier =
                                                        Modifier.align(Alignment.BottomCenter)
                                                                .fillMaxWidth()
                                                                .fillMaxHeight(
                                                                        0.95f
                                                                ) // Take up 95% of screen
                                                                .clip(
                                                                        RoundedCornerShape(
                                                                                topStart = 16.dp,
                                                                                topEnd = 16.dp
                                                                        )
                                                                )
                                                                .background(
                                                                        Color(0xFF1C1C1E)
                                                                ) // Apple Dark Gray
                                        ) {
                                                // Apple-style Header
                                                Row(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(
                                                                                horizontal = 16.dp,
                                                                                vertical = 18.dp
                                                                        ),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween
                                                ) {
                                                        Text(
                                                                "Cancel",
                                                                color = SamsungBlue, // Theme color
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge,
                                                                modifier =
                                                                        Modifier.clickable {
                                                                                showAddSongsDialog =
                                                                                        false
                                                                        }
                                                        )

                                                        Text(
                                                                "Add Music",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium.copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        ),
                                                                color = AppleWhite
                                                        )

                                                        Text(
                                                                if (selectedSongsToAdd.isEmpty())
                                                                        "Done"
                                                                else
                                                                        "Add (${selectedSongsToAdd.size})",
                                                                color =
                                                                        if (selectedSongsToAdd
                                                                                        .isEmpty()
                                                                        )
                                                                                AppleGray
                                                                        else SamsungBlue,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge.copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        ),
                                                                modifier =
                                                                        Modifier.clickable {
                                                                                if (selectedSongsToAdd
                                                                                                .isNotEmpty()
                                                                                ) {
                                                                                        viewModel
                                                                                                .addSongsToPlaylist(
                                                                                                        playlist.playlistId,
                                                                                                        selectedSongsToAdd
                                                                                                                .toList()
                                                                                                )
                                                                                        showAddSongsDialog =
                                                                                                false
                                                                                        selectedSongsToAdd
                                                                                                .clear()
                                                                                } else {
                                                                                        showAddSongsDialog =
                                                                                                false
                                                                                }
                                                                        }
                                                        )
                                                }

                                                // Search Bar
                                                AppleStyleSearchBar(
                                                        query = searchQuery,
                                                        onQueryChange = { searchQuery = it },
                                                        placeholder = "Search Songs"
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                HorizontalDivider(
                                                        color = SamsungDarkGray,
                                                        thickness = 1.dp
                                                )

                                                // Songs List
                                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                        items(filteredSongs) { song ->
                                                                AppleStyleSongItem(
                                                                        song = song,
                                                                        isSelected =
                                                                                selectedSongsToAdd
                                                                                        .contains(
                                                                                                song
                                                                                        ),
                                                                        onToggle = {
                                                                                if (selectedSongsToAdd
                                                                                                .contains(
                                                                                                        song
                                                                                                )
                                                                                ) {
                                                                                        selectedSongsToAdd
                                                                                                .remove(
                                                                                                        song
                                                                                                )
                                                                                } else {
                                                                                        selectedSongsToAdd
                                                                                                .add(
                                                                                                        song
                                                                                                )
                                                                                }
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
fun AppleStyleSearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String) {
        TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp)),
                placeholder = {
                        Text(
                                placeholder,
                                color = AppleGray,
                                style = MaterialTheme.typography.bodyLarge
                        )
                },
                prefix = {
                        Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = AppleGray,
                                modifier = Modifier.padding(end = 8.dp)
                        )
                },
                colors =
                        TextFieldDefaults.colors(
                                focusedContainerColor =
                                        Color(0xFF2C2C2E), // Slightly lighter gray for input
                                unfocusedContainerColor = Color(0xFF2C2C2E),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = AppleWhite,
                                unfocusedTextColor = AppleWhite,
                                cursorColor = SamsungBlue
                        ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge
        )
}

@Composable
fun AppleStyleSongItem(song: Song, isSelected: Boolean, onToggle: () -> Unit) {
        Column {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clickable { onToggle() }
                                        .padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 8.dp,
                                                bottom = 8.dp
                                        ),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        // Album Art
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(SamsungDarkGray),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = AppleGray,
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = song.displayTitle,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = AppleWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                        text = song.displayArtist,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AppleGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Apple Selection Circle
                        AppleSelectionCircle(isSelected = isSelected)
                }
                // Divider with offset to match Apple style (starts after album art usually, or near
                // text)
                HorizontalDivider(
                        modifier = Modifier.padding(start = 76.dp),
                        color = Color(0xFF3A3A3C),
                        thickness = 0.5.dp
                )
        }
}

@Composable
fun AppleSelectionCircle(isSelected: Boolean) {
        Box(
                modifier =
                        Modifier.size(22.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(if (isSelected) SamsungBlue else Color.Transparent)
                                .then(
                                        if (!isSelected) {
                                                Modifier.border(
                                                        1.5.dp,
                                                        AppleGray,
                                                        androidx.compose.foundation.shape
                                                                .CircleShape
                                                )
                                        } else Modifier
                                ),
                contentAlignment = Alignment.Center
        ) {
                if (isSelected) {
                        Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                        )
                }
        }
}

private fun formatDuration(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
}

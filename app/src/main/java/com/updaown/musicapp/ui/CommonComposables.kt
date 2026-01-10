package com.updaown.musicapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.updaown.musicapp.data.PlaylistEntity
import com.updaown.musicapp.data.Song
import com.updaown.musicapp.ui.theme.*

@Composable
fun SongList(
        songs: List<Song>,
        onSongClick: (Song) -> Unit,
        onLongClick: (Song) -> Unit = {},
        emptyMsg: String = "No songs"
) {
        if (songs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                                emptyMsg,
                                color = AppleGray,
                                style = MaterialTheme.typography.bodyLarge
                        )
                }
                return
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                items(songs) { song ->
                        SongItemCard(
                                song = song,
                                onSongClick = { onSongClick(song) },
                                onEditClick = { onLongClick(song) }
                        )
                }
        }
}

@Composable
fun SongItemCard(song: Song, onSongClick: () -> Unit, onEditClick: () -> Unit = {}) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SamsungDarkGray)
                                .clickable { onSongClick() }
                                .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
                // Album art with default placeholder
                Box(
                        modifier =
                                Modifier.size(56.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SamsungBlack),
                        contentAlignment = Alignment.Center
                ) {
                        AsyncImage(
                                model =
                                        ImageRequest.Builder(LocalContext.current)
                                                .data(song.displayAlbumArt)
                                                .crossfade(true)
                                                .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(56.dp)
                        )
                }

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                song.displayTitle,
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                        ),
                                color = AppleWhite,
                                maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                song.displayArtist,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppleGray,
                                maxLines = 1
                        )
                }

                IconButton(onClick = onEditClick, modifier = Modifier.size(40.dp)) {
                        Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = SamsungBlue,
                                modifier = Modifier.size(20.dp)
                        )
                }
        }
        Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun ArtistList(songs: List<Song>, onArtistClick: (String) -> Unit) {
        val artists = songs.map { it.displayArtist }.distinct()

        if (artists.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No artists", color = AppleGray)
                }
                return
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp)) {
                items(artists) { artist ->
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(SamsungDarkGray)
                                                .clickable { onArtistClick(artist) }
                                                .padding(18.dp, 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Text(
                                        artist,
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                ),
                                        color = AppleWhite,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                        color = SamsungLightGray,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(start = 8.dp)
                                ) {
                                        Text(
                                                "${songs.filter { it.displayArtist == artist }.size}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AppleGray,
                                                modifier = Modifier.padding(6.dp, 4.dp)
                                        )
                                }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                }
        }
}

@Composable
fun AlbumList(songs: List<Song>, onAlbumClick: (String) -> Unit) {
        val albums = songs.map { it.displayAlbum }.distinct()

        if (albums.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No albums", color = AppleGray)
                }
                return
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp)) {
                items(albums) { album ->
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(SamsungDarkGray)
                                                .clickable { onAlbumClick(album) }
                                                .padding(18.dp, 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Text(
                                        album,
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                ),
                                        color = AppleWhite,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                        color = SamsungLightGray,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(start = 8.dp)
                                ) {
                                        Text(
                                                "${songs.filter { it.displayAlbum == album }.size}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AppleGray,
                                                modifier = Modifier.padding(6.dp, 4.dp)
                                        )
                                }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                }
        }
}

@Composable
fun FolderList(
        songs: List<Song>,
        playlists: List<PlaylistEntity>,
        onCollectionClick: (SelectedCollection) -> Unit
) {
        val folders = songs.map { it.folderName }.distinct()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Grid of playlist cards (Samsung Music style)
                item {
                        if (playlists.isNotEmpty()) {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                        Text(
                                                "Playlists",
                                                style =
                                                        MaterialTheme.typography.titleLarge.copy(
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                color = AppleWhite,
                                                modifier = Modifier.padding(bottom = 12.dp)
                                        )

                                        // Grid layout for playlists
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                                var index = 0
                                                while (index < playlists.size) {
                                                        Row(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(
                                                                                        bottom =
                                                                                                12.dp
                                                                                ),
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(12.dp)
                                                        ) {
                                                                repeat(2) {
                                                                        if (index < playlists.size
                                                                        ) {
                                                                                val playlist =
                                                                                        playlists[
                                                                                                index]
                                                                                PlaylistGridCard(
                                                                                        name =
                                                                                                playlist.name,
                                                                                        onClick = {
                                                                                                onCollectionClick(
                                                                                                        SelectedCollection
                                                                                                                .Playlist(
                                                                                                                        playlist
                                                                                                                )
                                                                                                )
                                                                                        },
                                                                                        modifier =
                                                                                                Modifier.weight(
                                                                                                        1f
                                                                                                )
                                                                                )
                                                                                index++
                                                                        } else {
                                                                                Box(
                                                                                        modifier =
                                                                                                Modifier.weight(
                                                                                                        1f
                                                                                                )
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                        }
                }

                // List of folders
                if (folders.isNotEmpty()) {
                        item {
                                Text(
                                        "Folders",
                                        style =
                                                MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = AppleWhite,
                                        modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 12.dp)
                                )
                        }

                        items(folders) { folder ->
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .clip(RoundedCornerShape(14.dp))
                                                        .background(SamsungDarkGray)
                                                        .clickable {
                                                                onCollectionClick(
                                                                        SelectedCollection.Folder(
                                                                                folder
                                                                        )
                                                                )
                                                        }
                                                        .padding(18.dp, 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                        Icon(
                                                Icons.Default.Folder,
                                                contentDescription = null,
                                                tint = SamsungBlue,
                                                modifier = Modifier.size(28.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        folder,
                                                        style =
                                                                MaterialTheme.typography.titleMedium
                                                                        .copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .SemiBold
                                                                        ),
                                                        color = AppleWhite,
                                                        maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        "${songs.filter { it.folderName == folder }.size} songs",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = AppleGray
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                        }
                }
        }
}

@Composable
private fun PlaylistGridCard(name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Column(
                modifier =
                        modifier.clip(RoundedCornerShape(14.dp))
                                .background(SamsungDarkGray)
                                .clickable { onClick() }
                                .padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                // Icon box
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .aspectRatio(1f)
                                        .background(SamsungLightGray),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                Icons.Default.Folder,
                                contentDescription = null,
                                tint = SamsungBlue,
                                modifier = Modifier.size(50.dp)
                        )
                }

                // Text section
                Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.Start
                ) {
                        Text(
                                name,
                                style =
                                        MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                        ),
                                color = AppleWhite,
                                maxLines = 2
                        )
                }
        }
}

@Composable
fun MiniPlayer(
        song: Song,
        isPlaying: Boolean,
        progress: Float,
        onTogglePlay: () -> Unit,
        onClick: () -> Unit,
        onNextClick: () -> Unit = {},
        onPreviousClick: () -> Unit = {},
        onDragUp: () -> Unit = {},
        modifier: Modifier = Modifier
) {
        Column(
                modifier =
                        modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                .background(SamsungLightGray)
                                .pointerInput(Unit) {
                                        detectVerticalDragGestures { _, dragAmount ->
                                                if (dragAmount < -50) { // Drag up to expand
                                                        onDragUp()
                                                }
                                        }
                                }
        ) {
                // Drag handle
                Box(
                        modifier =
                                Modifier.align(Alignment.CenterHorizontally)
                                        .padding(top = 10.dp)
                                        .width(44.dp)
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(2.5f.dp))
                                        .background(AppleGray.copy(alpha = 0.4f))
                )

                Row(
                        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                        // Album art with default placeholder
                        Box(
                                modifier =
                                        Modifier.size(56.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(SamsungBlack),
                                contentAlignment = Alignment.Center
                        ) {
                                AsyncImage(
                                        model =
                                                ImageRequest.Builder(LocalContext.current)
                                                        .data(song.displayAlbumArt)
                                                        .crossfade(true)
                                                        .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(56.dp)
                                )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        song.displayTitle,
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                ),
                                        color = AppleWhite,
                                        maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                        song.displayArtist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppleGray,
                                        maxLines = 1
                                )
                        }

                        // Controls
                        IconButton(onClick = onPreviousClick, modifier = Modifier.size(36.dp)) {
                                Icon(
                                        imageVector = Icons.Default.SkipPrevious,
                                        contentDescription = "Previous",
                                        tint = SamsungBlue,
                                        modifier = Modifier.size(20.dp)
                                )
                        }

                        IconButton(onClick = { onTogglePlay() }, modifier = Modifier.size(36.dp)) {
                                if (isPlaying) {
                                        Icon(
                                                imageVector = Icons.Default.Pause,
                                                contentDescription = "Pause",
                                                tint = SamsungBlue
                                        )
                                } else {
                                        Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                tint = SamsungBlue
                                        )
                                }
                        }

                        IconButton(onClick = onNextClick, modifier = Modifier.size(36.dp)) {
                                Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Next",
                                        tint = SamsungBlue,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                }

                // Progress Bar (Bottom of MiniPlayer)
                LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = SamsungBlue,
                        trackColor = Color.Transparent
                )
        }
}

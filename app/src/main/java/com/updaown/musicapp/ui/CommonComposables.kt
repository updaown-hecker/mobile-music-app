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
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
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

        LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(songs) { song ->
                        SongItem(
                                song = song,
                                onSongClick = { onSongClick(song) },
                                onMoreClick = { onLongClick(song) }
                        )
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
        }
}

@Composable
fun SongItem(song: Song, onSongClick: () -> Unit, onMoreClick: () -> Unit = {}) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick() }
        ) {
                Row(
                        modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Album art
                        AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                        .data(song.displayAlbumArt)
                                        .crossfade(true)
                                        .build(),
                                contentDescription = "Album Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AppleSlate)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = song.displayTitle,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium
                                        ),
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

                        IconButton(onClick = onMoreClick) {
                                Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = AppleGray,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                }
                HorizontalDivider(
                        modifier = Modifier.padding(start = 80.dp),
                        thickness = 0.5.dp,
                        color = AppleSlate.copy(alpha = 0.5f)
                )
        }
}

@Composable
fun ArtistList(songs: List<Song>, onArtistClick: (String) -> Unit) {
        val artists = songs.map { it.displayArtist }.distinct().sorted()

        if (artists.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No artists", color = AppleGray)
                }
                return
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(artists) { artist ->
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onArtistClick(artist) }
                        ) {
                                Row(
                                        modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                artist,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.Medium
                                                ),
                                                color = AppleWhite,
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1
                                        )
                                        Icon(
                                                imageVector = Icons.Default.SkipNext,
                                                contentDescription = null,
                                                tint = AppleSlate,
                                                modifier = Modifier.size(16.dp)
                                        )
                                }
                                HorizontalDivider(
                                        modifier = Modifier.padding(start = 16.dp),
                                        thickness = 0.5.dp,
                                        color = AppleSlate.copy(alpha = 0.5f)
                                )
                        }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
        }
}

@Composable
fun AlbumList(songs: List<Song>, onAlbumClick: (String) -> Unit) {
        val albums = songs.map { it.displayAlbum }.distinct().sorted()

        if (albums.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No albums", color = AppleGray)
                }
                return
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(albums) { album ->
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onAlbumClick(album) }
                        ) {
                                Row(
                                        modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                album,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.Medium
                                                ),
                                                color = AppleWhite,
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1
                                        )
                                        Icon(
                                                imageVector = Icons.Default.SkipNext,
                                                contentDescription = null,
                                                tint = AppleSlate,
                                                modifier = Modifier.size(16.dp)
                                        )
                                }
                                HorizontalDivider(
                                        modifier = Modifier.padding(start = 16.dp),
                                        thickness = 0.5.dp,
                                        color = AppleSlate.copy(alpha = 0.5f)
                                )
                        }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
        }
}

@Composable
fun FolderList(
        songs: List<Song>,
        playlists: List<PlaylistEntity>,
        onCollectionClick: (SelectedCollection) -> Unit
) {
        val folders = songs.map { it.folderName }.distinct().sorted()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                        if (playlists.isNotEmpty()) {
                                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                                        Text(
                                                "Playlists",
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                                color = AppleWhite,
                                                modifier = Modifier.padding(bottom = 12.dp)
                                        )

                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                playlists.take(2).forEach { playlist ->
                                                        PlaylistGridCard(
                                                                name = playlist.name,
                                                                onClick = {
                                                                        onCollectionClick(SelectedCollection.Playlist(playlist))
                                                                },
                                                                modifier = Modifier.weight(1f)
                                                        )
                                                }
                                                if (playlists.size < 2) {
                                                        Box(modifier = Modifier.weight(1f))
                                                }
                                        }

                                        if (playlists.size > 2) {
                                                Spacer(modifier = Modifier.height(16.dp))
                                                playlists.drop(2).forEach { playlist ->
                                                        PlaylistListItem(playlist, onCollectionClick)
                                                }
                                        }
                                }
                        }
                }

                if (folders.isNotEmpty()) {
                        item {
                                Text(
                                        "Folders",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                        color = AppleWhite,
                                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 12.dp)
                                )
                        }

                        items(folders) { folder ->
                                Column(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onCollectionClick(SelectedCollection.Folder(folder)) }
                                ) {
                                        Row(
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.Folder,
                                                        contentDescription = null,
                                                        tint = AppleSystemBlue,
                                                        modifier = Modifier.size(24.dp)
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                folder,
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                        fontWeight = FontWeight.Medium
                                                                ),
                                                                color = AppleWhite,
                                                                maxLines = 1
                                                        )
                                                        Text(
                                                                "${songs.filter { it.folderName == folder }.size} songs",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = AppleGray
                                                        )
                                                }
                                        }
                                        HorizontalDivider(
                                                modifier = Modifier.padding(start = 56.dp),
                                                thickness = 0.5.dp,
                                                color = AppleSlate.copy(alpha = 0.5f)
                                        )
                                }
                        }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
        }
}

@Composable
fun PlaylistListItem(playlist: PlaylistEntity, onCollectionClick: (SelectedCollection) -> Unit) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCollectionClick(SelectedCollection.Playlist(playlist)) }
        ) {
                Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Box(
                                modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AppleSlate),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(Icons.Default.Folder, null, tint = AppleSystemBlue, modifier = Modifier.size(20.dp))
                        }
                        Text(
                                playlist.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppleWhite,
                                modifier = Modifier.weight(1f)
                        )
                }
                HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        thickness = 0.5.dp,
                        color = AppleSlate.copy(alpha = 0.5f)
                )
        }
}

@Composable
private fun PlaylistGridCard(name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Column(
                modifier = modifier
                        .clickable { onClick() }
                        .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.Start
        ) {
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppleSlate),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                Icons.Default.Folder,
                                contentDescription = null,
                                tint = AppleSystemBlue,
                                modifier = Modifier.size(48.dp)
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                        name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                        ),
                        color = AppleWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
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
        Box(
                modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .height(64.dp)
                        .shadow(20.dp, RoundedCornerShape(14.dp), ambientColor = Color.Black)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GlassBackground)
                        .pointerInput(Unit) {
                                detectVerticalDragGestures { _, dragAmount ->
                                        if (dragAmount < -30) onDragUp()
                                }
                        }
                        .clickable { onClick() }
        ) {
                Row(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                        .data(song.displayAlbumArt)
                                        .crossfade(true)
                                        .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .shadow(4.dp, RoundedCornerShape(6.dp))
                        )

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        song.displayTitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = (-0.2).sp
                                        ),
                                        color = AppleWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                        song.displayArtist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppleGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { onTogglePlay() }, modifier = Modifier.size(40.dp)) {
                                        Icon(
                                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = AppleWhite,
                                                modifier = Modifier.size(32.dp)
                                        )
                                }
                                IconButton(onClick = onNextClick, modifier = Modifier.size(40.dp)) {
                                        Icon(
                                                imageVector = Icons.Default.SkipNext,
                                                contentDescription = null,
                                                tint = AppleWhite,
                                                modifier = Modifier.size(32.dp)
                                        )
                                }
                        }
                }

                // Thin progress bar at the bottom
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .align(Alignment.BottomCenter)
                                .background(AppleWhite.copy(alpha = 0.1f))
                ) {
                        Box(
                                modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .background(AppleWhite.copy(alpha = 0.5f))
                        )
                }
        }
}

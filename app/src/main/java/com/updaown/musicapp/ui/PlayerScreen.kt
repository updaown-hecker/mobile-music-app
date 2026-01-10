package com.updaown.musicapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.updaown.musicapp.ui.theme.AppleGray
import com.updaown.musicapp.ui.theme.AppleWhite
import com.updaown.musicapp.ui.theme.SamsungBlack
import com.updaown.musicapp.ui.theme.SamsungBlue
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(viewModel: MainViewModel, onCollapse: () -> Unit) {
        val song = viewModel.currentSong ?: return

        // Drag to Dismiss State
        var offsetY by remember { mutableFloatStateOf(0f) }

        // Metadata Editor State
        var showMetadataEditor by remember { mutableStateOf(false) }

        // Dynamic Animations for Text/Controls ensuring contrast on dark background
        // We force white text because the background is deep dark "Liquid"
        val contentColor = AppleWhite
        val secondaryColor = AppleGray

        fun formatTime(ms: Long): String {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
                return String.format("%02d:%02d", minutes, seconds)
        }

        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .offset { IntOffset(0, offsetY.roundToInt()) }
                                .pointerInput(Unit) {
                                        detectVerticalDragGestures(
                                                onDragEnd = {
                                                        if (offsetY > 300f) {
                                                                onCollapse()
                                                        } else {
                                                                offsetY = 0f
                                                        }
                                                },
                                                onDragCancel = { offsetY = 0f },
                                                onVerticalDrag = { change, dragAmount ->
                                                        change.consume()
                                                        val newOffset = offsetY + dragAmount
                                                        if (newOffset >= 0) {
                                                                offsetY = newOffset
                                                        } else {
                                                                offsetY =
                                                                        newOffset / 3f // Resistance
                                                        }
                                                }
                                        )
                                }
                                .background(SamsungBlack) // Base
        ) {
                // Dynamic Gradient Background (Apple/Modern Style)
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        viewModel.dominantColor
                                                                                .copy(alpha = 0.6f),
                                                                        SamsungBlack
                                                                )
                                                )
                                        )
                )

                // Overlay to ensure text readability
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))

                // Top Bar
                Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                IconButton(onClick = onCollapse) {
                                        Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Collapse",
                                                tint = contentColor,
                                                modifier = Modifier.size(32.dp)
                                        )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                // Capsule Indicator (Apple Style)
                                Box(
                                        modifier =
                                                Modifier.width(40.dp)
                                                        .height(5.dp)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(contentColor.copy(alpha = 0.2f))
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { showMetadataEditor = true }) {
                                        Icon(Icons.Default.MoreVert, "More", tint = contentColor)
                                }
                        }

                        // Main Content
                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .weight(1f)
                                                .padding(horizontal = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                                // Album Art
                                Surface(
                                        modifier =
                                                Modifier.fillMaxWidth(0.85f)
                                                        .aspectRatio(1f)
                                                        .shadow(
                                                                elevation = 50.dp,
                                                                shape = RoundedCornerShape(20.dp),
                                                                spotColor = viewModel.dominantColor,
                                                                ambientColor =
                                                                        viewModel.dominantColor
                                                        )
                                                        .clip(RoundedCornerShape(20.dp)),
                                        color = Color.Transparent
                                ) {
                                        AsyncImage(
                                                model =
                                                        ImageRequest.Builder(LocalContext.current)
                                                                .data(song.albumArtUri)
                                                                .crossfade(true)
                                                                .build(),
                                                contentDescription = "Album Art",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                        )
                                }

                                Spacer(modifier = Modifier.height(48.dp))

                                // Title & Artist
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        text = song.title,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineMedium.copy(
                                                                        fontWeight = FontWeight.Bold
                                                                ),
                                                        color = contentColor,
                                                        maxLines = 1
                                                )
                                                Text(
                                                        text = song.artist,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        color = contentColor.copy(alpha = 0.7f),
                                                        maxLines = 1
                                                )
                                        }

                                        IconButton(onClick = { viewModel.toggleFavorite(song) }) {
                                                Icon(
                                                        imageVector =
                                                                if (viewModel.favorites.contains(
                                                                                song.id
                                                                        )
                                                                )
                                                                        Icons.Default.Favorite
                                                                else Icons.Default.FavoriteBorder,
                                                        contentDescription = "Favorite",
                                                        tint =
                                                                if (viewModel.favorites.contains(
                                                                                song.id
                                                                        )
                                                                )
                                                                        SamsungBlue
                                                                else
                                                                        contentColor.copy(
                                                                                alpha = 0.4f
                                                                        ), // Samsung Blue check
                                                        modifier = Modifier.size(32.dp)
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Seekbar (Apple Style: Thickened)
                                Slider(
                                        value = viewModel.progress,
                                        onValueChange = { viewModel.seekTo(it) },
                                        colors =
                                                SliderDefaults.colors(
                                                        thumbColor = contentColor,
                                                        activeTrackColor = contentColor,
                                                        inactiveTrackColor =
                                                                contentColor.copy(alpha = 0.2f)
                                                ),
                                        modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                formatTime(viewModel.currentPosition),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = secondaryColor
                                        )
                                        Text(
                                                formatTime(song.duration),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = secondaryColor
                                        )
                                }

                                Spacer(modifier = Modifier.height(48.dp))

                                // Controls
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        IconButton(onClick = { viewModel.toggleShuffle() }) {
                                                Icon(
                                                        Icons.Default.Shuffle,
                                                        null,
                                                        tint =
                                                                if (viewModel.isShuffleEnabled)
                                                                        SamsungBlue
                                                                else secondaryColor
                                                )
                                        }

                                        IconButton(
                                                onClick = { viewModel.skipPrevious() },
                                                modifier = Modifier.size(48.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.SkipPrevious,
                                                        null,
                                                        modifier = Modifier.size(36.dp),
                                                        tint = contentColor
                                                )
                                        }

                                        // Play/Pause - Big Button
                                        Box(
                                                modifier =
                                                        Modifier.size(80.dp)
                                                                .clip(CircleShape)
                                                                .background(contentColor)
                                                                .clickable {
                                                                        viewModel.togglePlayPause()
                                                                },
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        if (viewModel.isPlaying) Icons.Default.Pause
                                                        else Icons.Default.PlayArrow,
                                                        null,
                                                        tint = SamsungBlack,
                                                        modifier = Modifier.size(40.dp)
                                                )
                                        }

                                        IconButton(
                                                onClick = { viewModel.skipNext() },
                                                modifier = Modifier.size(48.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.SkipNext,
                                                        null,
                                                        modifier = Modifier.size(36.dp),
                                                        tint = contentColor
                                                )
                                        }

                                        IconButton(onClick = { viewModel.toggleRepeat() }) {
                                                Icon(
                                                        if (viewModel.repeatMode ==
                                                                        Player.REPEAT_MODE_ONE
                                                        )
                                                                Icons.Default.RepeatOne
                                                        else Icons.Default.Repeat,
                                                        null,
                                                        tint =
                                                                if (viewModel.repeatMode !=
                                                                                Player.REPEAT_MODE_OFF
                                                                )
                                                                        SamsungBlue
                                                                else secondaryColor
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.weight(1f))
                        }
                }
        }

        // Metadata Editor Dialog
        if (showMetadataEditor) {
                MetadataEditorDialog(
                        song = song,
                        viewModel = viewModel,
                        onDismiss = { showMetadataEditor = false }
                )
        }
}

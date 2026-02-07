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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.updaown.musicapp.ui.theme.AppleGray
import com.updaown.musicapp.ui.theme.AppleSystemBlue
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
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                IconButton(
                                        onClick = onCollapse,
                                        modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .size(40.dp)
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Collapse",
                                                tint = contentColor,
                                                modifier = Modifier.size(24.dp)
                                        )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                // Capsule Indicator (Apple Style)
                                Box(
                                        modifier = Modifier
                                                .width(40.dp)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(contentColor.copy(alpha = 0.3f))
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                        onClick = { showMetadataEditor = true },
                                        modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .size(40.dp)
                                ) {
                                        Icon(
                                                Icons.Default.MoreVert,
                                                "More",
                                                tint = contentColor,
                                                modifier = Modifier.size(24.dp)
                                        )
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
                                        modifier = Modifier
                                                .fillMaxWidth(0.8f)
                                                .aspectRatio(1f)
                                                .shadow(
                                                        elevation = 40.dp,
                                                        shape = RoundedCornerShape(24.dp),
                                                        spotColor = viewModel.dominantColor.copy(alpha = 0.4f),
                                                        ambientColor = viewModel.dominantColor.copy(alpha = 0.2f)
                                                )
                                                .clip(RoundedCornerShape(24.dp)),
                                        color = Color.Transparent
                                ) {
                                        AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                        .data(song.albumArtUri)
                                                        .crossfade(true)
                                                        .placeholder(android.R.drawable.ic_media_play)
                                                        .error(android.R.drawable.ic_media_play)
                                                        .build(),
                                                contentDescription = "Album Art",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                        )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Title & Artist
                                Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Text(
                                                text = song.title,
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                        fontWeight = FontWeight.Medium,
                                                        letterSpacing = 0.2.sp
                                                ),
                                                color = contentColor,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text = song.artist,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                        letterSpacing = 0.1.sp
                                                ),
                                                color = contentColor.copy(alpha = 0.8f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                        )
                                        if (song.album.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        text = song.album,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = contentColor.copy(alpha = 0.6f),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Favorite Button
                                IconButton(
                                        onClick = { viewModel.toggleFavorite(song) },
                                        modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                        if (viewModel.favorites.contains(song.id))
                                                                AppleSystemBlue.copy(alpha = 0.2f)
                                                        else
                                                                contentColor.copy(alpha = 0.1f)
                                                )
                                                .size(48.dp)
                                ) {
                                        Icon(
                                                imageVector = if (viewModel.favorites.contains(song.id))
                                                        Icons.Default.Favorite
                                                else 
                                                        Icons.Default.FavoriteBorder,
                                                contentDescription = "Favorite",
                                                tint = if (viewModel.favorites.contains(song.id))
                                                        AppleSystemBlue
                                                else
                                                        contentColor.copy(alpha = 0.7f),
                                                modifier = Modifier.size(24.dp)
                                        )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Enhanced Seekbar (Apple Style)
                                Box(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp)
                                ) {
                                        Slider(
                                                value = viewModel.progress,
                                                onValueChange = { viewModel.seekTo(it) },
                                                colors = SliderDefaults.colors(
                                                        thumbColor = contentColor,
                                                        activeTrackColor = contentColor,
                                                        inactiveTrackColor = contentColor.copy(alpha = 0.2f),
                                                        disabledThumbColor = contentColor.copy(alpha = 0.5f),
                                                        disabledActiveTrackColor = contentColor.copy(alpha = 0.5f)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                }
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                text = formatTime(viewModel.currentPosition),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                        letterSpacing = 0.2.sp
                                                ),
                                                color = secondaryColor
                                        )
                                        Text(
                                                text = formatTime(song.duration),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                        letterSpacing = 0.2.sp
                                                ),
                                                color = secondaryColor
                                        )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Enhanced Controls
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        // Shuffle
                                        IconButton(
                                                onClick = { viewModel.toggleShuffle() },
                                                modifier = Modifier
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .size(44.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.Shuffle,
                                                        contentDescription = "Shuffle",
                                                        tint = if (viewModel.isShuffleEnabled)
                                                                AppleSystemBlue
                                                        else secondaryColor,
                                                        modifier = Modifier.size(20.dp)
                                                )
                                        }

                                        // Previous
                                        IconButton(
                                                onClick = { viewModel.skipPrevious() },
                                                modifier = Modifier.size(52.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.SkipPrevious,
                                                        contentDescription = "Previous",
                                                        modifier = Modifier.size(28.dp),
                                                        tint = contentColor
                                                )
                                        }

                                        // Play/Pause - Enhanced
                                        Surface(
                                                modifier = Modifier
                                                        .size(72.dp)
                                                        .clip(CircleShape)
                                                        .clickable { viewModel.togglePlayPause() },
                                                color = contentColor,
                                                shadowElevation = 8.dp
                                        ) {
                                                Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.fillMaxSize()
                                                ) {
                                                        Icon(
                                                                imageVector = if (viewModel.isPlaying) 
                                                                        Icons.Default.Pause
                                                                else 
                                                                        Icons.Default.PlayArrow,
                                                                contentDescription = if (viewModel.isPlaying) "Pause" else "Play",
                                                                tint = SamsungBlack,
                                                                modifier = Modifier.size(32.dp)
                                                        )
                                                }
                                        }

                                        // Next
                                        IconButton(
                                                onClick = { viewModel.skipNext() },
                                                modifier = Modifier.size(52.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.SkipNext,
                                                        contentDescription = "Next",
                                                        modifier = Modifier.size(28.dp),
                                                        tint = contentColor
                                                )
                                        }

                                        // Repeat
                                        IconButton(
                                                onClick = { viewModel.toggleRepeat() },
                                                modifier = Modifier
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .size(44.dp)
                                        ) {
                                                Icon(
                                                        imageVector = if (viewModel.repeatMode == Player.REPEAT_MODE_ONE)
                                                                Icons.Default.RepeatOne
                                                        else
                                                                Icons.Default.Repeat,
                                                        contentDescription = "Repeat",
                                                        tint = if (viewModel.repeatMode != Player.REPEAT_MODE_OFF)
                                                                AppleSystemBlue
                                                        else
                                                                secondaryColor,
                                                        modifier = Modifier.size(20.dp)
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

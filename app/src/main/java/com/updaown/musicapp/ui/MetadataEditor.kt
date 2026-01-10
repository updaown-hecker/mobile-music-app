package com.updaown.musicapp.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.updaown.musicapp.data.Song
import com.updaown.musicapp.ui.theme.*

@Composable
fun MetadataEditorDialog(
    song: Song,
    viewModel: MainViewModel? = null,
    onSave: (title: String, artist: String, album: String) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit
) {
    var editedTitle by remember { mutableStateOf(song.displayTitle) }
    var editedArtist by remember { mutableStateOf(song.displayArtist) }
    var editedAlbum by remember { mutableStateOf(song.displayAlbum) }
    var editedAlbumArtUri by remember { mutableStateOf<String?>(song.customAlbumArtPath) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            editedAlbumArtUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SamsungDarkGray,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .clip(RoundedCornerShape(20.dp)),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Edit Song Info",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppleWhite
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppleGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Album Art Preview with edit button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SamsungBlack)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (editedAlbumArtUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(editedAlbumArtUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            "♪",
                            style = MaterialTheme.typography.displayLarge,
                            color = AppleGray
                        )
                    }
                    
                    // Edit overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Change image",
                            tint = AppleWhite,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Title Field
                EditTextField(
                    label = "Song Title",
                    value = editedTitle,
                    onValueChange = { editedTitle = it }
                )

                // Artist Field
                EditTextField(
                    label = "Artist",
                    value = editedArtist,
                    onValueChange = { editedArtist = it }
                )

                // Album Field
                EditTextField(
                    label = "Album",
                    value = editedAlbum,
                    onValueChange = { editedAlbum = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel?.updateSongMetadata(song.id, editedTitle, editedArtist, editedAlbum, editedAlbumArtUri)
                    onSave(editedTitle, editedArtist, editedAlbum)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Save", color = AppleWhite)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(40.dp)
            ) {
                Text("Cancel", color = AppleGray)
            }
        }
    )
}

@Composable
private fun EditTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, color = AppleGray, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SamsungBlack)
                .padding(12.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = AppleWhite, fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    innerTextField()
                }
            )
        }
    }
}

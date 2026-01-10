package com.updaown.musicapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.updaown.musicapp.data.Song
import com.updaown.musicapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    var deviceSongs by remember { mutableStateOf<List<Song>?>(null) }
    val selectedSongs = remember { mutableStateListOf<Song>() }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { deviceSongs = viewModel.getDeviceSongsForImport() }

    // Filter songs and sort alphabetically by title
    val filteredSongs =
            remember(deviceSongs, searchQuery) {
                deviceSongs?.let { songs ->
                    val filtered =
                            if (searchQuery.isBlank()) {
                                songs
                            } else {
                                songs.filter {
                                    it.displayTitle.contains(searchQuery, ignoreCase = true) ||
                                            it.displayArtist.contains(
                                                    searchQuery,
                                                    ignoreCase = true
                                            )
                                }
                            }
                    filtered.sortedBy { it.displayTitle }
                }
                        ?: emptyList()
            }

    // Get unique first letters for index
    val indexLetters =
            remember(filteredSongs) {
                filteredSongs.mapNotNull { it.displayTitle.firstOrNull()?.uppercaseChar() }
                        .distinct()
                        .sorted()
            }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    "Select tracks",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                    ),
                                    color = AppleWhite
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = AppleWhite
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = AppleWhite)
                            }
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = SamsungBlack,
                                        titleContentColor = AppleWhite,
                                        navigationIconContentColor = AppleWhite,
                                        actionIconContentColor = AppleWhite
                                )
                )
            },
            floatingActionButton = {
                if (selectedSongs.isNotEmpty()) {
                    FloatingActionButton(
                            onClick = {
                                viewModel.importSongs(selectedSongs.toList())
                                onBack()
                            },
                            containerColor = SamsungBlue,
                            contentColor = Color.White
                    ) {
                        Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import (${selectedSongs.size})")
                        }
                    }
                }
            },
            containerColor = SamsungBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search Bar
                SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                // Select All Row
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (filteredSongs.isEmpty()) AppleGray else SamsungBlue,
                            modifier = Modifier.size(24.dp).clickable {
                                if (selectedSongs.size == filteredSongs.size) {
                                    selectedSongs.clear()
                                } else {
                                    selectedSongs.clear()
                                    selectedSongs.addAll(filteredSongs)
                                }
                            }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                            "All",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppleWhite
                    )
                }

                if (deviceSongs == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SamsungBlue)
                    }
                } else if (filteredSongs.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                                if (searchQuery.isBlank()) "No music found on device."
                                else "No results for \"$searchQuery\"",
                                color = AppleGray
                        )
                    }
                } else {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            state = listState,
                            contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredSongs, key = { "song_${it.id}" }) { song ->
                            val isSelected = selectedSongs.contains(song)
                            ImportSongItem(
                                    song = song,
                                    isSelected = isSelected,
                                    onToggle = {
                                        if (isSelected) selectedSongs.remove(song)
                                        else selectedSongs.add(song)
                                    }
                            )
                        }
                    }
                }
            }

            // Alphabetical Index on the right
            if (filteredSongs.isNotEmpty() && searchQuery.isBlank()) {
                AlphabeticalIndex(
                        letters = indexLetters,
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
            modifier =
                    modifier
                            .background(SamsungDarkGray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = AppleGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle =
                            TextStyle(
                                    color = AppleWhite,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                            ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                    "Search songs...",
                                    color = AppleGray.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        innerTextField()
                    },
                    singleLine = true
            )
        }
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppleGray)
            }
        }
    }
}


@Composable
fun ImportSongItem(song: Song, isSelected: Boolean, onToggle: () -> Unit) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clickable { onToggle() }
                            .background(SamsungBlack)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Checkbox - Circle style
        Box(
                modifier = Modifier
                        .size(24.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(
                                if (isSelected) SamsungBlue
                                else SamsungDarkGray
                        ),
                contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = AppleWhite,
                        modifier = Modifier.size(16.dp)
                )
            }
        }

        // Album Art
        Surface(
                modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                color = SamsungDarkGray,
                shadowElevation = 2.dp
        ) {
            AsyncImage(
                    model =
                            ImageRequest.Builder(LocalContext.current)
                                    .data(song.displayAlbumArt)
                                    .crossfade(true)
                                    .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = song.displayTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    color = AppleWhite,
                    overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = song.displayArtist,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = AppleGray,
                    overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AlphabeticalIndex(letters: List<Char>, modifier: Modifier = Modifier) {
    Column(
            modifier =
                    modifier
                            .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        letters.forEach { letter ->
            Box(
                    modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SamsungDarkGray.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = letter.toString(),
                        fontSize = 10.sp,
                        color = AppleGray,
                        style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}


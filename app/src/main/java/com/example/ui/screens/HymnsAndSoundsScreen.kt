package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChurchHymn
import com.example.model.Language
import com.example.model.Screen
import com.example.model.Strings
import com.example.ui.components.GrjTopAppBar
import com.example.ui.theme.GrjBlue40
import com.example.ui.theme.GrjGold50
import com.example.ui.theme.GrjGold90
import com.example.util.SoundManager
import com.example.viewmodel.QuizViewModel

@Composable
fun HymnsAndSoundsScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val churchSoundsTheme by viewModel.churchSoundsThemeEnabled.collectAsState()
    val hymns = viewModel.hymns

    var currentlyPlayingSound by remember { mutableStateOf<String?>(null) }
    var expandedHymnId by remember { mutableStateOf<String?>(hymns.firstOrNull()?.id) }

    Scaffold(
        topBar = {
            GrjTopAppBar(
                title = Strings.hymnsAndSounds(currentLanguage),
                currentLanguage = currentLanguage,
                soundEnabled = soundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onLanguageClick = {
                    val nextLang = when (currentLanguage) {
                        Language.PORTUGUESE -> Language.ENGLISH
                        Language.ENGLISH -> Language.SHONA
                        Language.SHONA -> Language.PORTUGUESE
                    }
                    viewModel.setLanguage(nextLang)
                },
                onBackClick = { viewModel.navigateTo(Screen.HOME) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Description Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("hymns_header_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(GrjGold50, GrjBlue40))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Strings.hymnsAndSounds(currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Strings.hymnsAndSoundsDesc(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Sound Theme Toggle Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("sound_theme_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, GrjGold50.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = GrjGold50,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = Strings.soundThemeChurch(currentLanguage),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (churchSoundsTheme) "Sinos sagrados e coro nos jogos" else "Efeitos sonoros clássicos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = churchSoundsTheme,
                            onCheckedChange = { viewModel.toggleChurchSoundsTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GrjGold50,
                                checkedTrackColor = GrjGold90
                            ),
                            modifier = Modifier.testTag("church_sound_theme_switch")
                        )
                    }
                }
            }

            // Section: Sons Sagrados Interativos
            item {
                Text(
                    text = Strings.sacredSoundsTitle(currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Interactive Church Sounds Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // 1. Bhero reKunamata (Holy Sanctuary Bell)
                    SacredSoundPlayRow(
                        title = Strings.churchBellName(currentLanguage),
                        subtitle = "Sino solene de Zvimba chamando para oração",
                        soundType = SoundManager.SoundType.CHURCH_BELL,
                        isPlaying = currentlyPlayingSound == "bell",
                        onPlay = {
                            currentlyPlayingSound = "bell"
                            viewModel.playHymnSound(SoundManager.SoundType.CHURCH_BELL)
                        }
                    )

                    // 2. Kwaya yeGuta (A Cappella Choir)
                    SacredSoundPlayRow(
                        title = Strings.churchChoirName(currentLanguage),
                        subtitle = "Coro a quatro vozes cantando louvores a Jeová",
                        soundType = SoundManager.SoundType.CHURCH_CHOIR,
                        isPlaying = currentlyPlayingSound == "choir",
                        onPlay = {
                            currentlyPlayingSound = "choir"
                            viewModel.playHymnSound(SoundManager.SoundType.CHURCH_CHOIR)
                        }
                    )

                    // 3. Kuuchira (Sacred Clapping)
                    SacredSoundPlayRow(
                        title = Strings.churchClappingName(currentLanguage),
                        subtitle = "Palmas sagradas de reverência e júbilo",
                        soundType = SoundManager.SoundType.CHURCH_CLAPPING,
                        isPlaying = currentlyPlayingSound == "clapping",
                        onPlay = {
                            currentlyPlayingSound = "clapping"
                            viewModel.playHymnSound(SoundManager.SoundType.CHURCH_CLAPPING)
                        }
                    )

                    // 4. Bênção e Paz (Ngaizviitwe)
                    SacredSoundPlayRow(
                        title = Strings.churchBlessingName(currentLanguage),
                        subtitle = "Harmonia coral de bênção e despedida em paz",
                        soundType = SoundManager.SoundType.CHURCH_BLESSING,
                        isPlaying = currentlyPlayingSound == "blessing",
                        onPlay = {
                            currentlyPlayingSound = "blessing"
                            viewModel.playHymnSound(SoundManager.SoundType.CHURCH_BLESSING)
                        }
                    )
                }
            }

            // Section: Hinos Sagrados (Nziyo dzeGuta)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hinos da Guta raJehovah (Nziyo Tsvene)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Hymn Cards with expandable lyrics and sound trigger
            items(hymns) { hymn ->
                HymnExpandableCard(
                    hymn = hymn,
                    currentLanguage = currentLanguage,
                    isExpanded = expandedHymnId == hymn.id,
                    onToggleExpand = {
                        expandedHymnId = if (expandedHymnId == hymn.id) null else hymn.id
                    },
                    onPlaySound = {
                        viewModel.playHymnSound(hymn.soundType)
                    }
                )
            }
        }
    }
}

@Composable
private fun SacredSoundPlayRow(
    title: String,
    subtitle: String,
    soundType: SoundManager.SoundType,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onPlay)
            .testTag("sacred_sound_${soundType.name.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GrjGold90),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = GrjGold50,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onPlay,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Tocar", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Tocar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun HymnExpandableCard(
    hymn: ChurchHymn,
    currentLanguage: Language,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPlaySound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hymn_card_${hymn.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, if (isExpanded) GrjGold50 else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hymn.getTitle(currentLanguage),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (currentLanguage != Language.SHONA) {
                        Text(
                            text = hymn.title["sn"] ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Recolher" else "Expandir"
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Liturgical context note
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = hymn.getContext(currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Play Hymn button
                    OutlinedButton(
                        onClick = onPlaySound,
                        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("play_hymn_sound_${hymn.id}"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Escutar Harmonia Sagrada", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stanzas / Lyrics
                    Text(
                        text = "Letra do Cântico:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    hymn.getLyrics(currentLanguage).forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

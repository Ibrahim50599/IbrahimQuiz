package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.theme.GrjBlue40
import com.example.ui.theme.GrjCorrect
import com.example.ui.theme.GrjGold50
import com.example.ui.theme.GrjGold90
import com.example.ui.theme.GrjWrong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrjTopAppBar(
    title: String,
    currentLanguage: Language,
    onLanguageClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    soundEnabled: Boolean? = null,
    onToggleSound: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
        },
        actions = {
            if (soundEnabled != null && onToggleSound != null) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.testTag("top_bar_sound_button")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (soundEnabled) "Som Ligado" else "Som Silenciado",
                        tint = if (soundEnabled) GrjGold50 else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Surface(
                onClick = onLanguageClick,
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag("language_selector_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentLanguage.flag, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentLanguage.shortCode,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@Composable
fun LanguageSelectorRow(
    currentLanguage: Language,
    onSelectLanguage: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Language.entries.forEach { lang ->
            val isSelected = lang == currentLanguage
            val bgColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                label = "lang_bg"
            )
            val textColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "lang_txt"
            )

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("lang_chip_${lang.code}")
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectLanguage(lang) },
                colors = CardDefaults.cardColors(containerColor = bgColor),
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) BorderStroke(1.5.dp, GrjGold50) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = lang.flag, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = lang.displayName,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun QuizTimerBar(
    timeRemaining: Int,
    totalTime: Int,
    modifier: Modifier = Modifier
) {
    if (totalTime <= 0) return

    val progress = (timeRemaining.toFloat() / totalTime).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "timer_progress"
    )

    val timerColor = when {
        progress > 0.5f -> MaterialTheme.colorScheme.primary
        progress > 0.25f -> GrjGold50
        else -> GrjWrong
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = timerColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${timeRemaining}s",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = timerColor
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = timerColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun QuizOptionCard(
    optionLetter: String,
    optionText: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isEliminated: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isCorrect == true -> GrjCorrect
        isSelected && isCorrect == false -> GrjWrong
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    val containerColor = when {
        isEliminated -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        isCorrect == true -> GrjCorrect.copy(alpha = 0.15f)
        isSelected && isCorrect == false -> GrjWrong.copy(alpha = 0.15f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("option_card_$optionLetter")
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled && !isEliminated) { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (borderColor != Color.Transparent) 2.dp else 1.dp,
            color = if (borderColor != Color.Transparent) borderColor else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isEliminated) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option Badge (A, B, C, D)
            val badgeBg = when {
                isCorrect == true -> GrjCorrect
                isSelected && isCorrect == false -> GrjWrong
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.primaryContainer
            }
            val badgeTextColor = when {
                isCorrect == true || (isSelected && isCorrect == false) || isSelected -> Color.White
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isEliminated) Color.Gray.copy(alpha = 0.4f) else badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionLetter,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = badgeTextColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isEliminated) Color.Gray else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (isCorrect == true) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Correto",
                    tint = GrjCorrect,
                    modifier = Modifier.size(24.dp)
                )
            } else if (isSelected && isCorrect == false) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Incorreto",
                    tint = GrjWrong,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

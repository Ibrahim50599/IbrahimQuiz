package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizQuestion
import com.example.model.Screen
import com.example.model.Strings
import com.example.ui.theme.GrjCorrect
import com.example.ui.theme.GrjGold50
import com.example.ui.theme.GrjPlayer1
import com.example.ui.theme.GrjPlayer2
import com.example.ui.theme.GrjWrong
import com.example.viewmodel.QuizViewModel

@Composable
fun DuelQuizScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val duelState by viewModel.duelState.collectAsState()
    val question = duelState.currentQuestion ?: return

    val options = question.getOptionList(currentLanguage)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP HALF: PLAYER 1
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(GrjPlayer1.copy(alpha = 0.05f))
                .let { if (duelState.invertP1) it.rotate(180f) else it }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            PlayerPane(
                playerNumber = 1,
                playerName = duelState.p1Name,
                playerScore = duelState.p1Score,
                themeColor = GrjPlayer1,
                question = question,
                options = options,
                selectedOption = duelState.p1Answer,
                isCorrect = duelState.p1Correct,
                roundResolved = duelState.roundResolved,
                onSelectOption = { viewModel.submitDuelAnswer(player = 1, optionIndex = it) },
                currentLanguage = currentLanguage
            )
        }

        // CENTER DIVIDER / SCOREBOARD
        CenterScoreDivider(
            roundNumber = duelState.currentRoundIndex + 1,
            totalRounds = duelState.totalRounds,
            timeRemaining = duelState.timeRemaining,
            p1Score = duelState.p1Score,
            p2Score = duelState.p2Score,
            p1Name = duelState.p1Name,
            p2Name = duelState.p2Name,
            roundResolved = duelState.roundResolved,
            roundRecap = duelState.roundRecap,
            onNextRound = { viewModel.advanceDuelRound() },
            onExit = { viewModel.navigateTo(Screen.HOME) },
            currentLanguage = currentLanguage
        )

        // BOTTOM HALF: PLAYER 2
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(GrjPlayer2.copy(alpha = 0.05f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            PlayerPane(
                playerNumber = 2,
                playerName = duelState.p2Name,
                playerScore = duelState.p2Score,
                themeColor = GrjPlayer2,
                question = question,
                options = options,
                selectedOption = duelState.p2Answer,
                isCorrect = duelState.p2Correct,
                roundResolved = duelState.roundResolved,
                onSelectOption = { viewModel.submitDuelAnswer(player = 2, optionIndex = it) },
                currentLanguage = currentLanguage
            )
        }
    }
}

@Composable
private fun PlayerPane(
    playerNumber: Int,
    playerName: String,
    playerScore: Int,
    themeColor: Color,
    question: QuizQuestion,
    options: List<String>,
    selectedOption: Int?,
    isCorrect: Boolean?,
    roundResolved: Boolean,
    onSelectOption: (Int) -> Unit,
    currentLanguage: com.example.model.Language,
    modifier: Modifier = Modifier
) {
    val letters = listOf("A", "B", "C", "D")

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Player info bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(themeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P$playerNumber",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = playerName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = themeColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = themeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "$playerScore pts",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = themeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Question text
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = question.getQuestionText(currentLanguage),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(10.dp)
            )
        }

        // 2x2 Grid of Option Buttons (Optimized for dual-screen interaction)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DuelOptionButton(
                    letter = "A",
                    text = options.getOrElse(0) { "" },
                    index = 0,
                    selectedIndex = selectedOption,
                    correctIndex = question.correctIndex,
                    roundResolved = roundResolved,
                    themeColor = themeColor,
                    onSelect = onSelectOption,
                    modifier = Modifier.weight(1f)
                )
                DuelOptionButton(
                    letter = "B",
                    text = options.getOrElse(1) { "" },
                    index = 1,
                    selectedIndex = selectedOption,
                    correctIndex = question.correctIndex,
                    roundResolved = roundResolved,
                    themeColor = themeColor,
                    onSelect = onSelectOption,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DuelOptionButton(
                    letter = "C",
                    text = options.getOrElse(2) { "" },
                    index = 2,
                    selectedIndex = selectedOption,
                    correctIndex = question.correctIndex,
                    roundResolved = roundResolved,
                    themeColor = themeColor,
                    onSelect = onSelectOption,
                    modifier = Modifier.weight(1f)
                )
                DuelOptionButton(
                    letter = "D",
                    text = options.getOrElse(3) { "" },
                    index = 3,
                    selectedIndex = selectedOption,
                    correctIndex = question.correctIndex,
                    roundResolved = roundResolved,
                    themeColor = themeColor,
                    onSelect = onSelectOption,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DuelOptionButton(
    letter: String,
    text: String,
    index: Int,
    selectedIndex: Int?,
    correctIndex: Int,
    roundResolved: Boolean,
    themeColor: Color,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedIndex == index
    val isCorrect = roundResolved && index == correctIndex
    val isWrong = roundResolved && isSelected && index != correctIndex

    val bg = when {
        isCorrect -> GrjCorrect
        isWrong -> GrjWrong
        isSelected -> themeColor
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        isCorrect || isWrong || isSelected -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = selectedIndex == null && !roundResolved) { onSelect(index) },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = BorderStroke(
            1.dp,
            if (isSelected) Color.White else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$letter.",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CenterScoreDivider(
    roundNumber: Int,
    totalRounds: Int,
    timeRemaining: Int,
    p1Score: Int,
    p2Score: Int,
    p1Name: String,
    p2Name: String,
    roundResolved: Boolean,
    roundRecap: com.example.model.DuelRoundRecap?,
    onNextRound: () -> Unit,
    onExit: () -> Unit,
    currentLanguage: com.example.model.Language,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exit button
                IconButton(
                    onClick = onExit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Sair",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Score Display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$p1Score",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GrjPlayer1
                    )
                    Text(
                        text = "  VS  ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$p2Score",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GrjPlayer2
                    )
                }

                // Timer or Round info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (timeRemaining <= 5) GrjWrong else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${timeRemaining}s",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (timeRemaining <= 5) GrjWrong else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "$roundNumber/$totalRounds",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Dynamic intelligent duel commentary
            if (!roundResolved) {
                Text(
                    text = Strings.duelDynamicStatus(
                        p1Leading = p1Score > p2Score,
                        p2Leading = p2Score > p1Score,
                        tied = p1Score == p2Score,
                        lang = currentLanguage
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Next round button banner if resolved
            if (roundResolved) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val winnerName = when (roundRecap?.winnerPlayer) {
                        1 -> p1Name
                        2 -> p2Name
                        else -> null
                    }

                    Text(
                        text = if (winnerName != null) Strings.roundPointsAwarded(winnerName, 150, currentLanguage) else Strings.bothWrong(currentLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (winnerName != null) GrjCorrect else GrjWrong
                    )

                    Button(
                        onClick = onNextRound,
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("duel_next_round_button"),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = Strings.nextQuestion(currentLanguage),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

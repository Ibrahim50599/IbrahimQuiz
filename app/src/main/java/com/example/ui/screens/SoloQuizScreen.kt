package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Strings
import com.example.ui.components.GrjTopAppBar
import com.example.ui.components.QuizOptionCard
import com.example.ui.components.QuizTimerBar
import com.example.ui.theme.GrjCorrect
import com.example.ui.theme.GrjGold50
import com.example.ui.theme.GrjGold90
import com.example.ui.theme.GrjWrong
import com.example.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoloQuizScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val soloState by viewModel.soloState.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val question = soloState.currentQuestion ?: return

    val options = question.getOptionList(currentLanguage)
    val letters = listOf("A", "B", "C", "D")

    Scaffold(
        topBar = {
            GrjTopAppBar(
                title = Strings.questionProgress(
                    current = soloState.currentIndex + 1,
                    total = soloState.questions.size,
                    lang = currentLanguage
                ),
                currentLanguage = currentLanguage,
                soundEnabled = soundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onLanguageClick = {
                    val nextLang = when (currentLanguage) {
                        com.example.model.Language.PORTUGUESE -> com.example.model.Language.ENGLISH
                        com.example.model.Language.ENGLISH -> com.example.model.Language.SHONA
                        com.example.model.Language.SHONA -> com.example.model.Language.PORTUGUESE
                    }
                    viewModel.setLanguage(nextLang)
                },
                onBackClick = { viewModel.handleBack() }
            )
        },
        bottomBar = {
            if (soloState.isAnswered) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        val isLast = soloState.currentIndex + 1 >= soloState.questions.size
                        Button(
                            onClick = { viewModel.nextSoloQuestion() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("next_question_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (isLast) Strings.seeResults(currentLanguage) else Strings.nextQuestion(currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Row: Score, Streak, Lifelines
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.testTag("solo_score_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${Strings.scoreLabel(currentLanguage)}: ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = soloState.score.toString(),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Streak pill
                if (soloState.streak > 1) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GrjGold90,
                        modifier = Modifier.testTag("solo_streak_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = GrjGold50,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "x${soloState.streak}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF6B4400)
                            )
                        }
                    }
                }

                // Lifelines (50:50, +10s, and Smart Hint)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.use5050Lifeline() },
                        enabled = !soloState.lifeline5050Used && !soloState.isAnswered,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("lifeline_5050_button")
                    ) {
                        Icon(imageVector = Icons.Default.Filter2, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "50:50", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = { viewModel.useExtraTimeLifeline() },
                        enabled = !soloState.lifelineTimeUsed && !soloState.isAnswered,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("lifeline_time_button")
                    ) {
                        Icon(imageVector = Icons.Default.AddAlarm, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "+10s", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = { viewModel.toggleSmartHint() },
                        enabled = !soloState.isAnswered,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("lifeline_hint_button"),
                        colors = if (soloState.smartHintVisible) ButtonDefaults.outlinedButtonColors(
                            containerColor = GrjGold90.copy(alpha = 0.5f)
                        ) else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = GrjGold50, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "Dica", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Smart Streak Notification (Encourages intelligence and focus)
            if (soloState.streak >= 2) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GrjGold90.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, GrjGold50.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("smart_streak_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = GrjGold50,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.smartStreakBanner(soloState.streak, currentLanguage),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF6B4400)
                        )
                    }
                }
            }

            // Timer Bar
            QuizTimerBar(
                timeRemaining = soloState.timeRemaining,
                totalTime = soloState.totalTimerSeconds
            )

            // Smart Hint Card (Revealed when player taps Dica)
            AnimatedVisibility(visible = soloState.smartHintVisible) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("smart_hint_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GrjGold90.copy(alpha = 0.55f)),
                    border = BorderStroke(1.dp, GrjGold50)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = GrjGold50,
                            modifier = Modifier.size(22.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.smartHintTitle(currentLanguage),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF6B4400)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.getHintText(currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = question.getCategoryTitle(currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = question.getQuestionText(currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Options List
            options.forEachIndexed { index, optionText ->
                val letter = letters.getOrElse(index) { "${index + 1}" }
                val isSelected = soloState.selectedOption == index
                val isEliminated = soloState.eliminatedOptions.contains(index)

                val isCorrectAnswer: Boolean? = if (soloState.isAnswered) {
                    index == question.correctIndex
                } else null

                QuizOptionCard(
                    optionLetter = letter,
                    optionText = optionText,
                    isSelected = isSelected,
                    isCorrect = isCorrectAnswer,
                    isEliminated = isEliminated,
                    enabled = !soloState.isAnswered,
                    onClick = { viewModel.submitSoloAnswer(index) }
                )
            }

            // Explanation & Result Card (Animated in when answered)
            AnimatedVisibility(
                visible = soloState.isAnswered,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                val wasCorrect = soloState.selectedOption == question.correctIndex
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explanation_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (wasCorrect) GrjCorrect.copy(alpha = 0.1f) else GrjWrong.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (wasCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (wasCorrect) GrjCorrect else GrjWrong,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (wasCorrect) Strings.correctExclamation(currentLanguage) else if (soloState.selectedOption == -1) Strings.timeOutExclamation(currentLanguage) else Strings.incorrectExclamation(currentLanguage),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (wasCorrect) GrjCorrect else GrjWrong
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Strings.explanationLabel(currentLanguage),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.getExplanationText(currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

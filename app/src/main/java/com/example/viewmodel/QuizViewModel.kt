package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuestionRepository
import com.example.data.UserPreferences
import com.example.model.DailyWisdom
import com.example.model.DuelRoundRecap
import com.example.model.Language
import com.example.model.QuestionReview
import com.example.model.QuizCategory
import com.example.model.QuizQuestion
import com.example.model.Screen
import com.example.model.SoloQuizSummary
import com.example.util.SoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuestionRepository(application)
    private val preferences = UserPreferences(application)

    // User profile state
    private val _isRegistered = MutableStateFlow(preferences.isRegistered)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    private val _userName = MutableStateFlow(preferences.userName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userLocation = MutableStateFlow(preferences.userLocation)
    val userLocation: StateFlow<String> = _userLocation.asStateFlow()

    // Navigation state
    private val _currentScreen = MutableStateFlow(if (preferences.isRegistered) Screen.HOME else Screen.REGISTRATION)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen back stack
    private val backStack = mutableListOf<Screen>()

    // Current language
    private val _currentLanguage = MutableStateFlow(preferences.language)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    // User stats
    private val _bestScore = MutableStateFlow(preferences.bestScore)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()

    private val _quizzesCompleted = MutableStateFlow(preferences.quizzesCompleted)
    val quizzesCompleted: StateFlow<Int> = _quizzesCompleted.asStateFlow()

    private val _duelWinsP1 = MutableStateFlow(preferences.duelWinsP1)
    val duelWinsP1: StateFlow<Int> = _duelWinsP1.asStateFlow()

    private val _duelWinsP2 = MutableStateFlow(preferences.duelWinsP2)
    val duelWinsP2: StateFlow<Int> = _duelWinsP2.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(preferences.hapticFeedbackEnabled)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(preferences.soundEnabled)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _churchSoundsThemeEnabled = MutableStateFlow(preferences.churchSoundsThemeEnabled)
    val churchSoundsThemeEnabled: StateFlow<Boolean> = _churchSoundsThemeEnabled.asStateFlow()

    val hymns = repository.getChurchHymns()
    val achievements = repository.getAchievements()

    private val _unlockedAchievements = MutableStateFlow(preferences.getUnlockedAchievements())
    val unlockedAchievements: StateFlow<Set<String>> = _unlockedAchievements.asStateFlow()

    // Daily Wisdom
    private val dailyWisdomItems = repository.getDailyWisdomList()
    private val _currentWisdomIndex = MutableStateFlow(0)
    val currentWisdomIndex: StateFlow<Int> = _currentWisdomIndex.asStateFlow()

    val currentWisdom: StateFlow<DailyWisdom> = MutableStateFlow(
        dailyWisdomItems.firstOrNull() ?: DailyWisdom(
            title = mapOf("pt" to "Sabedoria"),
            teaching = mapOf("pt" to "A fé em Jeová traz a paz."),
            reference = "GRJ"
        )
    )

    // Categories
    private val _categories = MutableStateFlow<List<QuizCategory>>(emptyList())
    val categories: StateFlow<List<QuizCategory>> = _categories.asStateFlow()

    // Solo Config
    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedQuestionCount = MutableStateFlow(10)
    val selectedQuestionCount: StateFlow<Int> = _selectedQuestionCount.asStateFlow()

    private val _selectedTimerSeconds = MutableStateFlow(preferences.defaultTimerSeconds)
    val selectedTimerSeconds: StateFlow<Int> = _selectedTimerSeconds.asStateFlow()

    // Solo Active Quiz State
    data class SoloState(
        val questions: List<QuizQuestion> = emptyList(),
        val currentIndex: Int = 0,
        val score: Int = 0,
        val streak: Int = 0,
        val bestStreak: Int = 0,
        val timeRemaining: Int = 20,
        val totalTimerSeconds: Int = 20,
        val isAnswered: Boolean = false,
        val selectedOption: Int = -1,
        val eliminatedOptions: Set<Int> = emptySet(),
        val lifeline5050Used: Boolean = false,
        val lifelineTimeUsed: Boolean = false,
        val smartHintVisible: Boolean = false,
        val smartHintUsed: Boolean = false,
        val reviews: List<QuestionReview> = emptyList(),
        val totalTimeSpentSeconds: Int = 0,
        val summary: SoloQuizSummary? = null
    ) {
        val currentQuestion: QuizQuestion?
            get() = if (currentIndex in questions.indices) questions[currentIndex] else null
    }

    private val _soloState = MutableStateFlow(SoloState())
    val soloState: StateFlow<SoloState> = _soloState.asStateFlow()

    private var soloTimerJob: Job? = null

    // Duel Config & Active State
    data class DuelState(
        val totalRounds: Int = 7,
        val invertP1: Boolean = true,
        val p1Name: String = "Jogador 1",
        val p2Name: String = "Jogador 2",
        val questions: List<QuizQuestion> = emptyList(),
        val currentRoundIndex: Int = 0,
        val p1Score: Int = 0,
        val p2Score: Int = 0,
        val timeRemaining: Int = 15,
        val totalTimerSeconds: Int = 15,
        val p1Answer: Int? = null,
        val p2Answer: Int? = null,
        val p1Correct: Boolean? = null,
        val p2Correct: Boolean? = null,
        val roundResolved: Boolean = false,
        val roundRecap: DuelRoundRecap? = null,
        val duelWinner: Int? = null // 1 for P1, 2 for P2, 0 for draw
    ) {
        val currentQuestion: QuizQuestion?
            get() = if (currentRoundIndex in questions.indices) questions[currentRoundIndex] else null
    }

    private val _duelState = MutableStateFlow(DuelState())
    val duelState: StateFlow<DuelState> = _duelState.asStateFlow()

    private var duelTimerJob: Job? = null

    init {
        _categories.value = repository.getAllCategories()
        if (dailyWisdomItems.isNotEmpty()) {
            (currentWisdom as MutableStateFlow).value = dailyWisdomItems[0]
        }
    }

    fun playSound(type: SoundManager.SoundType) {
        SoundManager.play(type, _soundEnabled.value)
    }

    fun toggleSound() {
        val newVal = !_soundEnabled.value
        _soundEnabled.value = newVal
        preferences.soundEnabled = newVal
        if (newVal) {
            playSound(SoundManager.SoundType.TAP)
        }
    }

    fun toggleChurchSoundsTheme() {
        val newVal = !_churchSoundsThemeEnabled.value
        _churchSoundsThemeEnabled.value = newVal
        preferences.churchSoundsThemeEnabled = newVal
        if (newVal) {
            playSound(SoundManager.SoundType.CHURCH_BELL)
        } else {
            playSound(SoundManager.SoundType.TAP)
        }
    }

    fun unlockAchievement(id: String) {
        if (!_unlockedAchievements.value.contains(id)) {
            preferences.unlockAchievement(id)
            _unlockedAchievements.value = preferences.getUnlockedAchievements()
        }
    }

    fun playHymnSound(soundType: SoundManager.SoundType) {
        playSound(soundType)
        unlockAchievement("church_sounds")
    }

    fun nextDailyWisdom() {
        if (dailyWisdomItems.isEmpty()) return
        val nextIdx = (_currentWisdomIndex.value + 1) % dailyWisdomItems.size
        _currentWisdomIndex.value = nextIdx
        (currentWisdom as MutableStateFlow).value = dailyWisdomItems[nextIdx]
        playSound(SoundManager.SoundType.TAP)
    }

    // Vibrator helper
    private fun vibrate(durationMs: Long = 50) {
        if (!_hapticEnabled.value) return
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val ctx = getApplication<Application>()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator?.vibrate(
                        VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            } catch (_: Exception) {
                // Ignore
            }
        }
    }

    fun registerUser(name: String, location: String) {
        val trimmedName = name.trim()
        val trimmedLocation = location.trim()
        preferences.registerUser(trimmedName, trimmedLocation)
        _userName.value = trimmedName
        _userLocation.value = trimmedLocation
        _isRegistered.value = true
        backStack.clear()
        _currentScreen.value = Screen.HOME
        playSound(SoundManager.SoundType.START)
    }

    fun updateProfile(name: String, location: String) {
        val trimmedName = name.trim()
        val trimmedLocation = location.trim()
        preferences.registerUser(trimmedName, trimmedLocation)
        _userName.value = trimmedName
        _userLocation.value = trimmedLocation
        playSound(SoundManager.SoundType.TAP)
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
            playSound(SoundManager.SoundType.TAP)
        }
    }

    fun handleBack(): Boolean {
        if (backStack.isNotEmpty()) {
            val previous = backStack.removeAt(backStack.lastIndex)
            _currentScreen.value = previous
            playSound(SoundManager.SoundType.TAP)
            return true
        }
        return false
    }

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
        preferences.language = lang
        playSound(SoundManager.SoundType.TAP)
    }

    fun toggleHaptic() {
        val newVal = !_hapticEnabled.value
        _hapticEnabled.value = newVal
        preferences.hapticFeedbackEnabled = newVal
        if (newVal) vibrate(30)
    }

    fun setSelectedCategory(catId: String) {
        _selectedCategory.value = catId
        playSound(SoundManager.SoundType.TAP)
    }

    fun setSelectedQuestionCount(count: Int) {
        _selectedQuestionCount.value = count
        playSound(SoundManager.SoundType.TAP)
    }

    fun setSelectedTimerSeconds(seconds: Int) {
        _selectedTimerSeconds.value = seconds
        preferences.defaultTimerSeconds = seconds
        playSound(SoundManager.SoundType.TAP)
    }

    // ==========================================
    // SOLO QUIZ LOGIC
    // ==========================================

    fun startSoloQuiz() {
        val questions = repository.getQuestions(
            categoryId = _selectedCategory.value,
            count = _selectedQuestionCount.value,
            shuffle = true
        )
        if (questions.isEmpty()) return

        val timerSec = _selectedTimerSeconds.value
        _soloState.value = SoloState(
            questions = questions,
            currentIndex = 0,
            score = 0,
            streak = 0,
            bestStreak = 0,
            timeRemaining = timerSec,
            totalTimerSeconds = timerSec,
            isAnswered = false,
            selectedOption = -1,
            eliminatedOptions = emptySet(),
            lifeline5050Used = false,
            lifelineTimeUsed = false,
            smartHintVisible = false,
            smartHintUsed = false,
            reviews = emptyList(),
            totalTimeSpentSeconds = 0,
            summary = null
        )

        if (_churchSoundsThemeEnabled.value) {
            playSound(SoundManager.SoundType.CHURCH_BELL)
        } else {
            playSound(SoundManager.SoundType.START)
        }
        navigateTo(Screen.SOLO_QUIZ)
        startSoloTimer()
    }

    private fun startSoloTimer() {
        soloTimerJob?.cancel()
        val timerLimit = _soloState.value.totalTimerSeconds
        if (timerLimit <= 0) return // No timer

        soloTimerJob = viewModelScope.launch {
            while (_soloState.value.timeRemaining > 0 && !_soloState.value.isAnswered) {
                delay(1000L)
                _soloState.update { current ->
                    val newTime = (current.timeRemaining - 1).coerceAtLeast(0)
                    current.copy(
                        timeRemaining = newTime,
                        totalTimeSpentSeconds = current.totalTimeSpentSeconds + 1
                    )
                }
                // Play tension ticking during final 5 seconds
                if (_soloState.value.timeRemaining in 1..5 && !_soloState.value.isAnswered) {
                    playSound(SoundManager.SoundType.TICK)
                }
            }
            if (_soloState.value.timeRemaining == 0 && !_soloState.value.isAnswered) {
                submitSoloAnswer(optionIndex = -1)
            }
        }
    }

    fun submitSoloAnswer(optionIndex: Int) {
        if (_soloState.value.isAnswered) return
        soloTimerJob?.cancel()

        val q = _soloState.value.currentQuestion ?: return
        val isCorrect = optionIndex == q.correctIndex

        if (isCorrect) {
            vibrate(40)
            val nextStreak = _soloState.value.streak + 1
            if (nextStreak >= 3) {
                if (_churchSoundsThemeEnabled.value) {
                    playSound(SoundManager.SoundType.CHURCH_CLAPPING)
                } else {
                    playSound(SoundManager.SoundType.STREAK)
                }
                unlockAchievement("streak_3")
                if (nextStreak >= 5) unlockAchievement("streak_5")
            } else {
                playSound(SoundManager.SoundType.CORRECT)
            }
        } else {
            vibrate(120)
            playSound(SoundManager.SoundType.WRONG)
        }

        val basePoints = 100
        val timeBonus = (_soloState.value.timeRemaining * 10).coerceAtLeast(0)
        val currentStreak = _soloState.value.streak
        val streakMultiplier = 1 + (currentStreak * 0.2f)
        val earnedScore = if (isCorrect) ((basePoints + timeBonus) * streakMultiplier).toInt() else 0

        val newStreak = if (isCorrect) currentStreak + 1 else 0
        val newBestStreak = maxOf(_soloState.value.bestStreak, newStreak)

        val review = QuestionReview(
            question = q,
            selectedIndex = optionIndex,
            isCorrect = isCorrect,
            timeSpentSeconds = _soloState.value.totalTimerSeconds - _soloState.value.timeRemaining
        )

        _soloState.update { current ->
            current.copy(
                isAnswered = true,
                selectedOption = optionIndex,
                score = current.score + earnedScore,
                streak = newStreak,
                bestStreak = newBestStreak,
                reviews = current.reviews + review
            )
        }
    }

    fun use5050Lifeline() {
        val current = _soloState.value
        if (current.lifeline5050Used || current.isAnswered) return
        val q = current.currentQuestion ?: return

        val wrongIndices = (0..3).filter { it != q.correctIndex }
        val toEliminate = wrongIndices.shuffled().take(2).toSet()

        _soloState.update {
            it.copy(
                lifeline5050Used = true,
                eliminatedOptions = toEliminate
            )
        }
        vibrate(30)
        playSound(SoundManager.SoundType.LIFELINE)
    }

    fun useExtraTimeLifeline() {
        val current = _soloState.value
        if (current.lifelineTimeUsed || current.isAnswered) return

        _soloState.update {
            it.copy(
                lifelineTimeUsed = true,
                timeRemaining = it.timeRemaining + 10
            )
        }
        vibrate(30)
        playSound(SoundManager.SoundType.LIFELINE)
    }

    fun toggleSmartHint() {
        val current = _soloState.value
        val newVisibility = !current.smartHintVisible
        _soloState.update {
            it.copy(
                smartHintVisible = newVisibility,
                smartHintUsed = true
            )
        }
        if (newVisibility) {
            playSound(SoundManager.SoundType.HINT)
            vibrate(25)
        } else {
            playSound(SoundManager.SoundType.TAP)
        }
    }

    fun nextSoloQuestion() {
        val current = _soloState.value
        if (current.currentIndex + 1 < current.questions.size) {
            _soloState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    isAnswered = false,
                    selectedOption = -1,
                    eliminatedOptions = emptySet(),
                    smartHintVisible = false,
                    timeRemaining = it.totalTimerSeconds
                )
            }
            playSound(SoundManager.SoundType.TAP)
            startSoloTimer()
        } else {
            finishSoloQuiz()
        }
    }

    private fun finishSoloQuiz() {
        soloTimerJob?.cancel()
        val current = _soloState.value
        val correctCount = current.reviews.count { it.isCorrect }

        val categoryObj = _categories.value.find { it.id == _selectedCategory.value }
        val categoryLabel = categoryObj?.getName(_currentLanguage.value) ?: "Guta raJehovah"

        val summary = SoloQuizSummary(
            totalQuestions = current.questions.size,
            correctAnswers = correctCount,
            score = current.score,
            highestStreak = current.bestStreak,
            timeTakenSeconds = current.totalTimeSpentSeconds,
            categoryName = categoryLabel,
            reviews = current.reviews
        )

        // Update preferences
        preferences.bestScore = current.score
        preferences.incrementCompletedQuizzes()
        _bestScore.value = preferences.bestScore
        _quizzesCompleted.value = preferences.quizzesCompleted

        // Check Achievements
        unlockAchievement("first_quiz")
        if (summary.highestStreak >= 3) unlockAchievement("streak_3")
        if (summary.highestStreak >= 5) unlockAchievement("streak_5")
        if (summary.accuracyPercent == 100) unlockAchievement("perfect_score")

        _soloState.update { it.copy(summary = summary) }
        if (_churchSoundsThemeEnabled.value) {
            playSound(SoundManager.SoundType.CHURCH_CHOIR)
        } else {
            playSound(SoundManager.SoundType.VICTORY)
        }
        navigateTo(Screen.SOLO_RESULT)
    }

    // ==========================================
    // 2-PLAYER DUEL LOGIC
    // ==========================================

    fun startDuel(
        rounds: Int = 7,
        invertP1: Boolean = true,
        p1Name: String? = null,
        p2Name: String? = null
    ) {
        val lang = _currentLanguage.value
        val defaultP1 = if (_userName.value.isNotBlank()) _userName.value else when (lang) {
            Language.PORTUGUESE -> "Jogador 1"
            Language.ENGLISH -> "Player 1"
            Language.SHONA -> "Mutambi 1"
        }
        val name1 = p1Name?.ifBlank { null } ?: defaultP1
        val name2 = p2Name?.ifBlank { null } ?: when (lang) {
            Language.PORTUGUESE -> "Jogador 2"
            Language.ENGLISH -> "Player 2"
            Language.SHONA -> "Mutambi 2"
        }

        val questions = repository.getQuestions(categoryId = "all", count = rounds, shuffle = true)
        val timerLimit = 15

        _duelState.value = DuelState(
            totalRounds = rounds,
            invertP1 = invertP1,
            p1Name = name1,
            p2Name = name2,
            questions = questions,
            currentRoundIndex = 0,
            p1Score = 0,
            p2Score = 0,
            timeRemaining = timerLimit,
            totalTimerSeconds = timerLimit,
            p1Answer = null,
            p2Answer = null,
            p1Correct = null,
            p2Correct = null,
            roundResolved = false,
            roundRecap = null,
            duelWinner = null
        )

        if (_churchSoundsThemeEnabled.value) {
            playSound(SoundManager.SoundType.CHURCH_BELL)
        } else {
            playSound(SoundManager.SoundType.START)
        }
        navigateTo(Screen.DUEL_QUIZ)
        startDuelTimer()
    }

    private fun startDuelTimer() {
        duelTimerJob?.cancel()
        duelTimerJob = viewModelScope.launch {
            while (_duelState.value.timeRemaining > 0 && !_duelState.value.roundResolved) {
                delay(1000L)
                _duelState.update { it.copy(timeRemaining = (it.timeRemaining - 1).coerceAtLeast(0)) }
                if (_duelState.value.timeRemaining in 1..4 && !_duelState.value.roundResolved) {
                    playSound(SoundManager.SoundType.TICK)
                }
            }
            if (_duelState.value.timeRemaining == 0 && !_duelState.value.roundResolved) {
                resolveDuelRound(timedOut = true)
            }
        }
    }

    fun submitDuelAnswer(player: Int, optionIndex: Int) {
        val current = _duelState.value
        if (current.roundResolved) return
        val q = current.currentQuestion ?: return

        val isCorrect = optionIndex == q.correctIndex

        if (player == 1 && current.p1Answer == null) {
            _duelState.update {
                it.copy(p1Answer = optionIndex, p1Correct = isCorrect)
            }
            vibrate(40)
            if (isCorrect) playSound(SoundManager.SoundType.CORRECT) else playSound(SoundManager.SoundType.WRONG)
        } else if (player == 2 && current.p2Answer == null) {
            _duelState.update {
                it.copy(p2Answer = optionIndex, p2Correct = isCorrect)
            }
            vibrate(40)
            if (isCorrect) playSound(SoundManager.SoundType.CORRECT) else playSound(SoundManager.SoundType.WRONG)
        }

        val stateNow = _duelState.value
        if (stateNow.p1Answer != null && stateNow.p2Answer != null) {
            resolveDuelRound(timedOut = false)
        } else if (isCorrect) {
            resolveDuelRound(timedOut = false)
        }
    }

    private fun resolveDuelRound(timedOut: Boolean) {
        duelTimerJob?.cancel()
        val current = _duelState.value
        val q = current.currentQuestion ?: return

        val p1Correct = current.p1Answer != null && current.p1Answer == q.correctIndex
        val p2Correct = current.p2Answer != null && current.p2Answer == q.correctIndex

        var p1Delta = 0
        var p2Delta = 0
        var roundWinner: Int? = null

        val speedBonus = current.timeRemaining * 2

        if (p1Correct && p2Correct) {
            p1Delta = 100 + speedBonus
            p2Delta = 100 + speedBonus
            roundWinner = null
        } else if (p1Correct) {
            p1Delta = 150 + speedBonus
            roundWinner = 1
        } else if (p2Correct) {
            p2Delta = 150 + speedBonus
            roundWinner = 2
        } else {
            if (current.p1Answer != null && !p1Correct) p1Delta = -20
            if (current.p2Answer != null && !p2Correct) p2Delta = -20
            roundWinner = null
        }

        val newP1Score = (current.p1Score + p1Delta).coerceAtLeast(0)
        val newP2Score = (current.p2Score + p2Delta).coerceAtLeast(0)

        val recap = DuelRoundRecap(
            roundNumber = current.currentRoundIndex + 1,
            winnerPlayer = roundWinner,
            p1AnsweredCorrect = if (current.p1Answer != null) p1Correct else false,
            p2AnsweredCorrect = if (current.p2Answer != null) p2Correct else false,
            p1PointsGained = p1Delta,
            p2PointsGained = p2Delta
        )

        _duelState.update {
            it.copy(
                p1Score = newP1Score,
                p2Score = newP2Score,
                roundResolved = true,
                roundRecap = recap
            )
        }
    }

    fun advanceDuelRound() {
        val current = _duelState.value
        val nextIndex = current.currentRoundIndex + 1

        if (nextIndex < current.questions.size && nextIndex < current.totalRounds) {
            _duelState.update {
                it.copy(
                    currentRoundIndex = nextIndex,
                    timeRemaining = it.totalTimerSeconds,
                    p1Answer = null,
                    p2Answer = null,
                    p1Correct = null,
                    p2Correct = null,
                    roundResolved = false,
                    roundRecap = null
                )
            }
            playSound(SoundManager.SoundType.TAP)
            startDuelTimer()
        } else {
            finishDuel()
        }
    }

    private fun finishDuel() {
        duelTimerJob?.cancel()
        val current = _duelState.value
        val winner = when {
            current.p1Score > current.p2Score -> 1
            current.p2Score > current.p1Score -> 2
            else -> 0
        }

        if (winner != 0) {
            preferences.recordDuelWin(winner)
            _duelWinsP1.value = preferences.duelWinsP1
            _duelWinsP2.value = preferences.duelWinsP2
            unlockAchievement("duel_master")
        }

        _duelState.update { it.copy(duelWinner = winner) }
        if (_churchSoundsThemeEnabled.value) {
            playSound(SoundManager.SoundType.CHURCH_BLESSING)
        } else {
            playSound(SoundManager.SoundType.VICTORY)
        }
        navigateTo(Screen.DUEL_RESULT)
    }

    override fun onCleared() {
        super.onCleared()
        soloTimerJob?.cancel()
        duelTimerJob?.cancel()
    }
}

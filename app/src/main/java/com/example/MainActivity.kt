package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Screen
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.DuelConfigScreen
import com.example.ui.screens.DuelQuizScreen
import com.example.ui.screens.DuelResultScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HymnsAndSoundsScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.SoloConfigScreen
import com.example.ui.screens.SoloQuizScreen
import com.example.ui.screens.SoloResultScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppNav()
                }
            }
        }
    }
}

@Composable
fun MainAppNav(
    viewModel: QuizViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = currentScreen != Screen.HOME && currentScreen != Screen.REGISTRATION) {
        if (!viewModel.handleBack()) {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(120)) togetherWith fadeOut(animationSpec = tween(120))
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            Screen.REGISTRATION -> RegistrationScreen(viewModel = viewModel)
            Screen.HOME -> HomeScreen(viewModel = viewModel)
            Screen.SOLO_CONFIG -> SoloConfigScreen(viewModel = viewModel)
            Screen.SOLO_QUIZ -> SoloQuizScreen(viewModel = viewModel)
            Screen.SOLO_RESULT -> SoloResultScreen(viewModel = viewModel)
            Screen.DUEL_CONFIG -> DuelConfigScreen(viewModel = viewModel)
            Screen.DUEL_QUIZ -> DuelQuizScreen(viewModel = viewModel)
            Screen.DUEL_RESULT -> DuelResultScreen(viewModel = viewModel)
            Screen.HYMNS_AND_SOUNDS -> HymnsAndSoundsScreen(viewModel = viewModel)
            Screen.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
        }
    }
}

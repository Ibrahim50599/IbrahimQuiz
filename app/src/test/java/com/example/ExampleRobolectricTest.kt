package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GRJ Quiz", appName)
  }

  @Test
  fun `user registration persists correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = com.example.data.UserPreferences(context)
    prefs.registerUser("Ibrahim Cassamo", "Maputo, Moçambique")
    assertEquals(true, prefs.isRegistered)
    assertEquals("Ibrahim Cassamo", prefs.userName)
    assertEquals("Maputo, Moçambique", prefs.userLocation)
  }

  @Test
  fun `sound preference toggle persists`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = com.example.data.UserPreferences(context)
    prefs.soundEnabled = true
    assertEquals(true, prefs.soundEnabled)
    prefs.soundEnabled = false
    assertEquals(false, prefs.soundEnabled)
  }

  @Test
  fun `questions repository loads and shuffles answer options`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.QuestionRepository(context)
    val questions = repository.getQuestions(count = 10, shuffle = true, shuffleOptions = true)
    org.junit.Assert.assertTrue("Questions should not be empty", questions.isNotEmpty())

    // Verify correct answers are varied across the questions, not always option 0 (A)
    val indices = questions.map { it.correctIndex }.toSet()
    org.junit.Assert.assertTrue("Correct answer indices should vary beyond index 0", indices.size > 1)

    // Verify hint text is present and meaningful
    val sampleQ = questions.first()
    val hintPt = sampleQ.getHintText(com.example.model.Language.PORTUGUESE)
    org.junit.Assert.assertTrue("Hint should not be blank", hintPt.isNotBlank())
  }

  @Test
  fun `smart wisdom diagnosis produces insightful assessment`() {
    val q = com.example.model.QuizQuestion(
        id = "test_q",
        category = "history",
        categoryName = mapOf("pt" to "História"),
        question = mapOf("pt" to "Pergunta teste?"),
        options = mapOf("pt" to listOf("Op1", "Op2", "Op3", "Op4")),
        correctIndex = 1,
        explanation = mapOf("pt" to "Explicação teste")
    )
    val reviews = listOf(
        com.example.model.QuestionReview(q, selectedIndex = 1, isCorrect = true, timeSpentSeconds = 5),
        com.example.model.QuestionReview(q, selectedIndex = 0, isCorrect = false, timeSpentSeconds = 8)
    )
    val summary = com.example.model.SoloQuizSummary(
        totalQuestions = 2,
        correctAnswers = 1,
        score = 150,
        highestStreak = 1,
        timeTakenSeconds = 13,
        categoryName = "História",
        reviews = reviews
    )
    val diagnosis = summary.getSmartDiagnosis(com.example.model.Language.PORTUGUESE)
    org.junit.Assert.assertTrue("Diagnosis should provide meaningful insight", diagnosis.isNotBlank())

    val breakdown = summary.getCategoryBreakdown(com.example.model.Language.PORTUGUESE)
    assertEquals(1, breakdown.size)
    assertEquals(50, breakdown[0].accuracyPercent)
  }
}

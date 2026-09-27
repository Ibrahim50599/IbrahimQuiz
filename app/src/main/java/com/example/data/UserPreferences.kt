package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Language

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("grj_quiz_prefs", Context.MODE_PRIVATE)

    var language: Language
        get() {
            val code = prefs.getString("pref_language", Language.PORTUGUESE.code) ?: Language.PORTUGUESE.code
            return Language.fromCode(code)
        }
        set(value) {
            prefs.edit().putString("pref_language", value.code).apply()
        }

    var bestScore: Int
        get() = prefs.getInt("pref_best_score", 0)
        set(value) {
            if (value > bestScore) {
                prefs.edit().putInt("pref_best_score", value).apply()
            }
        }

    var quizzesCompleted: Int
        get() = prefs.getInt("pref_quizzes_completed", 0)
        set(value) {
            prefs.edit().putInt("pref_quizzes_completed", value).apply()
        }

    var duelWinsP1: Int
        get() = prefs.getInt("pref_duel_wins_p1", 0)
        set(value) {
            prefs.edit().putInt("pref_duel_wins_p1", value).apply()
        }

    var duelWinsP2: Int
        get() = prefs.getInt("pref_duel_wins_p2", 0)
        set(value) {
            prefs.edit().putInt("pref_duel_wins_p2", value).apply()
        }

    var hapticFeedbackEnabled: Boolean
        get() = prefs.getBoolean("pref_haptic_feedback", true)
        set(value) {
            prefs.edit().putBoolean("pref_haptic_feedback", value).apply()
        }

    var soundEnabled: Boolean
        get() = prefs.getBoolean("pref_sound_enabled", true)
        set(value) {
            prefs.edit().putBoolean("pref_sound_enabled", value).apply()
        }

    var churchSoundsThemeEnabled: Boolean
        get() = prefs.getBoolean("pref_church_sounds_theme", true)
        set(value) {
            prefs.edit().putBoolean("pref_church_sounds_theme", value).apply()
        }

    var defaultTimerSeconds: Int
        get() = prefs.getInt("pref_timer_seconds", 20)
        set(value) {
            prefs.edit().putInt("pref_timer_seconds", value).apply()
        }

    var isRegistered: Boolean
        get() = prefs.getBoolean("pref_is_registered", false)
        set(value) {
            prefs.edit().putBoolean("pref_is_registered", value).apply()
        }

    var userName: String
        get() = prefs.getString("pref_user_name", "") ?: ""
        set(value) {
            prefs.edit().putString("pref_user_name", value).apply()
        }

    var userLocation: String
        get() = prefs.getString("pref_user_location", "") ?: ""
        set(value) {
            prefs.edit().putString("pref_user_location", value).apply()
        }

    fun registerUser(name: String, location: String) {
        prefs.edit()
            .putString("pref_user_name", name.trim())
            .putString("pref_user_location", location.trim())
            .putBoolean("pref_is_registered", true)
            .apply()
    }

    fun incrementCompletedQuizzes() {
        quizzesCompleted += 1
    }

    fun recordDuelWin(winner: Int) {
        if (winner == 1) duelWinsP1 += 1
        if (winner == 2) duelWinsP2 += 1
    }

    fun getUnlockedAchievements(): Set<String> {
        return prefs.getStringSet("pref_achievements", emptySet()) ?: emptySet()
    }

    fun unlockAchievement(id: String) {
        val current = getUnlockedAchievements().toMutableSet()
        current.add(id)
        prefs.edit().putStringSet("pref_achievements", current).apply()
    }
}

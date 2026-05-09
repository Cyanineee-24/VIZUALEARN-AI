package com.myApp.vizualearnfinal.screens.progress

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import com.myApp.vizualearnfinal.utils.DeckProgressManager

class ProgressModel(
    private val context: Context,
    private val repository: StudySetRepository
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vizualearn_progress", Context.MODE_PRIVATE)

    fun getCurrentStreak(): Int = prefs.getInt("CURRENT_STREAK", 0)
    fun getBestStreak(): Int = prefs.getInt("BEST_STREAK", 0)
    fun getTotalDays(): Int = prefs.getInt("TOTAL_DAYS", 0)
    fun getMonthDays(): Int = prefs.getInt("MONTH_DAYS", 0)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMissedDays(): Int {
        val firstLogin = prefs.getLong("FIRST_LOGIN_DATE", 0L)
        if (firstLogin == 0L) return 0 // Brand new account, 0 missed days!

        val todayEpoch = LocalDate.now().toEpochDay()
        val expectedDays = (todayEpoch - firstLogin + 1).toInt()
        val missed = expectedDays - getTotalDays()
        return if (missed < 0) 0 else missed
    }

    // Streak logic
    @RequiresApi(Build.VERSION_CODES.O)
    fun recordDailyLogin() {
        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()
        val currentMonth = today.monthValue

        val lastLoginEpoch = prefs.getLong("LAST_LOGIN_DATE", 0L)
        val savedMonth = prefs.getInt("LAST_LOGIN_MONTH", currentMonth)

        if (todayEpoch == lastLoginEpoch) return // Already got streak today

        val editor = prefs.edit()

        // Save their very first day for missed days calculation
        if (!prefs.contains("FIRST_LOGIN_DATE")) {
            editor.putLong("FIRST_LOGIN_DATE", todayEpoch)
        }

        var currentStreak = getCurrentStreak()
        var totalDays = getTotalDays()
        val bestStreak = getBestStreak()
        var monthDays = prefs.getInt("MONTH_DAYS", 0)

        // Month Logic
        if (currentMonth != savedMonth) {
            monthDays = 1 // Reset for new month
        } else {
            monthDays += 1
        }

        // Streak & Total Days Logic
        if (lastLoginEpoch == 0L) {
            currentStreak = 1
            totalDays = 1
            monthDays = 1
        } else if (todayEpoch - lastLoginEpoch == 1L) {
            currentStreak += 1
            totalDays += 1
        } else {
            currentStreak = 1
            totalDays += 1
        }

        // Save everything
        editor.putInt("CURRENT_STREAK", currentStreak)
        editor.putInt("TOTAL_DAYS", totalDays)
        editor.putInt("MONTH_DAYS", monthDays)
        editor.putInt("LAST_LOGIN_MONTH", currentMonth)
        editor.putLong("LAST_LOGIN_DATE", todayEpoch)

        if (currentStreak > bestStreak) {
            editor.putInt("BEST_STREAK", currentStreak)
        }

        editor.apply()
    }

    data class SubjectMastery(
        val subjectName: String,
        val iconResName: String,
        val learned: Int,
        val total: Int
    ) {
        val percent get() = if (total > 0) (learned * 100) / total else 0
    }

    suspend fun getMasteryData(): Pair<Int, List<SubjectMastery>> {
        val sets = repository.getAllSets()
        var totalLearnedAcrossApp = 0
        var totalCardsAcrossApp = 0
        val subjectList = mutableListOf<SubjectMastery>()

        for (set in sets) {
            val decks = repository.getDecksForSet(set.id)
            var setLearned = 0
            var setTotal = 0

            for (deck in decks) {
                val cards = repository.getFlashcardsForDeck(deck.id)
                val learned = DeckProgressManager.getLearnedIds(context, deck.id).size
                setLearned += learned
                setTotal += cards.size
            }

            totalLearnedAcrossApp += setLearned
            totalCardsAcrossApp += setTotal

            if (setTotal > 0) {
                subjectList.add(SubjectMastery(set.subject, set.iconResName, setLearned, setTotal))
            }
        }

        val overallPct = if (totalCardsAcrossApp > 0) (totalLearnedAcrossApp * 100) / totalCardsAcrossApp else 0
        return Pair(overallPct, subjectList)
    }
}
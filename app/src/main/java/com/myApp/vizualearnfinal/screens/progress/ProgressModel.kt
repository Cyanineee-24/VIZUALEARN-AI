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
    private val repository: StudySetRepository // <--- ADDED REPOSITORY HERE
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vizualearn_progress", Context.MODE_PRIVATE)

    fun getCurrentStreak(): Int = prefs.getInt("CURRENT_STREAK", 0)
    fun getBestStreak(): Int = prefs.getInt("BEST_STREAK", 0)
    fun getTotalDays(): Int = prefs.getInt("TOTAL_DAYS", 0)

    // THE REAL-TIME STREAK LOGIC
    @RequiresApi(Build.VERSION_CODES.O)
    fun recordDailyLogin() {
        val todayEpoch = LocalDate.now().toEpochDay()
        val lastLoginEpoch = prefs.getLong("LAST_LOGIN_DATE", 0L)

        // 1. If this is the exact same day, do nothing. They already got their streak today.
        if (todayEpoch == lastLoginEpoch) return

        val editor = prefs.edit()
        var currentStreak = getCurrentStreak()
        var totalDays = getTotalDays()
        val bestStreak = getBestStreak()

        // 2. Calculate the difference in days
        if (lastLoginEpoch == 0L) {
            // First time ever opening the app!
            currentStreak = 1
            totalDays = 1
        } else if (todayEpoch - lastLoginEpoch == 1L) {
            // They logged in exactly yesterday! Keep the streak alive.
            currentStreak += 1
            totalDays += 1
        } else {
            // They missed a day (or more). Streak is broken.
            currentStreak = 1
            totalDays += 1 // We still count it as a total day studied
        }

        // 3. Save the new values
        editor.putInt("CURRENT_STREAK", currentStreak)
        editor.putInt("TOTAL_DAYS", totalDays)
        editor.putLong("LAST_LOGIN_DATE", todayEpoch) // Update last login to today

        // 4. Update the high score if they beat it
        if (currentStreak > bestStreak) {
            editor.putInt("BEST_STREAK", currentStreak)
        }

        editor.apply()
    }

    // --- EVERYTHING BELOW MUST BE INSIDE THE CLASS ---

    // Create a data class to hold the calculated info
    data class SubjectMastery(
        val subjectName: String,
        val iconResName: String,
        val learned: Int,
        val total: Int
    ) {
        val percent get() = if (total > 0) (learned * 100) / total else 0
    }

    // Add this to calculate the data for the UI
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
package com.myApp.vizualearnfinal.screens.progress

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class ProgressModel(context: Context) {
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
            // They missed a day (or more). Streak is broken. 😭
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
}
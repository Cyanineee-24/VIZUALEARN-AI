package com.myApp.vizualearnfinal.utils

import android.content.Context

object DeckProgressManager {
    private const val PREFS_NAME = "deck_progress"

    fun getLearnedIds(context: Context, deckId: Int): MutableSet<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getStringSet("deck_$deckId", emptySet()) ?: emptySet()
        return stored.map { it.toInt() }.toMutableSet()
    }

    fun saveLearnedIds(context: Context, deckId: Int, ids: Set<Int>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet("deck_$deckId", ids.map { it.toString() }.toSet()).apply()
    }

    fun getProgressPercent(context: Context, deckId: Int, totalCards: Int): Int {
        if (totalCards == 0) return 0
        return (getLearnedIds(context, deckId).size * 100) / totalCards
    }
}
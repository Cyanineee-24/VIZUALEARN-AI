package com.myApp.vizualearnfinal.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.myApp.vizualearnfinal.R

// 1. The Type Flag
enum class ContainerType { FLASHCARD, MIND_MAP }

// 2. UPDATED: Added iconResId
data class DeckItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val progress: Int,
    val type: ContainerType,
    val iconResId: Int // <--- NEW!
)

// 3. The Adapter
class ContainerAdapter(
    private var items: List<DeckItem>,
    private val onItemClick: (Int, ContainerType) -> Unit
) : RecyclerView.Adapter<ContainerAdapter.ContainerViewHolder>() {

    class ContainerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.textDeckTitle)
        val textSubtitle: TextView = view.findViewById(R.id.textCardCount)
        val textProgress: TextView = view.findViewById(R.id.textProgressPercent)
        val progressBar: LinearProgressIndicator = view.findViewById(R.id.progressBar)
        val imageIcon: ImageView = view.findViewById(R.id.imageDeckIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deck_card, parent, false)
        return ContainerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContainerViewHolder, position: Int) {
        val item = items[position]

        // Set the universal text
        holder.textTitle.text = item.title
        holder.textSubtitle.text = item.subtitle

        // SET THE DYNAMIC ICON FROM THE PARENT SET!
        if (item.iconResId != 0) {
            holder.imageIcon.setImageResource(item.iconResId)
        }

        // Format based on type (Flashcard vs Mind Map)
        if (item.type == ContainerType.MIND_MAP) {
            // Hide the progress bar stuff for Mind Maps
            holder.textProgress.visibility = View.GONE
            holder.progressBar.visibility = View.GONE
        } else {
            // Show and set the progress bar for Flashcards
            holder.textProgress.visibility = View.VISIBLE
            holder.progressBar.visibility = View.VISIBLE
            holder.textProgress.text = "${item.progress}%"
            holder.progressBar.progress = item.progress
        }

        // Handle the click event
        holder.itemView.setOnClickListener {
            onItemClick(item.id, item.type)
        }
    }

    override fun getItemCount() = items.size

    // Helper to update data dynamically
    fun updateData(newItems: List<DeckItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
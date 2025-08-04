package com.example.nbarandomizer.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.nbarandomizer.models.Player

class PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }
}
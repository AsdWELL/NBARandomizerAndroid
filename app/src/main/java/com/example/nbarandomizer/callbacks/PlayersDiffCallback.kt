package com.example.nbarandomizer.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.nbarandomizer.models.Player

class PlayersDiffCallback (
    private val oldData: List<Player>,
    private val newData: List<Player>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].id == newData[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}
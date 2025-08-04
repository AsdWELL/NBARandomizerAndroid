package com.example.nbarandomizer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.nbarandomizer.callbacks.PlayerDiffCallback
import com.example.nbarandomizer.databinding.ItemHistoryBinding
import com.example.nbarandomizer.models.Player

class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(player: Player) {
        with(binding) {
            name.text = player.playerName

            Glide.with(binding.root)
                .load(player.photoUrl)
                .signature(ObjectKey("${player.id}_${player.team}"))
                .into(photo)
        }
    }
}

class HistoryAdapter : ListAdapter<Player, HistoryViewHolder>(PlayerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHistoryBinding.inflate(inflater, parent, false)

        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}
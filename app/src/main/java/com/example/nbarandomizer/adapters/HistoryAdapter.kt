package com.example.nbarandomizer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbarandomizer.databinding.ItemGameBinding
import com.example.nbarandomizer.models.Game

class HistoryViewHolder(private val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root) {
    private val singleGameAdapter = SingleGameAdapter()

    private val gridLayoutManager = GridLayoutManager(itemView.context, 1).apply {
        isItemPrefetchEnabled = true
        initialPrefetchItemCount = 6
    }

    init {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding.gamesRecyclerView) {
            setHasFixedSize(true)
            itemAnimator = null
            setRecycledViewPool(HistoryAdapter.sharedPool)
            layoutManager = gridLayoutManager
            adapter = singleGameAdapter
        }
    }

    fun bind(game: Game, index: Int) {
        singleGameAdapter.players = game.usedPlayers
        gridLayoutManager.spanCount = game.teamsCount

        binding.gameLabel.text = "Игра ${index + 1}"
    }
}

class HistoryAdapter : RecyclerView.Adapter<HistoryViewHolder>() {
    lateinit var games: List<Game>

    companion object {
        val sharedPool = RecyclerView.RecycledViewPool()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGameBinding.inflate(inflater, parent, false)

        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(games[position], position)
    }
}
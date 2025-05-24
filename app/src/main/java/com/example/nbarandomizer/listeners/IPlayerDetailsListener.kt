package com.example.nbarandomizer.listeners

import android.view.View
import com.example.nbarandomizer.models.Player

interface IPlayerDetailsListener {
    fun onPlayerCardLongClick(player: Player, playerCard: View)
}
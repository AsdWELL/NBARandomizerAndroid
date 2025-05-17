package com.example.nbarandomizer.listeners

import com.example.nbarandomizer.models.Player

interface IPlayerDetailsListener {
    fun onLongClick(player: Player)
}
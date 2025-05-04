package com.example.nbarandomizer

import android.app.Application
import com.example.nbarandomizer.services.PlayersService

class App : Application() {
    lateinit var playersService: PlayersService

    override fun onCreate() {
        super.onCreate()

        playersService = PlayersService(this)
    }

    override fun onTerminate() {
        playersService.close()

        super.onTerminate()
    }
}
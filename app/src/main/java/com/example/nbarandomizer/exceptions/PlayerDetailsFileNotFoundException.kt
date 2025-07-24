package com.example.nbarandomizer.exceptions

import com.example.nbarandomizer.models.Version2K

class PlayerDetailsFileNotFoundException(private val version: Version2K)
    : Exception("Не удалось получить атрибуты игроков для ${version}. Перезапустите приложение")
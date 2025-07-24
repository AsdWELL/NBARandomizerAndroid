package com.example.nbarandomizer.exceptions

import com.example.nbarandomizer.models.Version2K

class RosterFileNotFoundException(private val version: Version2K)
    : Exception("Не удалось получить ростер для ${version}. Перезапустите приложение")
package com.example.nbarandomizer.exceptions

class PlayerNotFoundException(msg: String) : Exception("Игрок с $msg не найден")
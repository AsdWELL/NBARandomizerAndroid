package com.example.nbarandomizer.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbarandomizer.models.Epoch
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.services.PlayersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    data object Idle : UiState()
    data object LoadingRoster : UiState()
    data object SuccessRoster : UiState()
    data object LoadingDetails : UiState()
    data object SuccessDetails : UiState()
    data class Error(val msg: String) : UiState()
}

class SharedViewModel : ViewModel() {
    private var downloadingJob: Job? = null

    private var downloadingDetailsJob: Job? = null

    private val _selectedRosterBinding: MutableLiveData<MutableList<Player>> = MutableLiveData(mutableListOf())
    val selectedRosterBinding: LiveData<MutableList<Player>> get() = _selectedRosterBinding

    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> get() = _uiState

    var selectedRoster
        get() = _selectedRosterBinding.value!!
        private set(value) {
            _selectedRosterBinding.value = value
        }

    var playersDetails = mutableListOf<PlayerDetails>()
        private set

    fun stopDownloadingJob() {
        downloadingJob?.cancel()
        downloadingDetailsJob?.cancel()

        downloadingJob = null
        downloadingDetailsJob = null
    }

    fun downloadRoster(playersService: PlayersService, epoch: Epoch) {
        stopDownloadingJob()

        downloadingJob = viewModelScope.launch {
            _uiState.value = UiState.LoadingRoster

            val players: MutableList<Player>

            try {
                withContext(Dispatchers.IO) {
                    players = playersService.downloadPlayersByEpoch(epoch)
                }
            }
            catch (ex: Exception) {
                _uiState.value = UiState.Error("Интернет включи сука")

                return@launch
            }

            selectedRoster = players

            _uiState.value = UiState.SuccessRoster
        }
    }

    fun getRosterAndDetails(playersService: PlayersService, epoch: Epoch) {
        stopDownloadingJob()

        if (selectedRoster.isNotEmpty() && selectedRoster[0].epoch == epoch)
            return

        downloadingJob = viewModelScope.launch {
             _uiState.value = UiState.LoadingRoster

            val players = withContext(Dispatchers.IO) {
                playersService.getPlayersByEpoch(epoch)
            }

            if (players.isEmpty()) {
                _uiState.value = UiState.Error("Интернет включи сука")

                return@launch
            }
            else {
                selectedRoster = players

                _uiState.value = UiState.SuccessRoster
            }

            downloadingDetailsJob = viewModelScope.launch {
                _uiState.value = UiState.LoadingDetails

                val details = withContext(Dispatchers.IO) {
                    playersService.getPlayersDetails(selectedRoster, epoch)
                }

                playersDetails = details

                _uiState.value = UiState.SuccessDetails
            }
        }
    }

    fun isDownloadingRoster(): Boolean {
        return downloadingJob?.isActive == true
    }

    fun isDownloadingDetails(): Boolean {
        return downloadingDetailsJob?.isActive == true
    }

    override fun onCleared() {
        super.onCleared()

        stopDownloadingJob()
    }
}
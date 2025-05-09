package com.example.nbarandomizer.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.nbarandomizer.App
import com.example.nbarandomizer.adapters.ViewPagerAdapter
import com.example.nbarandomizer.databinding.ActivityMainBinding
import com.example.nbarandomizer.models.Epoch
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.services.PlayersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val playersService: PlayersService
        get() = (applicationContext as App).playersService

    private val downloadingAnimator by lazy {
        createDownloadingAnimation()
    }

    companion object {
        val selectedRoster: MutableLiveData<MutableList<Player>> = MutableLiveData(mutableListOf())
        var playersDetails = mutableListOf<PlayerDetails>()
        var downloadingJob: Job? = null
        var downloadingDetailsJob: Job? = null
    }

    private fun initializeSpinner(textView: AutoCompleteTextView, values: List<String>) {
        val adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, values)

        textView.setAdapter(adapter)
        textView.setText(values[0], false)

        textView.setOnItemClickListener { _, _, _, _ -> getRoster() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        initializeViewPager()

        initializeSpinner(binding.epochSpinner, Epoch.entries.map { it.toString() })
        initializeSpinner(binding.versionSpinner, listOf("2K25", "2K24", "2K23", "2K22", "2K21"))

        getRoster()

        binding.refreshBtn.setOnClickListener { downloadRoster() }
    }

    private fun initializeViewPager() {
        val viewPager = binding.viewPager
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        binding.indicator.setViewPager(binding.viewPager)
    }

    private fun stopDownloadingJob() {
        if (downloadingDetailsJob != null)
            if (downloadingDetailsJob?.isActive == true) {
                downloadingDetailsJob?.cancel()
                downloadingDetailsJob = null
            }

        if (downloadingJob != null)
            if (downloadingJob?.isActive == true) {
                downloadingJob?.cancel()
                downloadingJob = null
            }
    }

    private fun toastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun badNetworkStateMessage() {
        toastMessage("Интернет включи сука")
    }

    private fun createDownloadingAnimation(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.refreshBtn, View.ROTATION, 0f, 360f).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private fun startDownloadingAnimation() {
        if (!downloadingAnimator.isRunning)
            downloadingAnimator.start()
    }

    private fun downloadRoster() {
        stopDownloadingJob()

        startDownloadingAnimation()

        val epoch = Epoch.valueOf(binding.epochSpinner.text.toString())

        downloadingJob = lifecycleScope.launch(Dispatchers.IO) {
            val players: MutableList<Player>

            try {
                players = playersService.downloadPlayersByEpoch(epoch)
            }
            catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    badNetworkStateMessage()
                }
                return@launch
            }
            finally {
                withContext(Dispatchers.Main) {
                    downloadingAnimator.end()
                }
            }

            withContext(Dispatchers.Main) {
                if (players.isNotEmpty())
                    selectedRoster.value = players

                toastMessage("Данные обновлены")
            }
        }
    }

    private fun getRoster() {
        stopDownloadingJob()

        val epoch = Epoch.valueOf(binding.epochSpinner.text.toString())

        if (selectedRoster.value!!.isNotEmpty() && selectedRoster.value!![0].epoch == epoch)
            return

        startDownloadingAnimation()

        downloadingJob = lifecycleScope.launch(Dispatchers.IO) {
            val players = playersService.getPlayersByEpoch(epoch)

            withContext(Dispatchers.Main) {
                if (players.isEmpty()) {
                    downloadingAnimator.end()
                    toastMessage("Интернет включи сука")
                }

                else
                    selectedRoster.value = players
            }

            if (players.isEmpty())
                return@launch

            downloadingDetailsJob = lifecycleScope.launch(Dispatchers.IO) {
                val details = playersService.getPlayersDetails(selectedRoster.value!!, epoch)

                withContext(Dispatchers.Main) {
                    downloadingAnimator.end()

                    playersDetails = details
                    toastMessage("Все данные загружены")
                }
            }
        }
    }

    override fun onStop() {
        stopDownloadingJob()

        super.onStop()
    }
}
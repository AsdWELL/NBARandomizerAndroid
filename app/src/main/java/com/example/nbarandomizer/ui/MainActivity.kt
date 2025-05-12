package com.example.nbarandomizer.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nbarandomizer.App
import com.example.nbarandomizer.adapters.ViewPagerAdapter
import com.example.nbarandomizer.databinding.ActivityMainBinding
import com.example.nbarandomizer.extensions.hide
import com.example.nbarandomizer.extensions.show
import com.example.nbarandomizer.models.Epoch
import com.example.nbarandomizer.services.PlayersService
import com.example.nbarandomizer.viewModels.SharedViewModel
import com.example.nbarandomizer.viewModels.UiState

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedViewModel: SharedViewModel

    private val playersService: PlayersService
        get() = (applicationContext as App).playersService

    private val downloadingAnimator by lazy {
        createDownloadingAnimation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

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

        observeViewModelStates()

        binding.refreshBtn.setOnClickListener { downloadRoster() }

        playersService.notifyProgressBar = { binding.progressBar.progress++ }
    }

    override fun onResume() {
        super.onResume()

        if (sharedViewModel.uiState.value is UiState.Idle)
            getRoster()
    }

    private fun initializeSpinner(textView: AutoCompleteTextView, values: List<String>) {
        val adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, values)

        textView.setAdapter(adapter)
        textView.setText(values[0], false)

        textView.setOnItemClickListener { _, _, _, _ -> getRoster() }
    }

    private fun initializeViewPager() {
        val viewPager = binding.viewPager
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        binding.indicator.setViewPager(binding.viewPager)
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

    private fun getSelectedEpoch(): Epoch {
        return Epoch.valueOf(binding.epochSpinner.text.toString())
    }

    private fun setStatusText(text: String) {
        with(binding.statusTextView) {
            this.text = text
            show()
        }
    }

    private fun observeViewModelStates() {
        sharedViewModel.uiState.observe(this) {
            when(it) {
                is UiState.LoadingRoster -> {
                    startDownloadingAnimation()

                    setStatusText("Загрузка ростера...")
                    binding.progressBar.hide()
                }

                is UiState.SuccessRoster -> {
                    binding.statusTextView.hide()

                    downloadingAnimator.end()
                }

                is UiState.LoadingDetails -> {
                    startDownloadingAnimation()

                    setStatusText("Загрузка атрибутов игроков...")
                    binding.progressBar.show()
                    binding.progressBar.progress = 0
                }

                is UiState.SuccessDetails -> {
                    downloadingAnimator.end()

                    binding.progressBar.hide()
                    binding.statusTextView.hide()
                }

                is UiState.Error -> {
                    setStatusText(it.msg)

                    downloadingAnimator.end()

                    binding.progressBar.hide()
                }

                else -> Unit
            }
        }
    }

    private fun downloadRoster() {
        sharedViewModel.downloadRosterAndDetails(playersService, getSelectedEpoch())
    }

    private fun getRoster() {
        sharedViewModel.getRosterAndDetails(playersService, getSelectedEpoch())
    }

    override fun onDestroy() {
        sharedViewModel.stopDownloadingJob()

        super.onDestroy()
    }
}
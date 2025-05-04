package com.example.nbarandomizer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.nbarandomizer.MainActivity
import com.example.nbarandomizer.R
import com.example.nbarandomizer.databinding.FragmentRandomizePlayerBinding
import com.example.nbarandomizer.extensions.generateTeams
import com.example.nbarandomizer.extensions.getNextPlayer
import com.example.nbarandomizer.extensions.randomize
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.models.Position
import com.google.android.material.chip.Chip

class RandomizePlayerFragment : Fragment() {
    private var _binding: FragmentRandomizePlayerBinding? = null

    private val binding get() = _binding!!

    private var playersCount: Int = 1

    private val selectedPositions: MutableList<Position> = mutableListOf()

    private var shuffledPlayers: MutableLiveData<MutableList<Player>> = MutableLiveData(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomizePlayerBinding.inflate(inflater, container, false)

        initializePositionsChips()
        setSeekBarListeners()

        binding.randomizeButton.setOnClickListener { randomize() }
        binding.resetBtn.setOnClickListener { reset() }

        MainActivity.selectedRoster.observe(viewLifecycleOwner) { reset() }
        shuffledPlayers.observe(viewLifecycleOwner) { binding.remainingPlayersTextView.text = "Осталось игроков ${it.size}" }

        return binding.root
    }

    private fun initializePositionsChips() {
        Position.entries.map { it.toString() }.forEach(::addChip)
    }

    private fun addChip(text: String) {
        val chip = layoutInflater.inflate(R.layout.chip, binding.positionsChips, false) as Chip
        chip.text = text

        chip.setOnCheckedChangeListener { _, isChecked ->
            val position = Position.valueOf(chip.text.toString())

            if (isChecked)
                 selectedPositions.add(position)
            else
                selectedPositions.remove(position)
        }

        binding.positionsChips.addView(chip)
    }

    private fun setSeekBarListeners() {
        binding.playersCountSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                playersCount = progress + 1
                binding.positionsChips.isSingleSelection = progress == 0
                binding.playersCountTextView.text = "Игроков: ${playersCount}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun toastMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun randomize() {
        if (selectedPositions.isNotEmpty() && selectedPositions.size != playersCount) {
            toastMessage("Хуйня")
            return
        }

        var players = shuffledPlayers.value!!

        if (players.isEmpty())
            players = MainActivity.selectedRoster.value!!.randomize()

        try {
            if (playersCount == 1)
                players.getNextPlayer()
            else
                players.generateTeams(playersCount)
        }
        catch (ex: Exception) {
            toastMessage(ex.message!!)
        }

        shuffledPlayers.value = players
    }

    private fun reset() {
        binding.positionsChips.clearCheck()
        binding.playersCountSeekBar.progress = 0
        shuffledPlayers.value = mutableListOf()
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
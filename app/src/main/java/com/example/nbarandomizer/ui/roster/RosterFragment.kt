package com.example.nbarandomizer.ui.roster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nbarandomizer.R
import com.example.nbarandomizer.adapters.PlayerAdapter
import com.example.nbarandomizer.databinding.FragmentRosterBinding
import com.example.nbarandomizer.extensions.applyFilterSettingsAndSort
import com.example.nbarandomizer.listeners.IPlayerDetailsListener
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.ui.playerDetails.PlayerDetailsFragment
import com.example.nbarandomizer.viewModels.SharedViewModel
import com.example.nbarandomizer.viewModels.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RosterFragment : Fragment(), IPlayerDetailsListener {
    private var _binding: FragmentRosterBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var adapter: PlayerAdapter

    private fun toastMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onPlayerCardLongClick(player: Player, playerCard: View) {
        if (sharedViewModel.isDownloadingDetails()) {
            toastMessage("Погоди ща скачается")
            return
        }

        if (sharedViewModel.playersDetails.isEmpty()) {
            toastMessage("Нихуя")
            return
        }

        val playerDetailsFragment = PlayerDetailsFragment(sharedViewModel.playersDetails[player.id], playerCard)

        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.container, playerDetailsFragment, "details")
            .addToBackStack("details")
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRosterBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        adapter = PlayerAdapter(this)

        bindRoster()

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        binding.searchButton.setOnClickListener {
            if (sharedViewModel.isDownloadingRoster()) {
                toastMessage("Погоди ща скачается")

                return@setOnClickListener
            }

            requireActivity().supportFragmentManager
               .beginTransaction()
               .add(R.id.container, FilterFragment(), "filter")
               .addToBackStack("filter")
               .commit()
        }

        return binding.root
    }

    private fun showPlayersCount(count: Int) {
        binding.playersCountTextView.text = "Показано $count из 100 игроков"
    }

    private fun setFilteredRoster() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (sharedViewModel.uiState.value is UiState.LoadingRoster)
                return@launch

            val filteredPlayers = sharedViewModel.selectedRoster.applyFilterSettingsAndSort(sharedViewModel.filterSettings)

            withContext(Dispatchers.Main) {
                adapter.submitList(filteredPlayers) { binding.recyclerView.scrollToPosition(0) }
                showPlayersCount(filteredPlayers.size)
            }
        }
    }

    private fun bindRoster() {
        sharedViewModel.selectedRosterBinding.observe(viewLifecycleOwner) {
            setFilteredRoster()
        }

        sharedViewModel.filterSettingsBinding.observe(viewLifecycleOwner) {
            setFilteredRoster()
        }
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
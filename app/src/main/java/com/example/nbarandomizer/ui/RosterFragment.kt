package com.example.nbarandomizer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nbarandomizer.adapters.IPlayerDetailsListener
import com.example.nbarandomizer.adapters.PlayerAdapter
import com.example.nbarandomizer.databinding.FragmentRosterBinding
import com.example.nbarandomizer.models.Player

class RosterFragment : Fragment() {
    private var _binding: FragmentRosterBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: PlayerAdapter

    private fun toastMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRosterBinding.inflate(inflater, container, false)

        adapter = PlayerAdapter(object : IPlayerDetailsListener {
            override fun onClick(player: Player) {
                if (MainActivity.downloadingDetailsJob?.isActive == true) {
                    toastMessage("Погоди ща скачается")
                    return
                }

                val dialog = PlayerDetailsFragment(MainActivity.playersDetails[player.id])
                dialog.show(requireActivity().supportFragmentManager, "")
            }
        })

        bindRoster()

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    private fun bindRoster() {
        MainActivity.selectedRoster.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
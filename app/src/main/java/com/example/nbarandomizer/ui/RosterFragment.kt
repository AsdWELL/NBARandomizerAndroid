package com.example.nbarandomizer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nbarandomizer.adapters.PlayerAdapter
import com.example.nbarandomizer.databinding.FragmentRosterBinding

class RosterFragment : Fragment() {
    private var _binding: FragmentRosterBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: PlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRosterBinding.inflate(inflater, container, false)

        val manager = LinearLayoutManager(context)
        adapter = PlayerAdapter()

        bindRoster()

        binding.recyclerView.layoutManager = manager
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
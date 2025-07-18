package com.example.nbarandomizer.ui.randomizer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nbarandomizer.R
import com.example.nbarandomizer.adapters.HistoryAdapter
import com.example.nbarandomizer.databinding.FragmentHistoryBinding
import com.example.nbarandomizer.extensions.createEnterTransformation
import com.example.nbarandomizer.extensions.createReturnTransformation
import com.example.nbarandomizer.extensions.hide
import com.example.nbarandomizer.extensions.show
import com.example.nbarandomizer.models.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment(private val usedPlayers: MutableList<Player>) : Fragment() {
    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: HistoryAdapter

    private fun initAndAttachHistoryAdapter() {
        if (usedPlayers.isEmpty()) {
            binding.emptyHistoryMsg.show()

            return
        }

        adapter = HistoryAdapter()
        adapter.submitList(usedPlayers)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.root.setOnClickListener {}
        binding.backBtn.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        initAndAttachHistoryAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val historyCard = requireActivity().findViewById<View>(R.id.history_card)

        enterTransition = createEnterTransformation(historyCard, binding.historyContainer) {
            historyCard.hide()
        }

        returnTransition = createReturnTransformation(binding.historyContainer, historyCard) {
            historyCard.show()
        }
    }
}
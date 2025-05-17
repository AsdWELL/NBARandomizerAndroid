package com.example.nbarandomizer.ui.playerDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nbarandomizer.adapters.BadgesAdapter
import com.example.nbarandomizer.databinding.FragmentBadgesBinding
import com.example.nbarandomizer.listeners.IPageReadyListener
import com.example.nbarandomizer.models.Badge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BadgesFragment(
    private val badges: List<Badge>,
    private val onPageReadyListener: IPageReadyListener
) : Fragment() {
    private var _binding: FragmentBadgesBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: BadgesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBadgesBinding.inflate(inflater, container, false)

        lifecycleScope.launch(Dispatchers.IO) {
            adapter = BadgesAdapter()
            adapter.badgeCollection = badges

            withContext(Dispatchers.Main) {
                binding.badgesRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.badgesRecyclerView.adapter = adapter

                onPageReadyListener.onPageLoad()
            }
        }

        return binding.root
    }
}
package com.example.nbarandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nbarandomizer.adapters.AttributesAdapter
import com.example.nbarandomizer.databinding.FragmentAttributesBinding
import com.example.nbarandomizer.listeners.IPageReadyListener
import com.example.nbarandomizer.models.AttributeRatings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AttributesFragment(
    private val attrs: List<AttributeRatings>,
    private val onPageReadyListener: IPageReadyListener
) : Fragment() {
    private var _binding: FragmentAttributesBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: AttributesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttributesBinding.inflate(inflater, container, false)

        lifecycleScope.launch(Dispatchers.IO) {
            adapter = AttributesAdapter()
            adapter.attributesCollections = attrs

            withContext(Dispatchers.Main) {
                binding.attributesRecyclerView.layoutManager = GridLayoutManager(context, 2)
                binding.attributesRecyclerView.adapter = adapter

                onPageReadyListener.onPageLoad()
            }
        }

        return binding.root
    }
}
package com.example.nbarandomizer.ui.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nbarandomizer.R
import com.example.nbarandomizer.databinding.FragmentFilterBinding
import com.example.nbarandomizer.extensions.createEnterTransformation
import com.example.nbarandomizer.extensions.createReturnTransformation
import com.example.nbarandomizer.extensions.hide
import com.example.nbarandomizer.extensions.show
import com.example.nbarandomizer.models.FilterSettings
import com.example.nbarandomizer.models.Position
import com.example.nbarandomizer.viewModels.SharedViewModel

class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private fun initializeSpinner(textView: AutoCompleteTextView, values: List<String>) {
        val adapter = ArrayAdapter(requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            listOf(FilterSettings.FILTER_NONE_VALUE) + values)

        textView.setAdapter(adapter)
    }

    private fun initializeFilters() {
        with(binding.filterNameView) {
            setText(sharedViewModel.filterSettings.name)
        }

        with(binding.filterPositionView) {
            initializeSpinner(this, Position.entries.map { it.toString() })
            setText(sharedViewModel.filterSettings.position, false)
        }

        with(binding.filterTeamView) {
            initializeSpinner(this, sharedViewModel.selectedRoster.map { it.team }.distinct().sorted())
            setText(sharedViewModel.filterSettings.team, false)
        }
    }

    private fun setOnClickListeners() {
        binding.resetBtn.setOnClickListener {
            with(binding) {
                filterNameView.text.clear()
                filterPositionView.setText(FilterSettings.FILTER_NONE_VALUE, false)
                filterTeamView.setText(FilterSettings.FILTER_NONE_VALUE, false)
            }
        }

        binding.saveFiltersBtn.setOnClickListener {
            val name = binding.filterNameView.text.toString().trim()
            val team = binding.filterTeamView.text.toString()
            val position = binding.filterPositionView.text.toString()

            sharedViewModel.filterSettings =
                sharedViewModel.filterSettings.copy(name = name, team = team, position = position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        postponeEnterTransition()

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.root.setOnClickListener {}

        setOnClickListeners()

        initializeFilters()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnPreDraw {
            startPostponedEnterTransition()
        }

        val searchButton = requireActivity().findViewById<View>(R.id.searchButton)

        enterTransition = createEnterTransformation(searchButton, binding.filterContainer) {
            searchButton.hide()
        }

        returnTransition = createReturnTransformation(binding.filterContainer, searchButton) {
            searchButton.show()
        }
    }
}
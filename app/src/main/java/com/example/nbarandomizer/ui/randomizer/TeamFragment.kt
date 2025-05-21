package com.example.nbarandomizer.ui.randomizer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.transition.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.example.nbarandomizer.R
import com.example.nbarandomizer.animators.PlayerCardAnimator
import com.example.nbarandomizer.adapters.TeamAdapter
import com.example.nbarandomizer.databinding.TeamLayoutBinding
import com.example.nbarandomizer.extensions.show
import com.example.nbarandomizer.listeners.IPlayerCardListener
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.ui.playerDetails.PlayerDetailsFragment
import com.example.nbarandomizer.viewModels.SharedViewModel
import com.google.android.material.transition.platform.MaterialContainerTransform

class TeamFragment(
    private val players: MutableList<Player>,
    private val teamsCount: Int,
    private val getRandomPlayer: (position: Int) -> Player
) : Fragment() {
    private var _binding: TeamLayoutBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var adapter: TeamAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TeamLayoutBinding.inflate(inflater, container, false)

        binding.root.setOnClickListener {}

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        adapter = TeamAdapter(object : IPlayerCardListener {
            override fun onClick(position: Int) {
                val newData = adapter.currentList.toMutableList().apply {
                    this[position] = getRandomPlayer(position)
                }

                adapter.submitList(newData)
            }

            override fun onLongClick(player: Player, playerCard: View) {
                if (sharedViewModel.isDownloadingDetails()) {
                    Toast.makeText(context, "Погоди ща скачается", Toast.LENGTH_SHORT).show()
                    return
                }

                val playerDetailsFragment = PlayerDetailsFragment(sharedViewModel.playersDetails[player.id], playerCard)

                playerDetailsFragment.onNicknameUpdate = { playerName, nickname ->
                    adapter.submitList(adapter.currentList.map { item ->
                        if (item.name == playerName)
                            item.copy().apply { this.nickname = nickname}
                        else
                            item
                    })
                }

                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, playerDetailsFragment, "details")
                    .addToBackStack("details")
                    .commit()
            }
        })

        binding.recyclerView.itemAnimator = PlayerCardAnimator()
        binding.recyclerView.layoutManager = GridLayoutManager(context, teamsCount)
        binding.recyclerView.adapter = adapter

        adapter.submitList(players)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.randomizeButton)
            endView = binding.teamContainer
            duration = 350
            scrimColor = Color.TRANSPARENT
            addListener({_ -> requireActivity().findViewById<View>(R.id.randomizeButton).visibility = View.INVISIBLE})
        }

        returnTransition = TransitionSet().apply {
            addTransition(Fade().apply { addTarget(binding.teamContainer) })
            addTransition(Slide().apply { addTarget(binding.teamContainer) })
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = 350
        }
    }

    override fun onStop() {
        super.onStop()

        requireActivity().findViewById<View>(R.id.randomizeButton).show()
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
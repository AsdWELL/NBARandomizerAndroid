package com.example.nbarandomizer.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nbarandomizer.R
import com.example.nbarandomizer.adapters.IPlayerCardListener
import com.example.nbarandomizer.animators.PlayerCardAnimator
import com.example.nbarandomizer.adapters.TeamAdapter
import com.example.nbarandomizer.databinding.TeamLayoutBinding
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.viewModels.SharedViewModel

class TeamFragment(private val players: MutableList<Player>,
                   private val teamsCount: Int,
                   private val getRandomPlayer: (position: Int) -> Player)
    : DialogFragment() {
    private var _binding: TeamLayoutBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var adapter: TeamAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TeamLayoutBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        adapter = TeamAdapter(object : IPlayerCardListener {
            override fun onClick(position: Int) {
                val newData = adapter.currentList.toMutableList().apply {
                    this[position] = getRandomPlayer(position)
                }

                adapter.submitList(newData)
            }

            override fun onLongClick(player: Player) {
                if (sharedViewModel.isDownloadingDetails()) {
                    Toast.makeText(context, "Погоди ща скачается", Toast.LENGTH_SHORT).show()
                    return
                }

                val dialog = PlayerDetailsFragment(sharedViewModel.playersDetails[player.id])
                dialog.show(requireActivity().supportFragmentManager, "details")
            }
        })

        binding.recyclerView.itemAnimator = PlayerCardAnimator()
        binding.recyclerView.layoutManager = GridLayoutManager(context, teamsCount)
        binding.recyclerView.adapter = adapter

        adapter.submitList(players)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun getTheme(): Int {
        return R.style.RoundedDialogTheme
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
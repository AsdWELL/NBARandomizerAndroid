package com.example.nbarandomizer.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbarandomizer.R
import com.example.nbarandomizer.adapters.TeamAdapter
import com.example.nbarandomizer.callbacks.PlayersDiffCallback
import com.example.nbarandomizer.models.Player

class TeamFragment(private val teamsCount: Int) : DialogFragment() {
    private val adapter = TeamAdapter()

    fun setPlayers(players: MutableList<Player>) {
        val oldPlayers = adapter.playersCollection

        val diffResult = DiffUtil.calculateDiff(PlayersDiffCallback(oldPlayers, players))

        adapter.playersCollection = players

        diffResult.dispatchUpdatesTo(adapter)
    }

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
        val view = inflater.inflate(R.layout.team_layout, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = GridLayoutManager(context, teamsCount)
        recyclerView.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun getTheme(): Int {
        return R.style.CenteredDialogTheme
    }
}
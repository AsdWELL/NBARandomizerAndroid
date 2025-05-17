package com.example.nbarandomizer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nbarandomizer.listeners.IPageReadyListener
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.ui.playerDetails.AttributesFragment
import com.example.nbarandomizer.ui.playerDetails.BadgesFragment

class PlayerDetailsViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    playerDetails: PlayerDetails,
    onPageReadyListener: IPageReadyListener
) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        AttributesFragment(playerDetails.attributes, onPageReadyListener),
        BadgesFragment(playerDetails.badges, onPageReadyListener)
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}
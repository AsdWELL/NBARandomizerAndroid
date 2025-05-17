package com.example.nbarandomizer.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.nbarandomizer.R
import com.example.nbarandomizer.adapters.PlayerDetailsViewPagerAdapter
import com.example.nbarandomizer.databinding.PlayerDetailsBinding
import com.example.nbarandomizer.extensions.hide
import com.example.nbarandomizer.listeners.IPageReadyListener
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.ui.providers.CardOutlineProvider
import com.google.android.material.tabs.TabLayoutMediator

class PlayerDetailsFragment(private val playerDetails: PlayerDetails) : DialogFragment() {
    private var _binding: PlayerDetailsBinding? = null

    private val binding get() = _binding!!

    private var loadedPages = 0

    private val totalPages = 2

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(true)
        }
    }

    private fun setupCard(card: CardView) {
        card.cardElevation = 5f
        card.preventCornerOverlap = false
        card.clipToOutline = false
        card.outlineProvider = CardOutlineProvider(card)
    }

    private fun setCardColor(card: CardView, color: Int) {
        card.setCardBackgroundColor(color)
        card.outlineSpotShadowColor = color
    }

    private fun setDetails() {
        with(binding) {
            Glide.with(root)
                .load(playerDetails.photoUrl)
                .into(photo)

            name.text = playerDetails.name
            team.text = "Team: ${playerDetails.team}"
            height.text = "Height: ${playerDetails.height}cm"
            position.text = "Position: ${playerDetails.position}"
            overallTextView.text = playerDetails.overall.value.toString()

            setupCard(overallCardView)
            setCardColor(overallCardView, playerDetails.overall.color)

            initializeViewPager()
        }
    }

    private fun initializeViewPager() {
        with(binding.viewPager) {
            offscreenPageLimit = 2
            adapter = PlayerDetailsViewPagerAdapter(
                requireActivity(),
                playerDetails,
                object : IPageReadyListener {
                    override fun onPageLoad() {
                        loadedPages++

                        if (loadedPages == totalPages)
                            binding.progressBar.hide()
                    }
                })
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Attributes"
                1 -> tab.text = "Badges"
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerDetailsBinding.inflate(inflater, container, false)

        setDetails()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
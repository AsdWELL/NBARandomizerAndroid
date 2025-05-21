package com.example.nbarandomizer.ui.playerDetails

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.transition.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.example.nbarandomizer.adapters.PlayerDetailsViewPagerAdapter
import com.example.nbarandomizer.databinding.PlayerDetailsBinding
import com.example.nbarandomizer.extensions.hide
import com.example.nbarandomizer.extensions.show
import com.example.nbarandomizer.listeners.IPageReadyListener
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.ui.providers.CardOutlineProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.platform.MaterialContainerTransform
import java.io.File

class PlayerDetailsFragment(
    private val playerDetails: PlayerDetails,
    private val playerCard: View
) : Fragment() {
    private var _binding: PlayerDetailsBinding? = null

    private val binding get() = _binding!!

    private var loadedPages = 0

    private val totalPages = 2


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.transitionName = playerCard.transitionName

        enterTransition = MaterialContainerTransform().apply {
            startView = playerCard
            endView = binding.root
            duration = 350
            scrimColor = Color.TRANSPARENT
            addListener({_ -> binding.viewPager.show()})
        }

        returnTransition = TransitionSet().apply {
            addTransition(Fade().apply { addTarget(binding.root) })
            addTransition(Slide().apply { addTarget(binding.root) })
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = 350
        }
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
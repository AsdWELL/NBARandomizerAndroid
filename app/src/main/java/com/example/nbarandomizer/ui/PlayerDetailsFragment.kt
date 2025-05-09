package com.example.nbarandomizer.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.nbarandomizer.R
import com.example.nbarandomizer.adapters.AttributesAdapter
import com.example.nbarandomizer.adapters.BadgesAdapter
import com.example.nbarandomizer.databinding.PlayerDetailsBinding
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.ui.providers.CardOutlineProvider

class PlayerDetailsFragment(private val playerDetails: PlayerDetails) : DialogFragment() {
    private var _binding: PlayerDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var attributesAdapter: AttributesAdapter
    private lateinit var badgesAdapter: BadgesAdapter

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

            if (playerDetails.badges.isEmpty())
                badgeInfo.text = ""

            setupCard(overallCardView)
            setCardColor(overallCardView, playerDetails.overall.color)

            attributesRecyclerView.layoutManager = GridLayoutManager(context, 2)
            attributesRecyclerView.adapter = attributesAdapter

            badgesRecyclerView.layoutManager = LinearLayoutManager(context)
            badgesRecyclerView.adapter = badgesAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerDetailsBinding.inflate(inflater, container, false)

        attributesAdapter = AttributesAdapter()
        attributesAdapter.attributesCollections = playerDetails.attributes

        badgesAdapter = BadgesAdapter()
        badgesAdapter.badgeCollection = playerDetails.badges

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

        setDetails()
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDestroy() {
        _binding = null

        super.onDestroy()
    }
}
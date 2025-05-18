package com.example.nbarandomizer.ui.playerDetails

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.nbarandomizer.R
import com.example.nbarandomizer.adapters.PlayerDetailsViewPagerAdapter
import com.example.nbarandomizer.databinding.PlayerDetailsBinding
import com.example.nbarandomizer.extensions.hide
import com.example.nbarandomizer.listeners.IPageReadyListener
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.ui.providers.CardOutlineProvider
import com.example.nbarandomizer.viewModels.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File

class PlayerDetailsFragment(private val playerDetails: PlayerDetails) : DialogFragment() {
    private var _binding: PlayerDetailsBinding? = null

    private val binding get() = _binding!!

    private var loadedPages = 0

    private val totalPages = 2

    private lateinit var sharedViewModel: SharedViewModel

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

    private fun setNickNameChangeListener() {
        binding.nickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sharedViewModel.setNickname(playerDetails.name, s.toString().ifEmpty { null })

                onNicknameUpdate?.invoke(playerDetails.name, s.toString())
            }

            override fun afterTextChanged(s: Editable?) { }
        })
    }

    private fun setDetails() {
        with(binding) {
            Glide.with(root)
                .load(playerDetails.photoUrl)
                .into(photo)

            name.text = playerDetails.name
            nickname.setText(playerDetails.nickname)
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

    var onNicknameUpdate: ((playerName: String, nickname: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerDetailsBinding.inflate(inflater, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setDetails()
        setNickNameChangeListener()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        ViewCompat.setOnApplyWindowInsetsListener(dialog?.window?.decorView!!) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)

            WindowInsetsCompat.CONSUMED
        }
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onDestroy() {
        _binding = null
        sharedViewModel.saveNicknames(File(requireActivity().applicationContext.filesDir, sharedViewModel.nicknamesFile))

        super.onDestroy()
    }
}
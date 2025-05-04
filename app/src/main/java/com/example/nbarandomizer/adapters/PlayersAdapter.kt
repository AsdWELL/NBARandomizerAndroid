package com.example.nbarandomizer.adapters

import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nbarandomizer.R
import com.example.nbarandomizer.databinding.ItemPlayerBinding
import com.example.nbarandomizer.models.Player

class PlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)

class PlayerAdapter : RecyclerView.Adapter<PlayerViewHolder>() {
    var playersCollection: List<Player> = emptyList()

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerBinding.inflate(inflater, parent, false)

        return PlayerViewHolder(binding).also {
            context = it.itemView.context
        }
    }

    override fun getItemCount(): Int = playersCollection.size

    private fun getColor(colorId: Int) = ContextCompat.getColor(context, colorId)

    private fun getOvrColor(ovr: Int): Int {
        return when(ovr) {
            in 0..83 -> getColor(R.color.ovr8083)
            in 84..86 -> getColor(R.color.ovr8486)
            in 87..89 -> getColor(R.color.ovr8789)
            in 90..91 -> getColor(R.color.ovr9091)
            in 92..94 -> getColor(R.color.ovr9294)
            in 95..96 -> getColor(R.color.ovr9596)
            in 97..98 -> getColor(R.color.ovr9798)
            else -> getColor(R.color.ovr099)
        }
    }

    private fun getStatColor(value: Int): Int {
        return when(value) {
            in 0..59 -> getColor(R.color.stat059)
            in 60..69 -> getColor(R.color.stat6069)
            in 70..79 -> getColor(R.color.stat7079)
            in 80..89 -> getColor(R.color.stat8089)
            else -> getColor(R.color.stat9099)
        }
    }

    private fun setCardColor(card: CardView, color: Int) {
        card.setCardBackgroundColor(color)
        card.outlineSpotShadowColor = color
        ViewCompat.setElevation(card, 5f)
        card.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val radius = card.radius
                val glowSize = 15f

                outline.setRoundRect(
                    Rect(
                        (-glowSize).toInt(),
                        (-glowSize).toInt(),
                        (view.width + glowSize).toInt(),
                        (view.height + glowSize).toInt()
                    ),
                    radius + glowSize
                )
            }
        }
        card.clipToOutline = false
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = playersCollection[position]

        val binding = holder.binding

        binding.name.text = player.name
        binding.details.text = "${player.position} | ${player.height}cm | ${player.team}"
        binding.overallTextView.text = player.overall.toString()
        binding.threePtTextView.text = player.threePointRating.toString()
        binding.dunkTextView.text = player.dunkRating.toString()

        setCardColor(binding.overallCardView, getOvrColor(player.overall))
        setCardColor(binding.threePtCardView, getStatColor(player.threePointRating))
        setCardColor(binding.dunkCardView, getStatColor(player.dunkRating))

        Glide.with(holder.itemView.context)
            .load(player.photoUrl)
            .into(binding.photo)
    }
}
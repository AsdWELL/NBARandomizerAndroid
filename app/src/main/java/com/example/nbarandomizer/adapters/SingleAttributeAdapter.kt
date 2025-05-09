package com.example.nbarandomizer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nbarandomizer.databinding.ItemAttributeBinding
import com.example.nbarandomizer.models.Rating
import com.example.nbarandomizer.ui.providers.CardOutlineProvider

class SingleAttributeViewHolder(private val binding: ItemAttributeBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
        setupCard(binding.attributeCard)
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

    fun bind(rating: Rating) {
        with(binding) {
            setCardColor(attributeCard, rating.color)
            attributeValue.text = rating.value.toString()
            attributeName.text = rating.name
        }
    }
}

class SingleAttributeAdapter : RecyclerView.Adapter<SingleAttributeViewHolder>() {
    var ratingsCollection: List<Rating> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleAttributeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAttributeBinding.inflate(inflater, parent, false)

        return SingleAttributeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ratingsCollection.size
    }

    override fun onBindViewHolder(holder: SingleAttributeViewHolder, position: Int) {
        holder.bind(ratingsCollection[position])
    }
}
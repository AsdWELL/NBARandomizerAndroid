package com.example.nbarandomizer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbarandomizer.databinding.ItemPlayerAttributesBinding
import com.example.nbarandomizer.models.AttributeRatings
import com.example.nbarandomizer.ui.providers.CardOutlineProvider

class AttributesViewHolder(private val binding: ItemPlayerAttributesBinding) : RecyclerView.ViewHolder(binding.root) {
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

    fun bind(attributeRatings: AttributeRatings) {
        val singleAttributeAdapter = SingleAttributeAdapter()
        singleAttributeAdapter.ratingsCollection = attributeRatings.parameters

        with(binding) {
            setCardColor(attributeCard, attributeRatings.rating.color)
            attributeValue.text = attributeRatings.rating.value.toString()
            attributeName.text = attributeRatings.rating.name

            singleAttributeRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            singleAttributeRecyclerView.adapter = singleAttributeAdapter
        }
    }
}

class AttributesAdapter : RecyclerView.Adapter<AttributesViewHolder>() {
    var attributesCollections: List<AttributeRatings> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerAttributesBinding.inflate(inflater, parent, false)

        return AttributesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return attributesCollections.size
    }

    override fun onBindViewHolder(holder: AttributesViewHolder, position: Int) {
        holder.bind(attributesCollections[position])
    }
}
package com.example.nbarandomizer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nbarandomizer.databinding.ItemBadgeBinding
import com.example.nbarandomizer.models.Badge

class BadgeViewHolder(private val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(badge: Badge) {
        with (binding) {
            badgeName.text = badge.name
            badgeType.text = badge.type
            badgeDescription.text = badge.description

            Glide.with(root)
                .load(badge.photoUrl)
                .into(badgePhoto)
        }
    }
}

class BadgesAdapter : RecyclerView.Adapter<BadgeViewHolder>() {
    var badgeCollection: List<Badge> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBadgeBinding.inflate(inflater, parent, false)

        return BadgeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return badgeCollection.size
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(badgeCollection[position])
    }
}
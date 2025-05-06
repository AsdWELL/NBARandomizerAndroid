package com.example.nbarandomizer.ui.providers

import android.graphics.Outline
import android.graphics.Rect
import android.view.View
import android.view.ViewOutlineProvider
import androidx.cardview.widget.CardView

class CardOutlineProvider(private val card: CardView) : ViewOutlineProvider() {
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
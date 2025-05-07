package com.example.nbarandomizer.animators

import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class PlayerCardAnimator : DefaultItemAnimator() {
    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        val scale = 2f
        newHolder.itemView.alpha = 0f

        oldHolder.itemView.animate()
            .scaleX(scale)
            .scaleY(scale)
            .alpha(0f)
            .setDuration(250)
            .withEndAction {
                newHolder.itemView.scaleX = scale
                newHolder.itemView.scaleY = scale

                newHolder.itemView.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .alpha(1f)
                    .setInterpolator(OvershootInterpolator(0.5f))
                    .setDuration(250)
                    .withEndAction {
                        newHolder.itemView.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setInterpolator(OvershootInterpolator(0.5f))
                            .setDuration(150)
                            .withEndAction {
                                dispatchChangeFinished(oldHolder, true)
                                dispatchChangeFinished(newHolder, false)
                            }
                    }
                    .start()
            }
            .start()

        return true
    }
}
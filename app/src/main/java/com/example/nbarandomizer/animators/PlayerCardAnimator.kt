package com.example.nbarandomizer.animators

import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.nbarandomizer.adapters.TeamViewHolder

class PlayerCardAnimator : DefaultItemAnimator() {
    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        if (oldHolder.itemView.id == newHolder.itemView.id) {
            oldHolder.itemView.scaleX = 1f
            oldHolder.itemView.scaleY = 1f
            oldHolder.itemView.alpha = 1f

            newHolder.itemView.scaleX = 1f
            newHolder.itemView.scaleY = 1f
            newHolder.itemView.alpha = 1f

            return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        }

        (oldHolder as TeamViewHolder).isAnimating = true
        (newHolder as TeamViewHolder).isAnimating = true

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
                                oldHolder.isAnimating = false
                                newHolder.isAnimating = false

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
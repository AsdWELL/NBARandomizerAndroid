package com.example.nbarandomizer.extensions

import android.graphics.Color
import android.view.View
import androidx.core.transition.addListener
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.example.nbarandomizer.R
import com.google.android.material.transition.platform.MaterialContainerTransform

fun createEnterTransformation(
    startView: View,
    endView: View,
    onAnimationAction: (() -> Unit)? = null): MaterialContainerTransform {
    return MaterialContainerTransform().apply {
        drawingViewId = R.id.container
        addTarget(startView)
        this.endView = endView
        duration = 350
        scrimColor = Color.TRANSPARENT
        interpolator = LinearOutSlowInInterpolator()
        addListener({_ -> onAnimationAction?.invoke()})
    }
}

fun createReturnTransformation(
    startView: View,
    endView: View,
    onAnimationAction: (() -> Unit)? = null): MaterialContainerTransform {
    return MaterialContainerTransform().apply {
        drawingViewId = R.id.container
        addTarget(endView)
        this.startView = startView
        duration = 350
        scrimColor = Color.TRANSPARENT
        interpolator = LinearOutSlowInInterpolator()
        addListener({_ -> onAnimationAction?.invoke()})
    }
}
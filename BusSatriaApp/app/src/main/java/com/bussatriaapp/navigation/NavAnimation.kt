package com.bussatriaapp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

object NavAnimation {
    fun slideInFromRight(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    }

    fun slideOutToLeft(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }

    fun slideInFromLeft(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    }

    fun slideOutToRight(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }
}
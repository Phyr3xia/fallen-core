package de.fallen.core.view.animations

import android.animation.Animator
import android.view.View
import androidx.core.animation.doOnEnd

fun View.fadeOut(
   duration: Long = Animators.ANIM_TIME_DEFAULT,
   delay: Long? = null,
   doOnEnd: ((animator: Animator) -> Unit)? = null
) {
   Animators.fadeOutAnimator(this).apply {
      this.duration = duration
      delay?.let { startDelay = it }
   }.apply {
      doOnEnd?.let { this.doOnEnd(it) }
   }.start()
}
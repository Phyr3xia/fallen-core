package de.fallen.core.view.animations

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

object Animators {

   const val ANIM_TIME_DEFAULT: Long = 400

   fun translateYAnimator(target: View, translateY: Float): Animator {
      return ObjectAnimator.ofFloat(target, "translationY", translateY)
   }

   fun fadeInAnimator(target: View): Animator {
      return ObjectAnimator.ofFloat(target, "alpha", 0F, 1F).apply {
         doOnStart {
            if (!target.isVisible) {
               target.isVisible = true
            }
         }
      }
   }

   fun fadeOutAnimator(target: View): Animator {
      return ObjectAnimator.ofFloat(target, "alpha", 1F, 0F).apply {
         doOnEnd {
            target.isInvisible = true
         }
      }
   }

   fun rotateAnimator(target: View, degrees: Float): Animator {
      return ObjectAnimator.ofFloat(target, "rotation", degrees)
   }

}
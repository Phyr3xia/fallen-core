package de.fallen.core.view.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import de.fallen.core.view.animations.Animators.fadeInAnimator
import de.fallen.core.view.animations.Animators.fadeOutAnimator
import de.fallen.core.view.animations.Animators.rotateAnimator
import de.fallen.core.view.animations.Animators.translateYAnimator

class AdditiveAnimator private constructor(
   private val mTargets: List<View>,
   private val mAnimators: MutableList<Animator>,
   private var mStartDelay: Long
) {

   private val nextStartDelay: Long
      get() = mStartDelay + (mAnimators.map { it.duration }.maxOrNull() ?: 0)

   private var initialized: Boolean = false

   init {
      require(mTargets.isNotEmpty()) {
         "AdditiveAnimator must be initialized with at least one view as target"
      }
   }

   companion object {

      fun animate(target: View, vararg moreTargets: View): AdditiveAnimator {
         return AdditiveAnimator(targets(target, moreTargets), mutableListOf(), 0)
      }

      private fun targets(target: View, moreTargets: Array<out View>): List<View> {
         return mutableListOf(target).apply {
            addAll(moreTargets)
         }
      }

   }

   private fun add(
      animatorFactory: (View) -> Animator,
      duration: Long?,
   ): AdditiveAnimator {
      if (mTargets.size == 1) {
         mAnimators.add(animatorFactory(mTargets.first()).apply {
            this.duration = duration ?: Animators.ANIM_TIME_DEFAULT
            startDelay = mStartDelay
         })
      } else {
         val mDuration = duration ?: Animators.ANIM_TIME_DEFAULT
         for (target in mTargets) {
            mAnimators.add(animatorFactory(target).apply {
               this.duration = mDuration
               startDelay = mStartDelay
            })
         }
      }
      initialized = true
      return this
   }

   fun translateY(translateY: Float, duration: Long? = null): AdditiveAnimator {
      return add({ v -> translateYAnimator(v, translateY) }, duration)
   }

   fun fadeIn(duration: Long? = null): AdditiveAnimator {
      return add({ v -> fadeInAnimator(v) }, duration)
   }

   fun fadeOut(duration: Long? = null): AdditiveAnimator {
      return add({ v -> fadeOutAnimator(v) }, duration)
   }

   fun toggleVisibility(duration: Long? = null): AdditiveAnimator {
      return add(
         { v ->
            if (v.isVisible) {
               fadeOutAnimator(v)
            } else {
               fadeInAnimator(v)
            }
         }, duration
      )
   }

   fun rotate(degrees: Float, duration: Long? = null): AdditiveAnimator {
      return add({ v -> rotateAnimator(v, degrees) }, duration)
   }

   fun doOnEnd(action: (animator: Animator) -> Unit): AdditiveAnimator {
      require(mAnimators.isNotEmpty()) {
         "No animation defined"
      }
      mAnimators.last().doOnEnd(action)
      return this
   }

   fun pause(millis: Long): AdditiveAnimator {
      mStartDelay = nextStartDelay + millis
      return this
   }

   fun then(): AdditiveAnimator {
      require(mAnimators.isNotEmpty()) {
         "No animation defined"
      }
      mStartDelay = nextStartDelay
      initialized = false
      return this
   }

   fun thenAnimate(target: View, vararg moreTargets: View): AdditiveAnimator {
      return AdditiveAnimator(
         targets(target, moreTargets),
         mAnimators,
         nextStartDelay
      )
   }

   fun withEarlyStart(millis: Long): AdditiveAnimator {
      require(!initialized) {
         "withEarlyStart must be called directly after then or thenAnimate"
      }
      require(nextStartDelay > millis) {
         "Animation can not start before the last animation"
      }
      mStartDelay -= millis
      return this
   }

   fun start(delayInMillis: Long = 0) {
      AnimatorSet().run {
         startDelay = delayInMillis
         playTogether(mAnimators)
         start()
      }
   }

}
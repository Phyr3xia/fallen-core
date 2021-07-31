package de.fallen.core.os

import android.os.Handler
import android.os.Looper

data class PostRegistration(
   val handler: Handler = Handler(Looper.getMainLooper()),
   val runnable: Runnable,
   val delayMillis: Long? = null
) {

   fun post(removePendingPosts: Boolean = false, ignoreDelay: Boolean = false) {
      if (removePendingPosts) {
         cancel()
      }
      if (delayMillis != null && !ignoreDelay) {
         handler.postDelayed(runnable, delayMillis)
      } else {
         handler.post(runnable)
      }
   }

   fun cancel() {
      handler.removeCallbacks(runnable)
   }

}


fun post(r: () -> Unit): PostRegistration {
   return PostRegistration(Handler(Looper.getMainLooper()), r).also {
      it.post()
   }
}

fun postDelayed(r: () -> Unit, delayMillis: Long): PostRegistration {
   return PostRegistration(Handler(Looper.getMainLooper()), r, delayMillis).also {
      it.post()
   }
}
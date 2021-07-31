package de.fallen.core.lifecycle

abstract class SingleLiveDataEvent {

   private var handled = false

   fun handle(r: () -> Unit): Boolean {
      if (!handled) {
         r.invoke()
         handled = true
         return true
      }
      return false
   }

}
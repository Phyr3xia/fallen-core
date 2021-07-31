package de.fallen.core.lifecycle

import de.fallen.core.lifecycle.LiveDataValueWrapper.Status

open class LiveDataValueWrapper<S : Status>(val status: S) {

   interface Status

   enum class LiveDataValueStatus : Status {
      WAITING,
      SUCCESS,
      FAILURE
   }

}
package de.fallen.core.lifecycle

import de.fallen.core.lifecycle.LiveDataValueWrapper.LiveDataValueStatus

class Resource<T, V>(
   status: LiveDataValueStatus,
   val result: T? = null,
   val validationErrors: List<V> = emptyList()
) : LiveDataValueWrapper<LiveDataValueStatus>(status) {

   constructor(status: LiveDataValueStatus, result: T? = null, validationError: V) : this(
      status,
      result,
      listOf(
         validationError
      )
   )

}
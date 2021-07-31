package de.fallen.core.lifecycle

import de.fallen.core.lifecycle.LiveDataValueWrapper.LiveDataValueStatus

class Action(status: LiveDataValueStatus) : LiveDataValueWrapper<LiveDataValueStatus>(status) {

   companion object {

      fun waiting() = Action(LiveDataValueStatus.WAITING)

      fun success() = Action(LiveDataValueStatus.SUCCESS)

      fun failure() = Action(LiveDataValueStatus.FAILURE)

   }

}
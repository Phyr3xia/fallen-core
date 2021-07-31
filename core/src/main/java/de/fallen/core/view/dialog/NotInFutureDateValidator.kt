package de.fallen.core.view.dialog

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints

class NotInFutureDateValidator(private val time: Long) : CalendarConstraints.DateValidator {

   constructor(parcel: Parcel) : this(parcel.readLong())

   override fun describeContents(): Int {
      return 0
   }

   override fun writeToParcel(parcel: Parcel?, flags: Int) {
      parcel?.writeLong(time)
   }

   override fun isValid(date: Long): Boolean {
      return date <= time
   }

   companion object CREATOR : Parcelable.Creator<NotInFutureDateValidator> {
      override fun createFromParcel(parcel: Parcel): NotInFutureDateValidator {
         return NotInFutureDateValidator(parcel)
      }

      override fun newArray(size: Int): Array<NotInFutureDateValidator?> {
         return arrayOfNulls(size)
      }
   }
}
package de.fallen.core.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import de.fallen.core.R
import java.util.Date

fun Context.alert(
   @StringRes messageRes: Int? = null,
   message: String? = null,
   @StringRes titleRes: Int? = null,
   title: String? = null,
   @StringRes confirmRes: Int = R.string.ok
) {
   assertMessage(message, messageRes)

   initDialog(
      context = this,
      titleRes = titleRes,
      title = title,
      messageRes = messageRes,
      message = message,
      positiveButtonRes = confirmRes,
      view = null,
      cancelable = false
   ).show()
}

fun Context.confirm(
   @StringRes messageRes: Int? = null,
   confirmControl: ConfirmControl,
   message: String? = null,
   @StringRes titleRes: Int? = null,
   title: String? = null,
   @StringRes confirmRes: Int = R.string.yes,
   @StringRes declineRes: Int = R.string.no,
) {
   assertMessage(message, messageRes)

   val dialog = initDialog(
      context = this,
      titleRes = titleRes,
      title = title,
      messageRes = messageRes,
      message = message,
      view = null,
      positiveButtonRes = confirmRes,
      negativeButtonRes = declineRes,
      cancelable = true,
      modal = false
   )

   dialog.show()

   val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
   positiveButton.setOnClickListener {
      if (confirmControl.onConfirm()) {
         dialog.dismiss()
      }
   }
   val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
   negativeButton.setOnClickListener {
      if (confirmControl.onDecline()) {
         dialog.dismiss()
      }
   }
}

interface ConfirmControl {

   fun onConfirm(): Boolean

   fun onDecline(): Boolean = true

}

@SuppressLint("InflateParams")
fun Context.input(
   inputControl: InputControl,
   @StringRes messageRes: Int? = null,
   message: String? = null,
   @StringRes titleRes: Int? = null,
   title: String? = null,
   @StringRes labelRes: Int? = null,
   @StringRes submitRes: Int
) {
   val inflater = LayoutInflater.from(this)
   val view = inflater.inflate(R.layout.dialog_input, null)

   val dialog = initDialog(
      context = this,
      titleRes = titleRes,
      title = title,
      messageRes = messageRes,
      message = message,
      view = view,
      positiveButtonRes = submitRes,
      cancelable = true
   )

   val inputLayout: TextInputLayout = view.findViewById(R.id.inputLayout)
   val input: EditText = inputLayout.editText!!

   labelRes?.let { inputLayout.hint = this.getString(it) }

   dialog.setOnShowListener {
      input.requestFocus()
      val r = Runnable {
         val keyboard: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         keyboard.showSoftInput(input, 0)
      }
      input.postDelayed(r, 100)

      val submitButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
      submitButton.isEnabled = false

      input.addTextChangedListener(object : TextWatcher {
         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

         override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

         override fun afterTextChanged(p0: Editable?) {
            p0?.let {
               submitButton.isEnabled = inputControl.validateInput(it.toString())
               inputLayout.error = null
            }
         }
      })

      fun submit() {
         val text = input.text?.toString()
         text?.let {
            val errorOnSubmit = inputControl.onConfirm(text)
            if (errorOnSubmit != null) {
               inputLayout.error = errorOnSubmit.message
               submitButton.isEnabled = false
            } else {
               dialog.dismiss()
            }
         }
      }

      submitButton.setOnClickListener { submit() }

      input.setOnKeyListener { _, keyCode, event ->
         if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
            if (submitButton.isEnabled) {
               submit()
            }
            return@setOnKeyListener true
         }
         return@setOnKeyListener false
      }
   }

   dialog.show()
}

interface InputControl {

   class ErrorOnSubmit(val message: String)

   fun validateInput(input: String): Boolean = true

   fun onConfirm(input: String): ErrorOnSubmit?

}

fun FragmentActivity.datePicker(
   onDateSetListener: MaterialPickerOnPositiveButtonClickListener<Long>,
   date: Date = Date()
) {
   val dateInMillis = date.time
   val builder = MaterialDatePicker.Builder.datePicker()
   val picker = builder
      .setTheme(R.style.Widget_App_MaterialDatePicker)
      .setSelection(dateInMillis)
      .setCalendarConstraints(
         CalendarConstraints.Builder()
            .setEnd(dateInMillis)
            .setOpenAt(dateInMillis)
            .setValidator(NotInFutureDateValidator(dateInMillis))
            .build()
      )
      .setTitleText(getString(R.string.select_a_date))
      .build()

   picker.addOnPositiveButtonClickListener(onDateSetListener)
   picker.show(this.supportFragmentManager, "DATE PICK")
}

private fun assertMessage(message: String?, messageRes: Int?) {
   if (message.isNullOrBlank() && messageRes == null) {
      throw IllegalArgumentException("Alert has no message")
   }
}

private fun initDialog(
   context: Context,
   @StringRes titleRes: Int?,
   title: String?,
   @StringRes messageRes: Int?,
   message: String?,
   view: View?,
   @StringRes positiveButtonRes: Int,
   @StringRes negativeButtonRes: Int = R.string.cancel,
   cancelable: Boolean,
   modal: Boolean = cancelable
): AlertDialog {
   val builder = MaterialAlertDialogBuilder(context)
      .setView(view)
      .setPositiveButton(positiveButtonRes, null)
      .setCancelable(modal)

   if (cancelable) {
      builder.setNegativeButton(negativeButtonRes, null)
   }

   if (title != null) {
      builder.setTitle(title)
   } else if (titleRes != null) {
      builder.setTitle(titleRes)
   }

   if (message != null) {
      builder.setMessage(message)
   } else if (messageRes != null) {
      builder.setMessage(messageRes)
   }

   return builder.create()
}
package de.fallen.core.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

open class DividerItemDecoration(
   context: Context,
   orientation: Int,
   private val dividerBoundsOverrides: BoundsOverrides? = null
) :
   RecyclerView.ItemDecoration() {

   data class BoundsOverrides(
      val left: Int? = null,
      val top: Int? = null,
      val right: Int? = null,
      val bottom: Int? = null
   )

   companion object {

      const val HORIZONTAL = LinearLayout.HORIZONTAL
      const val VERTICAL = LinearLayout.VERTICAL

   }

   private val attrs = intArrayOf(android.R.attr.listDivider)
   private lateinit var mDivider: Drawable
   private var mOrientation = 0
   private val mBounds = Rect()

   init {
      val a = context.obtainStyledAttributes(attrs)
      a.getDrawable(0)?.let {
         mDivider = it
      } ?: kotlin.run {
         throw IllegalStateException("No listDivider set in theme")
      }
      a.recycle()
      setOrientation(orientation)
   }

   private fun setOrientation(orientation: Int) {
      require(orientation == HORIZONTAL || orientation == VERTICAL) {
         "Invalid orientation. It should be either HORIZONTAL or VERTICAL"
      }
      mOrientation = orientation
   }

   override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
      if (parent.layoutManager == null) {
         return
      }
      if (mOrientation == VERTICAL) {
         drawVertical(canvas, parent)
      } else {
         drawHorizontal(canvas, parent)
      }
   }

   private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
      canvas.save()
      val left: Int
      val right: Int
      if (parent.clipToPadding) {
         left = parent.paddingLeft
         right = parent.width - parent.paddingRight
         canvas.clipRect(
            left, parent.paddingTop, right,
            parent.height - parent.paddingBottom
         )
      } else {
         left = 0
         right = parent.width
      }
      val childCount = parent.childCount
      for (i in 0 until childCount - 1) {
         val child = parent.getChildAt(i)
         parent.getDecoratedBoundsWithMargins(child, mBounds)
         val bottom = mBounds.bottom + child.translationY.roundToInt()
         val top = bottom - mDivider.intrinsicHeight
         mDivider.setBounds(
            dividerBoundsOverrides?.left ?: left,
            dividerBoundsOverrides?.top ?: top,
            dividerBoundsOverrides?.right?.let { right - it } ?: right,
            dividerBoundsOverrides?.bottom ?: bottom
         )
         mDivider.draw(canvas)
      }
      canvas.restore()
   }

   private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
      canvas.save()
      val top: Int
      val bottom: Int
      if (parent.clipToPadding) {
         top = parent.paddingTop
         bottom = parent.height - parent.paddingBottom
         canvas.clipRect(
            parent.paddingLeft, top,
            parent.width - parent.paddingRight, bottom
         )
      } else {
         top = 0
         bottom = parent.height
      }
      val childCount = parent.childCount
      for (i in 0 until childCount - 1) {
         val child = parent.getChildAt(i)
         parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
         val right = mBounds.right + child.translationX.roundToInt()
         val left = right - mDivider.intrinsicWidth
         mDivider.setBounds(
            dividerBoundsOverrides?.left ?: left,
            dividerBoundsOverrides?.top ?: top,
            dividerBoundsOverrides?.right?.let { right - it } ?: right,
            dividerBoundsOverrides?.bottom ?: bottom
         )
         mDivider.draw(canvas)
      }
      canvas.restore()
   }

   override fun getItemOffsets(
      outRect: Rect, view: View, parent: RecyclerView,
      state: RecyclerView.State
   ) {
      if (mOrientation == VERTICAL) {
         outRect[0, 0, 0] = mDivider.intrinsicHeight
      } else {
         outRect[0, 0, mDivider.intrinsicWidth] = 0
      }
   }

}

class IndentedVerticalItemDecoration(
   context: Context,
   indentInPx: Int,
   indentRightInPx: Int? = null
) :
   DividerItemDecoration(
      context,
      VERTICAL,
      BoundsOverrides(left = indentInPx, right = indentRightInPx ?: indentInPx)
   )
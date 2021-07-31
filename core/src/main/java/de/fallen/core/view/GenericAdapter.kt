package de.fallen.core.view

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import de.fallen.core.view.GenericAdapter.ListItem

abstract class GenericAdapter<I : ListItem> : RecyclerView.Adapter<GenericAdapter.ViewHolder<I>>() {

   interface ListItem {
      val id: Any
      val type: Int
   }

   abstract class ViewHolder<I : ListItem>(binding: ViewBinding) :
      RecyclerView.ViewHolder(binding.root) {

      val context: Context by lazy {
         itemView.context
      }

      abstract fun bind(item: I)

   }

   open class SimpleDiffCallback<I : ListItem> : ItemCallback<I>() {
ƒ
      override fun areItemsTheSame(oldItem: I, newItem: I): Boolean {
         return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: I, newItem: I): Boolean {
         return oldItem.type == newItem.type
      }

   }

   var items: List<I> = emptyList()
      private set

   fun setItems(items: List<I>, diffCallback: ItemCallback<I>? = null) {
      if (diffCallback != null) {
         val mCallback = object : DiffUtil.Callback() {

            override fun getOldListSize(): Int {
               return this@GenericAdapter.items.size
            }

            override fun getNewListSize(): Int {
               return items.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
               val oldItem = this@GenericAdapter.items[oldItemPosition]
               val newItem = items[newItemPosition]
               return diffCallback.areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(
               oldItemPosition: Int,
               newItemPosition: Int
            ): Boolean {
               val oldItem = this@GenericAdapter.items[oldItemPosition]
               val newItem = items[newItemPosition]
               return diffCallback.areContentsTheSame(oldItem, newItem)
            }
         }
         val diff = DiffUtil.calculateDiff(mCallback)
         this.items = items
         diff.dispatchUpdatesTo(this)
      } else {
         this.items = items
         notifyDataSetChanged()
      }
   }

   @Suppress("UNCHECKED_CAST")
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<I> {
      return doOnCreateViewHolder(parent, viewType) as ViewHolder<I>
   }

   abstract fun doOnCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<out I>

   override fun onBindViewHolder(holder: ViewHolder<I>, position: Int) {
      holder.bind(items[position])
   }

   override fun getItemCount(): Int {
      return items.size
   }

   override fun getItemViewType(position: Int): Int {
      return items[position].type
   }

   fun clear() {
      items = emptyList()
   }
}
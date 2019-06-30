package com.github.mrbean355.android.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.mrbean355.android.EnhancedAdapter
import com.github.mrbean355.android.enhancedadapter.demo.R

private const val MAX_SELECTIONS = 3

class CountryAdapter : EnhancedAdapter<Country, CountryAdapter.ViewHolder>(DiffCallbacks(), MAX_SELECTIONS) {

    /** Create the view holder like normal. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    /** Bind the view holder like normal, with some additional steps. */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItemAt(position)
        holder.textView.text = item.name

        // If the item is selected, do something to indicate it.
        holder.textView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, if (isItemSelected(position)) R.color.colorAccent else android.R.color.transparent))

        // When an item is clicked, trigger a selection event.
        holder.itemView.setOnClickListener {
            onItemClicked(holder.adapterPosition)
        }
    }

    /** Sort the items alphabetically. */
    override fun compareItems(lhs: Country, rhs: Country): Int {
        return lhs.name.compareTo(rhs.name)
    }

    /** Items match the search query if they contain it. */
    override fun testItem(item: Country, query: String): Boolean {
        return item.name.contains(query, ignoreCase = true)
    }

    /** Normal view holder stuff. */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView as TextView
    }

    /**
     * Class that decides if items have changed.
     *
     * See: https://developer.android.com/reference/android/support/v7/util/DiffUtil
     */
    class DiffCallbacks : DiffUtil.ItemCallback<Country>() {

        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return true
        }
    }
}

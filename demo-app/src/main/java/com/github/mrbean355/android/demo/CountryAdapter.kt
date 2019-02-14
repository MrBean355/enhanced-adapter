package com.github.mrbean355.android.demo

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.github.mrbean355.android.EnhancedAdapter
import com.github.mrbean355.android.enhancedadapter.demo.R

class CountryAdapter : EnhancedAdapter<String, CountryAdapter.ViewHolder>(DiffCallbacks(), 3) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_country, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkBox.text = getItemAt(position)
        holder.checkBox.isChecked = isItemSelected(position)
        holder.itemView.setOnClickListener {
            onItemClicked(holder.adapterPosition)
        }
    }

    override fun compareItems(lhs: String, rhs: String): Int {
        return lhs.compareTo(rhs)
    }

    override fun testItem(item: String, query: String): Boolean {
        return item.contains(query, ignoreCase = true)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox = itemView as CheckBox
    }

    class DiffCallbacks : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(p0: String, p1: String): Boolean {
            return true
        }
    }
}
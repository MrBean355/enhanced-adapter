package com.github.mrbean355.android.asyncrecyclerviewadapter.demo

import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mrbean355.android.asyncrecyclerviewadapter.AsyncRecyclerViewAdapter

class DemoAdapter : AsyncRecyclerViewAdapter<String, DemoAdapter.ViewHolder>(
        DiffCallbacks(),
        { o1, o2 -> o1.length.compareTo(o2.length) },
        { query, item -> item.contains(query, ignoreCase = true) }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = getItemAt(position)
        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, if (isItemSelected(position)) R.color.colorAccent else android.R.color.transparent))
        holder.itemView.setOnClickListener {
            onItemClicked(holder.adapterPosition)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView as TextView
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

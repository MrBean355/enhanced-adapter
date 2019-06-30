package com.github.mrbean355.android

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Activity used solely for testing the [EnhancedAdapter] with Espresso.
 */
class TestActivity : Activity() {
    private lateinit var adapter: TestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        setContentView(recyclerView, ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT))

        adapter = TestAdapter()
        adapter.setItems(listOf("f", "b", "a", "e", "c", "d"))
        val selection = intent.getStringExtra(KEY_SELECTED_ITEM)
        if (selection != null) {
            adapter.setSelectedItems(listOf(selection))
        }
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "FILTER")?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menu?.add(0, 2, 0, "RESET")?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == 1) {
            adapter.filter("c")
            return true
        }
        if (item?.itemId == 2) {
            adapter.filter(null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class TestAdapter : EnhancedAdapter<String, TestAdapter.ViewHolder>(TestDiffCallback(), 2) {

        override fun compareItems(lhs: String, rhs: String): Int {
            return lhs.compareTo(rhs)
        }

        override fun testItem(item: String, query: String): Boolean {
            return item.contains(query, ignoreCase = true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = getItemAt(position) + if (isItemSelected(position)) " SELECTED" else ""
            holder.itemView.setOnClickListener {
                onItemClicked(holder.adapterPosition)
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView as TextView
        }

        private class TestDiffCallback : DiffUtil.ItemCallback<String>() {

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return true
            }
        }
    }

    companion object {
        const val KEY_SELECTED_ITEM = "SELECTED_ITEM"
    }
}
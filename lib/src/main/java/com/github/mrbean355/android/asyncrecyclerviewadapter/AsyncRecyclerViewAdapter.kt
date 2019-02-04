package com.github.mrbean355.android.asyncrecyclerviewadapter

import android.support.v4.util.ArraySet
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

abstract class AsyncRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder>(
        private val diffCallback: DiffUtil.ItemCallback<T>,
        private val sortComparator: (o1: T, o2: T) -> Int,
        private val filterPredicate: (query: String, item: T) -> Boolean) : RecyclerView.Adapter<VH>() {

    private var sourceItems: List<T> = emptyList()
    private var displayedItems: List<T> = emptyList()
    private val selectedItems: MutableSet<T> = mutableSetOf()
    private var busy = false

    /**
     * Set the adapter's items.
     *
     * The items will be sorted according to the [sortComparator].
     * Resets any filtering done via [filter].
     * Ignores any `null` elements in the collection.
     */
    fun setItems(items: Collection<T>?) {
        this.sourceItems = items.orEmpty()
                .filter { it != null }
                .sortedWith(Comparator { o1, o2 -> sortComparator(o1, o2) })
        publishList(sourceItems)
    }

    /**
     * @return a set of currently selected items.
     */
    fun getSelectedItems(): Set<T> {
        return ArraySet(selectedItems)
    }

    /**
     * Set the currently selected items.
     */
    fun setSelectedItems(selection: Collection<T>) {
        val changedItems = disjunctiveUnion(selectedItems, selection)
        selectedItems.clear()
        selectedItems.addAll(selection)
        changedItems.map { displayedItems.indexOf(it) }
                .filter { it != -1 }
                .forEach {
                    notifyItemChanged(it)
                }
    }

    /**
     * Filter the displayed items based on some [query].
     *
     * A `null` or empty [query] causes all items to be displayed.
     */
    fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            publishList(sourceItems)
            return
        }
        val filtered = this.sourceItems.filter {
            filterPredicate(query, it)
        }
        publishList(filtered)
    }

    /**
     * @return the currently displayed item at [position].
     */
    fun getItemAt(position: Int) = displayedItems[position]

    /**
     * @return `true` if the displayed item at [position] is currently selected, `false` otherwise.
     */
    fun isItemSelected(position: Int): Boolean {
        return selectedItems.contains(getItemAt(position))
    }

    override fun getItemCount() = displayedItems.size

    /**
     * Trigger a selection event; selecting the item at [adapterPosition] if its not selected, and deselecting it otherwise.
     */
    protected fun onItemClicked(adapterPosition: Int) {
        val item = getItemAt(adapterPosition)
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
        val index = displayedItems.indexOf(item)
        if (index != -1) {
            notifyItemChanged(index)
        }
    }

    private fun publishList(newList: List<T>) {
        // FIXME: Enqueue updates instead of ignoring them.
        if (busy) {
            return
        }
        busy = true
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = displayedItems.size

            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = displayedItems[oldItemPosition]
                val newItem = newList[newItemPosition]
                return if (oldItem != null && newItem != null) {
                    diffCallback.areItemsTheSame(oldItem, newItem)
                } else {
                    oldItem == null && newItem == null
                }
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = displayedItems[oldItemPosition]
                val newItem = newList[newItemPosition]
                return if (oldItem != null && newItem != null) {
                    diffCallback.areContentsTheSame(oldItem, newItem)
                } else if (oldItem == null && newItem == null) {
                    true
                } else {
                    throw AssertionError()
                }
            }
        }).dispatchUpdatesTo(this)
        this.displayedItems = newList
        busy = false
    }

    /**
     * Return a collection of items that are in either [a] or [b], but not both.
     */
    private fun <T> disjunctiveUnion(a: Collection<T>, b: Collection<T>): Collection<T> {
        val result = a.union(b).toMutableSet()
        val intersect = a.intersect(b)
        result.removeAll(intersect)
        return result
    }
}
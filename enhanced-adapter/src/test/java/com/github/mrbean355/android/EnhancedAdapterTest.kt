package com.github.mrbean355.android

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EnhancedAdapterTest {
    @get:Rule
    val mainThreadRule = MainThreadRule()
    private lateinit var adapter: TestEnhancedAdapter

    @Before
    fun setUp() {
        adapter = spy(TestEnhancedAdapter())
        doNothing().whenever(adapter).notifyItemRangeInserted(any(), any())
        doNothing().whenever(adapter).notifyItemChanged(any())
    }

    @Test
    fun testCompareItems_DefaultBehaviour_ReturnsZero() {
        val result = SimpleTestEnhancedAdapter().compareItems(1, 5)

        assertEquals(0, result)
    }

    @Test
    fun testTestItem_DefaultBehaviour_ReturnsFalse() {
        val result = SimpleTestEnhancedAdapter().testItem(1, "test")

        assertFalse(result)
    }

    @Test
    fun testSetItems_SortsItemsByCustomComparator() {
        adapter.setItems(listOf(1, 3, 6, 2, 0, 9))

        assertArrayEquals(arrayOf(9, 6, 3, 2, 1, 0), adapter.sourceItems.toTypedArray())
    }

    @Test
    fun testSetItems_WithNullInput_SetsSourceItemsFieldToEmptyList() {
        adapter.setItems(null)

        assertTrue(adapter.sourceItems.isEmpty())
    }

    @Test
    fun testSetItems_WithNullInput_SetsItemFieldsToEmptyList() {
        adapter.setItems(null)

        assertTrue(adapter.sourceItems.isEmpty())
        assertTrue(adapter.displayedItems.isEmpty())
    }

    @Test
    fun testSetItems_WithNonNullInput_SetsSortedItemFields() {
        adapter.setItems(listOf(1, 3, 6, 2, 0, 9))

        assertArrayEquals(arrayOf(9, 6, 3, 2, 1, 0), adapter.sourceItems.toTypedArray())
        assertArrayEquals(arrayOf(9, 6, 3, 2, 1, 0), adapter.displayedItems.toTypedArray())
    }

    @Test
    fun testGetSelectedItems_ReturnsDifferentCollectionToField() {
        adapter.selectedItems.addAll(listOf(1, 2))

        val result = adapter.getSelectedItems()

        assertNotSame(adapter.selectedItems, result)
    }

    @Test
    fun testGetSelectedItems_ReturnsCollectionWithSameElements() {
        adapter.selectedItems.addAll(listOf(1, 2))

        val result = adapter.getSelectedItems()

        assertArrayEquals(arrayOf(1, 2), result.toTypedArray())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetSelectedItems_NewSelectionExceedsMaxSelections_ThrowsException() {
        adapter.setSelectedItems(listOf(1, 2, 3))
    }

    @Test
    fun testSetSelectedItems_SelectedItemsFieldUpdatedCorrectly() {
        adapter.setSelectedItems(listOf(1, 2))

        assertArrayEquals(arrayOf(1, 2), adapter.selectedItems.toTypedArray())
    }

    @Test
    fun testSetSelectedItems_NotifiesChangedItems() {
        adapter.selectedItems.add(1)
        adapter.displayedItems = listOf(0, 1, 2, 3)

        adapter.setSelectedItems(listOf(1, 2))

        verify(adapter).notifyItemChanged(2)
    }

    @Test
    fun testFilter_NullQuery_SetsDisplayedItemsFieldToSourceItemsField() {
        adapter.sourceItems = listOf(1, 2, 3, 4)
        adapter.displayedItems = listOf()

        adapter.filter(null)

        assertArrayEquals(arrayOf(1, 2, 3, 4), adapter.displayedItems.toTypedArray())
    }

    @Test
    fun testFilter_EmptyQuery_SetsDisplayedItemsFieldToSourceItemsField() {
        adapter.sourceItems = listOf(1, 2, 3, 4)
        adapter.displayedItems = listOf()

        adapter.filter("")

        assertArrayEquals(arrayOf(1, 2, 3, 4), adapter.displayedItems.toTypedArray())
    }

    @Test
    fun testFilter_NonEmptyQuery_UpdatesDisplayedItemsFieldToContainMatchingItems() {
        adapter.sourceItems = listOf(1, 2, 3, 4)
        adapter.displayedItems = listOf()

        adapter.filter("test")

        assertEquals(2, adapter.displayedItems.size)
        assertTrue(adapter.displayedItems.contains(1))
        assertTrue(adapter.displayedItems.contains(2))
    }

    @Test
    fun testGetItemAt_ReturnsDisplayItemAt() {
        adapter.displayedItems = listOf(1, 2, 3)

        val result = adapter.testGetItemAt(1)

        assertEquals(2, result)
    }

    @Test
    fun testIsItemSelected_ItemContainedInSelectedItems_ReturnsTrue() {
        adapter.displayedItems = listOf(1, 2, 3)
        adapter.selectedItems.add(3)

        val result = adapter.isItemSelected(2)

        assertTrue(result)
    }

    @Test
    fun testIsItemSelected_ItemNotContainedInSelectedItems_ReturnsFalse() {
        adapter.displayedItems = listOf(1, 2, 3)
        adapter.selectedItems.add(3)

        val result = adapter.isItemSelected(0)

        assertFalse(result)
    }

    @Test
    fun testGetItemCount_ReturnsDisplayedItemCount() {
        adapter.displayedItems = listOf(1, 2, 3)

        val result = adapter.itemCount

        assertEquals(3, result)
    }

    @Test
    fun testOnItemClicked_ItemAlreadySelected_RemovesItemFromSelectedItems() {
        adapter.displayedItems = listOf(1, 2, 3)
        adapter.selectedItems.add(2)

        adapter.testOnClick(1)

        assertFalse(adapter.selectedItems.contains(2))
        verify(adapter).notifyItemChanged(1)
    }

    @Test
    fun testOnItemClicked_ItemNotAlreadySelected_AddsItemToSelectedItems() {
        adapter.displayedItems = listOf(1, 2, 3)
        adapter.selectedItems.add(2)

        adapter.testOnClick(2)

        assertTrue(adapter.selectedItems.contains(3))
        verify(adapter).notifyItemChanged(2)
    }

    @Test
    fun testOnItemClicked_ItemNotAlreadySelected_MaxSelectionsReached_DoesNotAddItemToSelectedItems() {
        adapter.displayedItems = listOf(1, 2, 3)
        adapter.selectedItems.addAll(listOf(1, 2))

        adapter.testOnClick(2)

        assertFalse(adapter.selectedItems.contains(3))
        verify(adapter, never()).notifyItemChanged(any())
    }

    private class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /** Implementation which specified custom sort & filter behaviour. */
    private class TestEnhancedAdapter : EnhancedAdapter<Int, TestViewHolder>(TestDiffCallback(), 2) {

        override fun compareItems(lhs: Int, rhs: Int): Int {
            // Sort in descending order.
            return rhs.compareTo(lhs)
        }

        override fun testItem(item: Int, query: String): Boolean {
            // Filter out items above 2.
            return item <= 2
        }

        fun testOnClick(adapterPosition: Int) {
            // Can't directly access onItemClicked() because it's protected.
            onItemClicked(adapterPosition)
        }

        fun testGetItemAt(position: Int): Int {
            // Can't directly access getItemAt() because it's protected.
            return getItemAt(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            throw RuntimeException("Stub")
        }

        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            throw RuntimeException("Stub")
        }
    }

    /** Minimal implementation which weakens method visibility for testability. */
    private class SimpleTestEnhancedAdapter : EnhancedAdapter<Int, TestViewHolder>(TestDiffCallback(), 2) {

        public override fun compareItems(lhs: Int, rhs: Int): Int {
            return super.compareItems(lhs, rhs)
        }

        public override fun testItem(item: Int, query: String): Boolean {
            return super.testItem(item, query)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            throw RuntimeException("Stub")
        }

        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            throw RuntimeException("Stub")
        }
    }

    /** Simply checks if two integers are equal. */
    private class TestDiffCallback : DiffUtil.ItemCallback<Int>() {

        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return true
        }
    }
}
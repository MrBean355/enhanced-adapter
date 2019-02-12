# Enhanced Adapter

[![Build Status](https://travis-ci.org/MrBean355/enhanced-adapter.svg?branch=master)](https://travis-ci.org/MrBean355/enhanced-adapter)

This is a `RecyclerView.Adapter` extension which provides the functionality:
- Item sorting; e.g. alphabetically.
- Item filtering; filtering the list based on a search query.
- Item selection; facilitates single & multi-selection of items.
- Uses [`DiffUtil`](https://developer.android.com/reference/android/support/v7/util/DiffUtil) behind the scenes. This means:
    - Changes in the list are animated (e.g. items slide up when one is removed).
    - Changes are efficient; only changed items are rebound (i.e. no `notifyDataSetChanged()`).
    - No UI freezes; list change calculations are done in a background thread.
    
## Usage
The library is published to Maven Central, so ensure you have the following in your top-level `build.gradle`:

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependency to your app-level `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.mrbean355:enhanced-adapter:$latest_version'
}
```

Finally, you can create an adapter which extends `com.github.mrbean355.android.EnhancedAdapter` instead of the usual `RecyclerView.Adapter`.

#### Minimal Usage
```kotlin
class MyAdapter : EnhancedAdapter<MyItem, MyAdapter.MyViewHolder>(
    /* class that checks if items have changed */ MyDiffCallbacks(),
    /* max number of selections */                0) {

    /** Normal onCreateViewHolder() stuff */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    /** Normal onBindViewHolder() stuff */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItemAt(position)
        holder.textView.text = item.name
    }

    /** Normal view holder stuff */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView as TextView
    }

    /**
    * Callback for calculating the diff between two items in a list.
    * 
    * See: https://developer.android.com/reference/android/support/v7/util/DiffUtil.ItemCallback
    */
    class MyDiffCallbacks : DiffUtil.ItemCallback<MyItem>() {

        override fun areItemsTheSame(oldItem: MyItem, newItem: MyItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MyItem, newItem: MyItem): Boolean {
            return true
        }
    }
}
```

#### Optional Extras
###### Sorting
```kotlin
class MyAdapter : EnhancedAdapter<MyItem, MyAdapter.MyViewHolder>(MyDiffCallbacks(), 0) {
    // Minimal stuff omitted...
    
    /** Customise how items are compared. */
    override fun compareItems(lhs: MyItem, rhs: MyItem): Int {
        return lhs.name.compareTo(rhs.name)
    }
}
```

###### Filtering
```kotlin
class MyAdapter : EnhancedAdapter<MyItem, MyAdapter.MyViewHolder>(MyDiffCallbacks(), 0) {
    // Minimal stuff omitted...
    
    /** Customise how items are tested. */
    override fun testItem(item: MyItem, query: String): Boolean {
        return item.name.contains(query, ignoreCase = true)
    }
}
```

###### Single Selection
```kotlin
class MyAdapter : EnhancedAdapter<MyItem, MyAdapter.MyViewHolder>(MyDiffCallbacks(), 0) {
    // Minimal stuff omitted...
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            // Your action here.
        }
    }
}
```

###### Multi-selection
```kotlin
class MyAdapter : EnhancedAdapter<MyItem, MyAdapter.MyViewHolder>(MyDiffCallbacks(), 5) {
    // Pass the max number of selection in the constructor (above).
    
    // Minimal stuff omitted...
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isSelected = isItemSelected(position)
        val context = holder.itemView.context
        // Do something to the item to indicate its selection status:
        if (isSelected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        }
        holder.itemView.setOnClickListener {
            // Notify that an item has been selected/unselected:
            onItemClicked(holder.adapterPosition)
        }
    }
}
```
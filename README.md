# Async RecyclerView Adapter

This is a `RecyclerView.Adapter` extension which provides the functionality:
- Item sorting; e.g. alphabetically.
- Item filtering; filtering the list based on a search query.
- Item selection; facilitates single & multi-selection of items.
- Uses [`DiffUtil`](https://developer.android.com/reference/android/support/v7/util/DiffUtil) behind the scenes. This means:
    - Changes in the list are animated (e.g. items slide up when one is removed).
    - Changes are efficient; only changed items are rebound (i.e. no `notifyDataSetChanged()`).
    - No UI freezes; list change calculations are done in a background thread.
    
## Usage

Simply extend `AsyncRecyclerViewAdapter` instead of the usual `RecyclerView.Adapter`:

```kotlin
class MyAdapter : AsyncRecyclerViewAdapter<MyItem, MyAdapter.MyViewHolder>(
    MyDiffCallbacks() /* class that checks if items have changed */,
    5                 /* max number of selections */ ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItemAt(position)
        holder.textView.text = item.name
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView as TextView
    }

    class MyDiffCallbacks : DiffUtil.ItemCallback<MyItem>() {

        override fun areItemsTheSame(oldItem: MyItem, newItem: MyItem): Boolean {
            /* check whether two objects represent the same item  */
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MyItem, newItem: MyItem): Boolean {
            /* check whether two items have the same data */
            return true
        }
    }
}
```
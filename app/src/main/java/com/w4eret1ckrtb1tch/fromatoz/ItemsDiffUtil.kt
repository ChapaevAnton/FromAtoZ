package com.w4eret1ckrtb1tch.fromatoz

import androidx.recyclerview.widget.DiffUtil

class ItemsDiffUtil(
    private val oldList: List<Item>,
    private val newList: List<Item>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem::class != newItem::class) return false
        return getItemCallback(oldItem).areItemsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem::class != newItem::class) return false
        return getItemCallback(oldItem).areContentsTheSame(oldItem, newItem)
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem::class != newItem::class) return false
        return getItemCallback(oldItem).getChangePayload(oldItem, newItem)
    }

    private fun getItemCallback(
        item: Item
    ): DiffUtil.ItemCallback<Item> = when (item) {
        is Item.Contact -> ItemsAdapter.ContactHolder.getDiffUtil() as DiffUtil.ItemCallback<Item>
        is Item.Header -> ItemsAdapter.LabelHolder.getDiffUtil() as DiffUtil.ItemCallback<Item>
    }
}
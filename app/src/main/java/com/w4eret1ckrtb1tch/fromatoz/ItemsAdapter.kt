package com.w4eret1ckrtb1tch.fromatoz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.w4eret1ckrtb1tch.fromatoz.Item.Contact
import com.w4eret1ckrtb1tch.fromatoz.Item.Header
import com.w4eret1ckrtb1tch.fromatoz.databinding.ItemContactBinding
import com.w4eret1ckrtb1tch.fromatoz.databinding.ItemHeaderBinding

class ItemsAdapter(
    private val onAlphabetClickListener: ((header: Header, selectPosition: Int) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<Item> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun selectMarker(selectPosition: Int) {
        items = items
            .asSequence()
            .mapIndexed { index, item ->
                (item as Header).copy(isSelected = index == selectPosition)
            }
            .toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.item_contact -> ContactHolder.create(parent)
            R.layout.item_header -> LabelHolder.create(parent, onAlphabetClickListener)
            else -> throw IllegalArgumentException("Illegal type $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContactHolder -> holder.bind(items[position] as Contact)
            is LabelHolder -> holder.bind(items[position] as Header)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is Contact -> R.layout.item_contact
            is Header -> R.layout.item_header
        }

    class ContactHolder private constructor(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) =
            with(binding) {
                val fullName = "${contact.lastName} ${contact.firstName}"
                title.text = fullName
                avatar.setImageResource(contact.resId)
            }

        companion object {

            fun create(parent: ViewGroup): ContactHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemContactBinding.inflate(inflater, parent, false)
                return ContactHolder(binding)
            }
        }
    }

    class LabelHolder private constructor(
        private val binding: ItemHeaderBinding,
        private val onAlphabetClickListener: ((header: Header, selectPosition: Int) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: Header) = with(binding) {
            tvLabel.text = header.label.uppercase()

            if (header.isSelected) {
                flLabel.setBackgroundColor(
                    ContextCompat.getColor(
                        flLabel.context,
                        android.R.color.holo_red_light
                    )
                )
            } else {
                flLabel.setBackgroundColor(
                    ContextCompat.getColor(
                        flLabel.context,
                        android.R.color.holo_blue_light
                    )
                )
            }

            flLabel.setOnClickListener {
                onAlphabetClickListener?.invoke(header, adapterPosition)
            }
        }

        companion object {

            fun create(
                parent: ViewGroup,
                onAlphabetClickListener: ((header: Header, selectPosition: Int) -> Unit)?
            ): LabelHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemHeaderBinding.inflate(inflater, parent, false)
                return LabelHolder(binding, onAlphabetClickListener)
            }
        }
    }
}
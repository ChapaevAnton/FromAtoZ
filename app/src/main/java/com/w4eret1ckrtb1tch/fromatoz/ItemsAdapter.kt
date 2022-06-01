package com.w4eret1ckrtb1tch.fromatoz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.w4eret1ckrtb1tch.fromatoz.Item.Contact
import com.w4eret1ckrtb1tch.fromatoz.Item.Header
import com.w4eret1ckrtb1tch.fromatoz.databinding.ItemContactBinding
import com.w4eret1ckrtb1tch.fromatoz.databinding.ItemHeaderBinding

class ItemsAdapter(
    private val onAlphabetClickListener: ((header: Header, selectPosition: Int) -> Unit)? = null,
    private val onContactClickListener: ((contact: Contact) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<Item> = emptyList()
        set(value) {
            val newList = value.toList()
            val diffUtil = ItemsDiffUtil(value, newList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            field = newList
            diffResult.dispatchUpdatesTo(this)
        }

    fun selectMarker(selectPosition: Int) {
        val newList = items
            .asSequence()
            .mapIndexed { index, item ->
                (item as Header).copy(isSelected = index == selectPosition)
            }
            .toList()
        val diffUtil = ItemsDiffUtil(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteContact(contact: Contact) {
        val newList = items.toMutableList()
        newList.remove(contact)
        val diffUtil = ItemsDiffUtil(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.item_contact -> ContactHolder.create(parent, onContactClickListener)
            R.layout.item_header -> LabelHolder.create(parent, onAlphabetClickListener)
            else -> throw IllegalArgumentException("Illegal type $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContactHolder -> holder.bind(items[position] as Contact)
            is LabelHolder -> holder.bind(items[position] as Header)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (holder is LabelHolder && payloads.isNotEmpty()) {
            holder.bind(items[position] as Header, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is Contact -> R.layout.item_contact
            is Header -> R.layout.item_header
        }

    class ContactHolder private constructor(
        private val binding: ItemContactBinding,
        private val onContactClickListener: ((contact: Contact) -> Unit)? = null
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var contact: Contact

        init {
            binding.contactContainer.setOnClickListener {
                onContactClickListener?.invoke(contact)

            }
        }

        fun bind(contact: Contact) = with(binding) {
            this@ContactHolder.contact = contact
            val fullName = "${contact.lastName} ${contact.firstName}"
            title.text = fullName
            avatar.setImageResource(contact.resId)
        }

        companion object {

            fun create(
                parent: ViewGroup,
                onContactClickListener: ((contact: Contact) -> Unit)? = null
            ): ContactHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemContactBinding.inflate(inflater, parent, false)
                return ContactHolder(binding, onContactClickListener)
            }

            fun getDiffUtil() = object : DiffUtil.ItemCallback<Contact>() {
                override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean =
                    oldItem == newItem

            }
        }

    }

    class LabelHolder private constructor(
        private val binding: ItemHeaderBinding,
        private val onAlphabetClickListener: ((header: Header, selectPosition: Int) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var header: Header

        init {
            binding.flLabel.setOnClickListener {
                onAlphabetClickListener?.invoke(header, adapterPosition)
            }
        }

        fun bind(header: Header) = with(binding) {
            this@LabelHolder.header = header
            tvLabel.text = header.label.uppercase()
            flLabel.setBackgroundColor(header.isSelected)
        }

        fun bind(
            header: Header,
            payloads: List<Any>
        ) = with(binding) {
            this@LabelHolder.header = header
            val isSelected = payloads.last() as Boolean
            flLabel.setBackgroundColor(isSelected)
        }

        private fun ViewGroup.setBackgroundColor(isSelected: Boolean) {
            val background = if (isSelected) {
                ContextCompat.getColor(
                    context,
                    android.R.color.holo_red_light
                )
            } else {
                ContextCompat.getColor(
                    context,
                    android.R.color.holo_blue_light
                )
            }
            setBackgroundColor(background)
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

            fun getDiffUtil() = object : DiffUtil.ItemCallback<Header>() {
                override fun areItemsTheSame(oldItem: Header, newItem: Header): Boolean =
                    oldItem.label == newItem.label

                override fun areContentsTheSame(oldItem: Header, newItem: Header): Boolean =
                    oldItem == newItem

                override fun getChangePayload(oldItem: Header, newItem: Header): Any? {
                    if (oldItem.isSelected != newItem.isSelected) return newItem.isSelected
                    return super.getChangePayload(oldItem, newItem)
                }
            }
        }
    }
}
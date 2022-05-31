package com.w4eret1ckrtb1tch.fromatoz

import android.content.Context
import android.widget.FrameLayout
import com.w4eret1ckrtb1tch.fromatoz.databinding.ItemHeaderBinding

class HeaderHolder(context: Context) : FrameLayout(context) {

    private val binding: ItemHeaderBinding =
        ItemHeaderBinding.bind(inflate(context, R.layout.item_header, this))

    fun setLabel(label: Char) {
        binding.tvLabel.text = label.uppercase()
    }
}
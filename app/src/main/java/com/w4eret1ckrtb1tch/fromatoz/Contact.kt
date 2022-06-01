package com.w4eret1ckrtb1tch.fromatoz

import androidx.annotation.DrawableRes
import kotlin.random.Random

sealed class Item {

    data class Contact(
        @DrawableRes val resId: Int,
        val firstName: String,
        val lastName: String
    ) : Item() {
        val id: Long = Random(42).nextLong()
    }

    data class Header(
        val label: Char,
        val isSelected: Boolean = false
    ) : Item()
}



package com.w4eret1ckrtb1tch.fromatoz

import androidx.annotation.DrawableRes

sealed class Item {

    data class Contact(
        val id: Long,
        @DrawableRes val resId: Int,
        val firstName: String,
        val lastName: String
    ) : Item()

    data class Header(
        val label: Char,
        val isSelected: Boolean = false
    ) : Item()
}



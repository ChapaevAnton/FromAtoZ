package com.w4eret1ckrtb1tch.fromatoz

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemsSwipeToDelete(
    private val onContactSwipeListener: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {

    private val childBackgroundPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onContactSwipeListener(viewHolder.adapterPosition)
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = if (viewHolder.itemViewType != R.layout.item_contact) {
        ItemTouchHelper.ACTION_STATE_IDLE
    } else {
        super.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.3F

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        //background
        val itemView = viewHolder.itemView
        val background = RectF(
            itemView.left.toFloat(),
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
        canvas.drawRect(background, childBackgroundPaint)
        //icon
        val icon =
            ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_baseline_delete_24)
                ?.toBitmap()
        icon?.let {
            val itemViewCenter =
                itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height) / 2
            val itemViewPadding = (itemView.right.toFloat() - icon.width) - (icon.width / 2)
            canvas.drawBitmap(
                icon,
                itemViewPadding,
                itemViewCenter,
                childBackgroundPaint
            )
        }
    }
}
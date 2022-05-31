package com.w4eret1ckrtb1tch.fromatoz

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemsHeaderDecorator(
    private val context: Context,
    private val getItems: () -> List<Item.Contact>
) : RecyclerView.ItemDecoration() {

    private val dividerHeight = dipToPx(0.8F)
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.parseColor("#ff0000")
    }

    private val sectionItemWidth: Int by lazy {
        getScreenWidth()
    }

    private val sectionItemHeight: Int by lazy {
        dipToPx(50F)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val layoutManager = parent.layoutManager
        if (layoutManager !is LinearLayoutManager) return
        if (layoutManager.orientation != LinearLayoutManager.VERTICAL) return

        val list = getItems()
        if (list.isEmpty()) return

        val position = parent.getChildLayoutPosition(view)
        if (position == 0) {
            outRect.top = sectionItemHeight
            return
        }

        val currentModel = getItems()[position]
        val previousModel = getItems()[position - 1]
        if (currentModel.lastName.first() != previousModel.lastName.first()) {
            outRect.top = sectionItemHeight
        } else {
            outRect.top = dividerHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val childView: View = parent.getChildAt(i)
            val position: Int = parent.getChildAdapterPosition(childView)
            val itemModel = getItems()[position]

            if (getItems().isNotEmpty() &&
                (position == 0 || itemModel.lastName.first() != getItems()[position - 1].lastName.first())
            ) {
                val top = childView.top - sectionItemHeight
                drawSectionView(c, itemModel.lastName.first(), top)
            } else {
                drawDivider(c, childView)
            }
        }
    }

    private fun drawDivider(canvas: Canvas, childView: View) {
        canvas.drawRect(
            0f,
            (childView.top - dividerHeight).toFloat(),
            childView.right.toFloat(),
            childView.top.toFloat(),
            dividerPaint
        )
    }

    private fun drawSectionView(canvas: Canvas, label: Char, top: Int) {
        val view = HeaderHolder(context)
        view.setLabel(label)

        val bitmap = getViewGroupBitmap(view)
        val bitmapCanvas = Canvas(bitmap)
        view.draw(bitmapCanvas)

        canvas.drawBitmap(bitmap, 0F, top.toFloat(), null)
    }

    private fun getViewGroupBitmap(viewGroup: ViewGroup): Bitmap {
        val layoutParams = ViewGroup.LayoutParams(sectionItemWidth, sectionItemHeight)
        viewGroup.layoutParams = layoutParams

        viewGroup.measure(
            View.MeasureSpec.makeMeasureSpec(sectionItemWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(sectionItemHeight, View.MeasureSpec.EXACTLY)
        )
        viewGroup.layout(0, 0, sectionItemWidth, sectionItemHeight)

        val bitmap = Bitmap.createBitmap(viewGroup.width, viewGroup.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        viewGroup.draw(canvas)

        return bitmap
    }

    private fun dipToPx(dpValue: Float): Int =
        (dpValue * context.resources.displayMetrics.density).toInt()

    private fun getScreenWidth(): Int {
        val outMetric = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(outMetric)
        } else {
            val display =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            display.getMetrics(outMetric)
        }
        return outMetric.widthPixels
    }
}

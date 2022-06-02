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
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemsHeaderDecorator(
    private val context: Context,
    heightDipDivider: Float = DEFAULT_DIVIDER_HEIGHT,
    @ColorRes colorResDivider: Int? = null,
    private val getItems: () -> List<Item.Contact>
) : RecyclerView.ItemDecoration() {

    //высота разделителя
    private val dividerHeight: Int by lazy { fromDipToPx(heightDipDivider) }

    //цвет разделителя
    private val dividerPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorResDivider?.let {
                ContextCompat.getColor(context, it)
            } ?: Color.TRANSPARENT
        }
    }

    //ширина элемента "заголовок"
    private val headerWidth: Int by lazy {
        getScreenWidthPx()
    }

    //высота элемента "заголовок"
    private val headerHeight: Int by lazy {
        fromDipToPx(HEADER_HEIGHT)
    }

    //получаем смещение для размещения "загловков" или "разделителя"
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //проверка установлен ли вертикальный линейный менеджер
        val layoutManager = parent.layoutManager
        if (layoutManager !is LinearLayoutManager) return
        if (layoutManager.orientation != LinearLayoutManager.VERTICAL) return
        //проверка не пустой ли список элементов "данных"
        if (getItems().isEmpty()) return
        //получаем позицию в адаптере данного view
        val position = parent.getChildAdapterPosition(view)
            .takeIf { it != RecyclerView.NO_POSITION }
            ?: parent.getChildViewHolder(view).oldPosition
        //если элемент - это заголовок
        if (isHeaderItem(position)) {
            //то задаем высоту для "заголовка"
            outRect.top = headerHeight
        } else {
            //то задаем высоту для "разделителя" элементов
            outRect.top = dividerHeight
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        //проверка не пустой ли список элементов "данных"
        if (getItems().isEmpty()) return
        //перебираем view списка
        for (i in 0 until parent.childCount) {
            //получаем view по текущему индексу
            val view = parent.getChildAt(i)
            //получаем позицию в адаптере данного view
            val position = parent.getChildAdapterPosition(view)
                .takeIf { it != RecyclerView.NO_POSITION } ?: return
            //если элемент - это заголовок
            if (isHeaderItem(position)) {
                //отрисовываем заголовок
                drawHeader(canvas, view.top, position)
            } else {
                //иначе отрисовываем разделитель
                drawDivider(canvas, view)
            }
        }
    }

    //отрисовка разделителя
    private fun drawDivider(canvas: Canvas, childView: View) {
        canvas.drawRect(
            0F,
            (childView.top - dividerHeight).toFloat(),
            childView.right.toFloat(),
            childView.bottom.toFloat(),
            dividerPaint
        )
    }

    //отрисовка "заголовка"
    private fun drawHeader(canvas: Canvas, viewTop: Int, position: Int) {
        //создаем элемент "заголовка"
        val view = HeaderHolder(context)
        //устанавливаем название "заголовка" по полученным "данным" из модели - берем первую букву фамилии
        view.setLabel(getItems()[position].lastName.first())
        //конвертируем viewGroup "заголовка" в bitmap изображение
        val bitmap = getViewGroupBitmap(view)
        //задаем верхнюю точку от которой будет отрисовываться
        // "заголовок", за разницей размера высоты самого "заголовка" - тем самы
        // выделяя область для отрисовки "заголовка"
        val top = viewTop - headerHeight.toFloat()
        //отрисовываем полученное изображения "заголовка" на холсте элемента
        canvas.drawBitmap(bitmap, 0F, top, null)
        //перерисовываем view
        view.draw(Canvas(bitmap))
    }

    //конвертация viewGroup в bitmap
    private fun getViewGroupBitmap(viewGroup: ViewGroup): Bitmap {
        //устанавливаем размер viewGroup
        val layoutParams = ViewGroup.LayoutParams(headerWidth, headerHeight)
        viewGroup.layoutParams = layoutParams
        //вычислении требований к размеру текущего viewGroup и всех его вложенных view
        viewGroup.measure(
            View.MeasureSpec.makeMeasureSpec(headerWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(headerHeight, View.MeasureSpec.EXACTLY)
        )
        //выделяем требуемый размер во viewGroup для дочерних view
        viewGroup.layout(0, 0, headerWidth, headerHeight)
        //создаем bitmap изображение
        val bitmap = Bitmap.createBitmap(viewGroup.width, viewGroup.height, Bitmap.Config.ARGB_8888)
        //перерисовываем viewGroup
        viewGroup.draw(Canvas(bitmap))
        return bitmap
    }

    //преобразование dp в px
    private fun fromDipToPx(dipValue: Float): Int =
        (dipValue * context.resources.displayMetrics.density).toInt()

    //получение ширины экрана в px
    private fun getScreenWidthPx(): Int {
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

    //если это 0 позиция (первая) ИЛИ если текущий элемент не из группы предыдущего - по первой букве фамилии) то это "заголовок"
    private fun isHeaderItem(position: Int): Boolean {
        if (position == 0) return true
        val current = getItems()[position].lastName.first()
        val previous = getItems()[position.minus(1)].lastName.first()
        return current != previous
    }

    private companion object {
        const val DEFAULT_DIVIDER_HEIGHT = 0.8F
        const val HEADER_HEIGHT = 50F
    }
}

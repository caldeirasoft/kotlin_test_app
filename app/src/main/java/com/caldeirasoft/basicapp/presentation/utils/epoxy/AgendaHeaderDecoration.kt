package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.graphics.Typeface.BOLD
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.res.*
import androidx.core.graphics.withTranslation
import androidx.core.text.inSpans
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.caldeirasoft.basicapp.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter


class AgendaHeaderDecoration<in T : EpoxyModel<*>>(
        private val context: Context,
        private val epoxyController: EpoxyController,
        private val models: List<EpoxyModel<*>>,
        private val headerClass: Class<T>,
        private val dateConverter: (T) -> LocalDate?
) : RecyclerView.ItemDecoration() {
    private val paint: TextPaint
    private val width: Int
    private val paddingTop: Int
    private val dateFormatter = DateTimeFormatter.ofPattern("d")
    private val dayFormatter = DateTimeFormatter.ofPattern("eee")
    private val dayTextSize: Int

    init {
        val attrs = context.obtainStyledAttributes(
                R.style.Widget_MaterialComponents_DateHeaders,
                R.styleable.DateHeader
        )
        paint = TextPaint(ANTI_ALIAS_FLAG).apply {
            color = attrs.getColorOrThrow(R.styleable.DateHeader_android_textColor)
            textSize = attrs.getDimensionOrThrow(R.styleable.DateHeader_dateTextSize)
            try {
                typeface = ResourcesCompat.getFont(
                        context,
                        attrs.getResourceIdOrThrow(R.styleable.DateHeader_android_fontFamily)
                )
            } catch (_: Exception) {
                // ignore
            }
        }
        width = attrs.getDimensionPixelSizeOrThrow(R.styleable.DateHeader_android_width)
        paddingTop = attrs.getDimensionPixelSizeOrThrow(R.styleable.DateHeader_android_paddingTop)
        dayTextSize = attrs.getDimensionPixelSizeOrThrow(R.styleable.DateHeader_dayTextSize)
        attrs.recycle()
    }

    // Get the block index:day and create header layouts for each
    private val daySlots: Map<Int, StaticLayout> =
            indexAgendaHeaders()
                    .map {
                        it.key to createHeader(it.value)
                    }.toMap()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position <= 0) return

        if (daySlots.containsKey(position)) {
            // first block of day, pad top
            outRect.top = paddingTop
        } else if (daySlots.containsKey(position + 1)) {
            // last block of day, pad bottom
            outRect.bottom = paddingTop
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (daySlots.isEmpty() || parent.isEmpty()) return

        var earliestFoundHeaderPos = -1
        var prevHeaderTop = Int.MAX_VALUE

        // Loop over each attached view looking for header items.
        // Loop backwards as a lower header can push another higher one upward.
        for (i in parent.childCount - 1 downTo 0) {
            val view = parent[i]
            val viewTop = view.top + view.translationY.toInt()
            if (view.bottom > 0 && viewTop < parent.height) {
                val position = parent.getChildAdapterPosition(view)
                daySlots[position]?.let { layout ->
                    paint.alpha = (view.alpha * 255).toInt()
                    val top = (viewTop + paddingTop)
                            .coerceAtLeast(paddingTop)
                            .coerceAtMost(prevHeaderTop - layout.height)
                    c.withTranslation(y = top.toFloat()) {
                        layout.draw(c)
                    }
                    earliestFoundHeaderPos = position
                    prevHeaderTop = viewTop - paddingTop - paddingTop
                }
            }
        }

        // If no headers found, ensure header of the first shown item is drawn.
        if (earliestFoundHeaderPos < 0) {
            earliestFoundHeaderPos = parent.getChildAdapterPosition(parent[0]) + 1
        }

        // Look back over headers to see if a prior item should be drawn sticky.
        for (headerPos in daySlots.keys.reversed()) {
            if (headerPos < earliestFoundHeaderPos) {
                daySlots[headerPos]?.let { layout ->
                    val top = (prevHeaderTop - layout.height).coerceAtMost(paddingTop)
                    c.withTranslation(y = top.toFloat()) {
                        layout.draw(c)
                    }
                }
                break
            }
        }
    }

    /**
     * Create a header layout for the given [day]
     */
    private fun createHeader(day: LocalDate): StaticLayout {
        val text = SpannableStringBuilder(dateFormatter.format(day)).apply {
            append(System.lineSeparator())
            inSpans(AbsoluteSizeSpan(dayTextSize), StyleSpan(BOLD)) {
                append(dayFormatter.format(day).toUpperCase())
            }
        }
        return StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
    }

    /**
     * Find the first block of each day (rounded down to nearest day) and return pairs of
     * index to start time. Assumes that [agendaItems] are sorted by ascending start time.
     */
    fun indexAgendaHeaders(agendaItems: List<LocalDateTime>): List<Pair<Int, LocalDateTime>> {
        return agendaItems
                .mapIndexed { index, date -> index to date }
                .distinctBy { it.second.dayOfMonth }
    }

    /**
     * Find the first block of each day (rounded down to nearest day) and return pairs of
     * index to start time. Assumes that [agendaItems] are sorted by ascending start time.
     */
    fun indexAgendaHeaders(): Map<Int, LocalDate> {
        val m1 = models
        val m2 = m1.mapIndexed { index, epoxyModel -> index to epoxyModel }
        val m3 = m2.filter { kvp -> kvp.second.javaClass == headerClass }
        val m4 = m3.map { pair -> pair.first to dateConverter(pair.second as T) }
        val m5 = m4.distinctBy { it.second?.dayOfMonth }
        val mapIndex = models
                .mapIndexed { index, epoxyModel -> index to epoxyModel }
                .filter { kvp -> kvp.second.javaClass == headerClass }
                .map { pair -> pair.first to dateConverter(pair.second as T) }
                .map { pair -> pair.second?.run { pair.first to pair.second!! } }
                .filterNotNull()
                .distinctBy { it.second.dayOfMonth }
                .toMap()
        return mapIndex
    }
}
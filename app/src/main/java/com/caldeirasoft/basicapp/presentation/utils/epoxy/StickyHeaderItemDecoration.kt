package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import java.util.*


class StickyHeaderItemDecoration<T : EpoxyModel<*>>(
        private val epoxyController: EpoxyController,
        private val headerClass: Class<T>
) : RecyclerView.ItemDecoration() {
    private var mStickyHeaderHeight: Int = 0

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (parent !is EpoxyRecyclerView) {
            throw InputMismatchException("This Item Decoration must be used with EpoxyRecyclerView")
        }

        val topChild = parent.getChildAt(0) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }

        val headerPos = getHeaderPositionForItem(topChildPosition)
        if (headerPos != RecyclerView.NO_POSITION) {
            val currentHeader = getHeaderViewForItem(headerPos, parent)
            fixLayoutSize(parent, currentHeader)
            val contactPoint = currentHeader.bottom
            val childInContact = getChildInContact(parent, contactPoint, headerPos)

            if (childInContact != null && isHeader(parent.getChildAdapterPosition(childInContact))) {
                moveHeader(c, currentHeader, childInContact)
                return
            }

            drawHeader(c, currentHeader)
        }
    }

    private fun getHeaderViewForItem(headerPosition: Int, parent: EpoxyRecyclerView): View {

        val viewHolder = epoxyController.adapter.onCreateViewHolder(
                parent,
                epoxyController.adapter.getItemViewType(headerPosition)
        )
        epoxyController.adapter.onBindViewHolder(viewHolder, headerPosition)
        return viewHolder.itemView
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0f, (nextHeader.top - currentHeader.height).toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    private fun isHeader(itemPosition: Int): Boolean {
        if (itemPosition != RecyclerView.NO_POSITION) {
            val model = epoxyController.adapter.getModelAtPosition(itemPosition)
            val isHeaderType = (model.javaClass == headerClass)
            return isHeaderType
        }
        return false
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int, currentHeaderPos: Int): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            var heightTolerance = 0
            val child = parent.getChildAt(i)

            //measure height tolerance with child if child is another header
            if (currentHeaderPos != i) {
                val isChildHeader = isHeader(parent.getChildAdapterPosition(child))
                if (isChildHeader) {
                    heightTolerance = mStickyHeaderHeight - child.height
                }
            }

            //add heightTolerance if child top be in display area
            val childBottomPosition = if (child.top > 0) {
                child.bottom + heightTolerance
            } else {
                child.bottom
            }

            if (childBottomPosition > contactPoint) {
                if (child.top <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }


    /**
     * This method gets called by [StickyHeaderItemDecoration] to fetch the position of the header item in the adapter
     * that is used for (represents) item at specified position.
     * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
     * @return int. Position of the header item in the adapter.
     */
    private fun getHeaderPositionForItem(itemPosition: Int): Int {
        var tempPosition = itemPosition
        var headerPosition = RecyclerView.NO_POSITION
        do {
            if (isHeader(tempPosition)) {
                headerPosition = tempPosition
                break
            }
            tempPosition -= 1
        } while (tempPosition >= -1)
        return headerPosition
    }

    /**
     * Properly measures and layouts the top sticky header.
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec =
                ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)
        mStickyHeaderHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
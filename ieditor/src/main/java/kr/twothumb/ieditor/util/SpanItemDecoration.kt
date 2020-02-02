package kr.twothumb.ieditor.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpanItemDecoration(private val spanCount: Int, private val spacing:Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        outRect.right =
            spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
        outRect.left = column * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)


        outRect.left = (spacing - column * (1f / spanCount * spacing)).toInt()
        outRect.right = column * spacing / spanCount

        if (column == 0) {
            outRect.left = spacing
            outRect.right = spacing / 2
        } else if (column == spanCount - 1) {
            outRect.left = spacing / 2
            outRect.right = spacing
        } else {
            outRect.left = spacing / 2
            outRect.right = spacing / 2
        }

        if (position < spanCount) { // top edge
            outRect.top = spacing
        }
        outRect.bottom = spacing // item bottom
    }
}

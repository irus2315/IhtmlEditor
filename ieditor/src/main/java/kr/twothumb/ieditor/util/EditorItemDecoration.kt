package kr.twothumb.ieditor.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EditorItemDecoration(private val spacing:Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if(parent.getChildAdapterPosition(view)  == RecyclerView.NO_POSITION)
            return
        when (parent.getChildAdapterPosition(view)) {
            parent.adapter!!.itemCount - 1 -> {
                outRect.right = spacing
                outRect.left = spacing
                outRect.top = 0
                outRect.bottom = spacing
            }
            0 -> {
                outRect.right = spacing
                outRect.left = spacing
                outRect.top = spacing
                outRect.bottom = 0
            }
            else -> {
                outRect.right = spacing
                outRect.left = spacing
                outRect.top = 0
                outRect.bottom = 0
            }
        }
    }
}
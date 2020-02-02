package kr.twothumb.ieditor.util.view

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

class EditorText: EditText {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        onSelectionChangedListener?.onSelectionsChanged(selStart, selEnd)
    }

    var onSelectionChangedListener: OnSelectionChangedListener? = null

    interface OnSelectionChangedListener {
        fun onSelectionsChanged(selStart: Int, selEnd: Int)
    }
}
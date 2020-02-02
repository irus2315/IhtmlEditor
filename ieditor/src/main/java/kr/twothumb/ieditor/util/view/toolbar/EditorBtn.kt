package kr.twothumb.ieditor.util.view.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import kr.twothumb.ieditor.R

open class EditorBtn: FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("Recycle", "CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        resource = context.obtainStyledAttributes(attrs, R.styleable.ToolbarButton) .getResourceId(R.styleable.ToolbarButton_imageResource, 0)
    }
    private var resource: Int
    lateinit var btn: ToggleButton
    var img: ImageView? = null


    open val isChecked: Boolean
        get() {
            return btn.isChecked
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        View.inflate(context, R.layout.toolbar_button, this)

        btn = findViewById(R.id.btn)
        btn.background = null
        img = findViewById(R.id.img)
        img?.setImageResource(resource)
        setCheck(false)
    }

    fun setImageResource(resource:Int){
        this.resource = resource
    }

    fun onClick() {
        setCheck(!btn.isChecked)
    }

    fun setCheck(checked: Boolean) {
        btn.isChecked = checked
        alpha = if (checked) {
            1f
        } else {
            .6f
        }
    }
}

@BindingAdapter("bindImage")
fun setImageResource(editorBtn: EditorBtn, resource: Int) {
    editorBtn.setImageResource(resource)
}

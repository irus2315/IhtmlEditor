package kr.twothumb.ieditor

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_option_toolbar.btn1
import kotlinx.android.synthetic.main.layout_option_toolbar.view.*
import kr.twothumb.ieditor.databinding.ViewIeditorBinding
import kr.twothumb.ieditor.model.Align
import kr.twothumb.ieditor.model.FontSize
import kr.twothumb.ieditor.model.OptionToolbarStatus
import kr.twothumb.ieditor.ui.HtmlEditorViewModel
import kr.twothumb.ieditor.ui.adapter.HtmlAdapter
import kr.twothumb.ieditor.ui.imagepicker.ImagePickerViewModel

class IEditorView: ConstraintLayout {

    val bind:ViewIeditorBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val inflater:LayoutInflater  = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        bind = ViewIeditorBinding.inflate(inflater, this, true)
    }

    fun initViewModel(vm: HtmlEditorViewModel, imageVm: ImagePickerViewModel){
        bind.vm = vm
        bind.imageVm = imageVm
        bind.lifecycleOwner = getLifeCycleOwner(this)

        bind.vm?.observeFocus?.observe(getLifeCycleOwner(this), Observer<Int>{
            if(bind.editor.adapter != null)
                (bind.editor.adapter as HtmlAdapter).index = it

            if(it < bind.vm?.editorList?.size!!) {
                when (bind.vm?.editorList?.get(it)?.fontSize) {
                    FontSize.T3 -> bind.btnFontsize.img?.setImageResource(R.drawable.ic_si_font_small)
                    FontSize.T2 -> bind.btnFontsize.img?.setImageResource(R.drawable.ic_si_fontsize)
                    FontSize.T1 -> bind.btnFontsize.img?.setImageResource(R.drawable.ic_si_font_large)
                }
                when (bind.vm?.editorList?.get(it)?.align) {
                    Align.NONE -> bind.btnAlign.img?.setImageResource(R.drawable.ic_si_align_justify)
                    Align.LEFT -> bind.btnAlign.img?.setImageResource(R.drawable.ic_si_align_left)
                    Align.RIGHT -> bind.btnAlign.img?.setImageResource(R.drawable.ic_si_align_right)
                    Align.CENTER -> bind.btnAlign.img?.setImageResource(R.drawable.ic_si_align_center)
                }
            }
        })

        bind.vm?.observeOptionToolbarStatus?.observe(getLifeCycleOwner(this), Observer<OptionToolbarStatus>{
            if(it != null) {
                when (it) {
                    OptionToolbarStatus.FontSizeLayoutOpen -> {
                        bind.layoutFontsize.option.visibility = View.VISIBLE
                        bind.layoutAlign.option.visibility = View.GONE
                    }
                    OptionToolbarStatus.AlignLayoutOpen -> {
                        bind.layoutFontsize.option.visibility = View.GONE
                        bind.layoutAlign.option.visibility = View.VISIBLE
                    }
                    OptionToolbarStatus.Close -> {
                        bind.layoutFontsize.option.visibility = View.GONE
                        bind.layoutAlign.option.visibility = View.GONE
                    }
                }
            }
        })

        bind.layoutAlign.btn1.setOnClickListener {bind.vm?.onOptionBtnClick(it) }
        bind.layoutAlign.btn2.setOnClickListener { bind.vm?.onOptionBtnClick(it) }
        bind.layoutAlign.btn3.setOnClickListener { bind.vm?.onOptionBtnClick(it) }
        bind.layoutFontsize.btn1.setOnClickListener { bind.vm?.onOptionBtnClick(it) }
        bind.layoutFontsize.btn2.setOnClickListener { bind.vm?.onOptionBtnClick(it) }
        bind.layoutFontsize.btn3.setOnClickListener { bind.vm?.onOptionBtnClick(it) }

        bind.editor.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                (bind.editor.adapter as HtmlAdapter).scrollSate = newState

                if(newState == 0)
                    bind.vm?.observeFocus?.postValue((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
            }
        })

    }

    private fun getLifeCycleOwner(view: View): LifecycleOwner {
        var context = view.context

        while (context is ContextWrapper) {
            if (context is LifecycleOwner)
                return context
            context = context.baseContext
        }
        return context as LifecycleOwner
    }
}

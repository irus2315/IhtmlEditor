package kr.twothumb.ieditor.ui.adapter

import android.util.TypedValue
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.twothumb.ieditor.IEditorView

import kr.twothumb.ieditor.model.EditorData



import kr.twothumb.ieditor.model.ImgPickData

import kr.twothumb.ieditor.ui.HtmlEditorViewModel
import kr.twothumb.ieditor.ui.imagepicker.ImagePickerViewModel
import kr.twothumb.ieditor.util.EditorItemDecoration
import kr.twothumb.ieditor.util.SpanItemDecoration

@BindingAdapter(value = ["editor_vm", "editor_imgVm"])
fun initViewModel(view: IEditorView, vm: HtmlEditorViewModel, imageVm: ImagePickerViewModel){
    view.run {
        view.initViewModel(vm, imageVm)
        initHtmlAdapter(view.bind.editor, vm.editorList, vm, imageVm)
    }
}

@BindingAdapter(value = ["editor_init", "editor_viewModel", "editor_imgViewModel"])
fun initHtmlAdapter(view: RecyclerView, items: ArrayList<EditorData>, vm: HtmlEditorViewModel, imageVm: ImagePickerViewModel) {
    view.adapter?.run {
        if(this is HtmlAdapter){
            if(view.itemDecorationCount == 0)
                view.addItemDecoration(EditorItemDecoration(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16f, view.context.resources.displayMetrics).toInt()))
            this.items = items
            this.notifyDataSetChanged()
        }
    } ?: run{
        HtmlAdapter(items, vm, imageVm).apply { view.adapter = this }
    }
}

@BindingAdapter(value = ["imagepicker_init", "imagepicker_viewModel"])
fun initImagePickerAdapter(view: RecyclerView, items: ArrayList<ImgPickData>, vm: ImagePickerViewModel) {
    view.adapter?.run {
        if(this is ImgPickAdapter){
            if(view.itemDecorationCount == 0){
                view.addItemDecoration(SpanItemDecoration(3, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2f, view.context.resources.displayMetrics).toInt()))
            }
            this.items = items
            this.notifyDataSetChanged()
        }
    } ?: run{
        ImgPickAdapter(items, vm).apply { view.adapter = this }
    }
}

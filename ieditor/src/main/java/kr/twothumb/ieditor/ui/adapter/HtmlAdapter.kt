package kr.twothumb.ieditor.ui.adapter

import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kr.twothumb.ieditor.model.FontSize
import kr.twothumb.ieditor.ui.HtmlEditorViewModel
import kr.twothumb.ieditor.ui.imagepicker.ImagePickerViewModel
import kr.twothumb.ieditor.model.ContentType
import kr.twothumb.ieditor.util.BindingViewHolder
import kotlinx.android.synthetic.main.item_image_picker.view.*
import kotlinx.android.synthetic.main.item_text.view.*
import kr.twothumb.ieditor.IEditor
import kr.twothumb.ieditor.R
import kr.twothumb.ieditor.databinding.ItemImageBinding
import kr.twothumb.ieditor.databinding.ItemTextBinding
import kr.twothumb.ieditor.model.Align
import kr.twothumb.ieditor.model.EditorData
import java.util.ArrayList

class HtmlAdapter(var items:ArrayList<EditorData>,
                  private val vm: HtmlEditorViewModel,
                  private val imageVm: ImagePickerViewModel
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val text = 0
    private val image = 1
    var index = 0
    var scrollSate:Int = 0

    private val requestOptions:RequestOptions = RequestOptions()

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when(items[position].type){
            ContentType.TEXT -> text
            ContentType.IMAGE -> image
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        return when(viewType){
            text -> TextHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false))
            image -> ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))
            else -> TextHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false))
        }
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder, position: Int) {
        when(items[position].type){
            ContentType.TEXT -> bindText(holder as TextHolder, position)
            ContentType.IMAGE -> bindImage(holder as ImageHolder, position)
        }
    }

    private fun bindText(holder: TextHolder, position:Int) {
        holder.onTextWatcher.updatePosition(holder.adapterPosition)
        holder.onSelectionChangeListener.updatePosition(holder.adapterPosition)

        holder.binding.vm = vm
        holder.binding.edit.tag = position
        holder.binding.edit.text = items[holder.adapterPosition].setStyle()
        holder.binding.edit.hint = SpannableStringBuilder(items[holder.adapterPosition].hint)
        holder.binding.edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 0f)

        if (scrollSate == 0) {
            if (position == index) {
                if (!holder.binding.edit.isFocusable)
                    holder.binding.edit.isFocusable = true
                if (!holder.binding.edit.isFocusableInTouchMode)
                    holder.binding.edit.isFocusableInTouchMode = true

                if (!holder.binding.edit.hasFocus()) {
                    holder.binding.edit.requestFocus()
                }
                try {
                    holder.binding.edit.setSelection(items[holder.adapterPosition].currentFocus)
                } catch (e: IndexOutOfBoundsException) {
                    holder.binding.edit.setSelection(0)
                }
            }
        }
        else{
            holder.binding.edit.isFocusable = false
            holder.binding.edit.isFocusableInTouchMode = false
        }

        when(items[holder.adapterPosition].fontSize){
            FontSize.T1 -> holder.binding.edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
            FontSize.T2 -> holder.binding.edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            FontSize.T3 -> holder.binding.edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
        }

        when(items[holder.adapterPosition].align){
            Align.CENTER -> holder.binding.edit.gravity = Gravity.CENTER
            Align.RIGHT-> holder.binding.edit.gravity = Gravity.END
            Align.LEFT -> holder.binding.edit.gravity = Gravity.START
            else -> holder.binding.edit.gravity = Gravity.START
        }
    }

    private fun bindImage(holder: ImageHolder, position:Int){

        holder.binding.vm = vm
        holder.binding.view.tag = position
        Glide.with(IEditor.applicationContext).load(items[position].uri)
            .thumbnail(.3f)
            .apply(requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(holder.itemView.img as ImageView)

        if (position == index) {
            imageVm.itemSelectedAnimation(holder.binding.img)
            holder.binding.selector.visibility = View.VISIBLE
        }
        else{
            imageVm.clearColorFilter(holder.binding.img)
            holder.binding.selector.visibility = View.GONE
        }
    }

    inner class TextHolder(v: View) : BindingViewHolder<ItemTextBinding>(v){
        var onTextWatcher: HtmlEditorViewModel.EditWatcher = HtmlEditorViewModel.EditWatcher(v.edit, items)
        var onSelectionChangeListener: HtmlEditorViewModel.OnSelectionChangedListener = HtmlEditorViewModel.OnSelectionChangedListener(items, v.edit)
        init {
            v.edit.setOnKeyListener(vm.onKeyListener)
            v.edit.onFocusChangeListener = HtmlEditorViewModel.OnFocusChangeListener(vm)
            v.edit.addTextChangedListener(onTextWatcher)
            v.edit.onSelectionChangedListener = onSelectionChangeListener
        }
    }

    inner class ImageHolder(v: View) : BindingViewHolder<ItemImageBinding>(v)

}
package kr.twothumb.ieditor.ui.adapter

import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kr.twothumb.ieditor.ui.imagepicker.ImagePickerViewModel
import kr.twothumb.ieditor.util.BindingViewHolder
import kotlinx.android.synthetic.main.item_image_picker.view.*
import java.util.ArrayList
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import kr.twothumb.ieditor.R
import kr.twothumb.ieditor.databinding.ItemImageBinding
import kr.twothumb.ieditor.model.ImgPickData

class ImgPickAdapter(var items:ArrayList<ImgPickData>, private val vm: ImagePickerViewModel) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var factory:DrawableCrossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.item_image_picker, parent, false)
        v.img.layoutParams = vm.initSize(v)
        return ImagePickHolder(v)
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder, position: Int) {
        bindImage(holder as ImagePickHolder)
    }

    private fun bindImage(holder: ImagePickHolder){
        Glide.with(holder.v.context).load(items[holder.adapterPosition].imgUri)
            .transition(withCrossFade(factory))
            .thumbnail(.3f)
            .apply(
                RequestOptions().placeholder(R.drawable.ic_image_black_24dp).centerCrop())
            .into(holder.v.img as ImageView)

        if(items[holder.adapterPosition].isSelected){
            vm.itemSelectedAnimation(holder.v.img)
            holder.v.selector.background = holder.v.context.getDrawable(R.drawable.background_circle_selected)
            holder.v.selector.setImageResource(R.drawable.ic_check_black_24dp)
        }
        else{
            (holder.v.img as ImageView).clearColorFilter()
            holder.v.selector.background = holder.v.context.getDrawable(R.drawable.background_circle_nor)
            holder.v.selector.setImageResource(0)
        }

        holder.v.setOnClickListener {
            vm.itemClick(holder)
        }
    }

    class ImagePickHolder(var v: View) : BindingViewHolder<ItemImageBinding>(v)
}
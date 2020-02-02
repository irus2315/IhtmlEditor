package kr.twothumb.ieditor.ui.imagepicker

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import kr.twothumb.ieditor.R
import kr.twothumb.ieditor.model.ImgPickData
import kr.twothumb.ieditor.ui.adapter.ImgPickAdapter
import kr.twothumb.ieditor.util.NotNullMutableLiveData
import kr.twothumb.lib.logger.DevLog
import kotlin.collections.ArrayList


class ImagePickerViewModel : ViewModel() {

    val list: NotNullMutableLiveData<ArrayList<ImgPickData>> = NotNullMutableLiveData(arrayListOf())
    private val imageList:ArrayList<ImgPickData> = ArrayList()
    private lateinit var activity:Activity

    fun initActivity(activity:Activity){
        this.activity = activity
    }
    fun initSize(v:View):FrameLayout.LayoutParams{
        return FrameLayout.LayoutParams(
            (v.context.resources.displayMetrics.widthPixels / 3) - (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,2f, v.context.resources.displayMetrics).toInt()),
            (v.context.resources.displayMetrics.widthPixels / 3) - (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,2f, v.context.resources.displayMetrics).toInt())
        )
    }

    fun clearColorFilter(v:View){
        (v as ImageView).clearColorFilter()

    }
    fun itemSelectedAnimation(v:View){
        val colorTo = ContextCompat.getColor(v.context!!, R.color.colorNull)
        val colorFrom = ContextCompat.getColor(v.context!!, R.color.colorSelected)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
        colorAnimation.duration = 280 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            (v as ImageView).setColorFilter(animator.animatedValue as Int, PorterDuff.Mode.SRC_OVER)
        }
        colorAnimation.interpolator = DecelerateInterpolator()
        colorAnimation.start()
    }

    fun itemClick(holder: ImgPickAdapter.ImagePickHolder){

        var isCanceled = false
        for((i) in imageList.withIndex()){

            if(i == holder.adapterPosition && imageList[holder.adapterPosition].isSelected){
                isCanceled = true
                imageList[i].isSelected = false
            }
            if(imageList[i].isSelected) {
                imageList[i].isSelected = false
            }
        }
        if(!isCanceled) {
            imageList[holder.adapterPosition].isSelected = !imageList[holder.adapterPosition].isSelected
        }
        list.postValue(imageList)
    }

    fun getImageList(context: Context){
        val returnValue = ArrayList<ImgPickData>()
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val columnIndexID: Int

        val projection = arrayOf(MediaStore.Images.Media._ID)

        val orderBy:String = MediaStore.Images.Media._ID

        val cursor = context.contentResolver.query(uriExternal, projection, null, null, orderBy)

        System.gc()

        if (cursor != null) {
            columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            if (cursor.moveToFirst()) {
                do {
                    returnValue.add(ImgPickData(
                        Uri.withAppendedPath(uriExternal, cursor.getString(columnIndexID))
                    ))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        returnValue.reverse()
        imageList.addAll(returnValue)
        list.postValue(returnValue)
    }

    //팝업
    fun imageSelected(v:View){
        for(data in imageList){
            if(data.isSelected){
                DevLog.e(data.imgUri)
                val i = Intent()
                i.putExtra("selectedImage", data.imgUri.toString())
                activity.setResult(Activity.RESULT_OK, i)
                activity.finish()
                return
            }
        }

        Snackbar.make(v, "하나 이상의 이미지를 선택하세요", Snackbar.LENGTH_LONG).show()
    }

}
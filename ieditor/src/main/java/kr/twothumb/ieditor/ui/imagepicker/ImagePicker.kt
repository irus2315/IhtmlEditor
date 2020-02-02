package kr.twothumb.ieditor.ui.imagepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import kr.twothumb.ieditor.R
import kr.twothumb.ieditor.databinding.ActivityImagePickerBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ImagePicker : AppCompatActivity() {
    private val bind by lazy {
        DataBindingUtil.setContentView<ActivityImagePickerBinding>(
            this@ImagePicker,
            R.layout.activity_image_picker
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind
        bind.vm = getViewModel()
        bind.lifecycleOwner = this
        bind.vm?.initActivity(this)
        bind.vm?.getImageList(this)
    }
}

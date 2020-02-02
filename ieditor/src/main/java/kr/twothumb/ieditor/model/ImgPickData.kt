package kr.twothumb.ieditor.model

import android.graphics.Bitmap
import android.net.Uri

class ImgPickData(var imgUri: Uri){
    private var position: Int = 0
    var isSelected = false
    lateinit var thumbnail: Bitmap

}
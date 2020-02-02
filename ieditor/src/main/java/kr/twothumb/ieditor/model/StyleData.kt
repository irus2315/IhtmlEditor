package kr.twothumb.ieditor.model

import android.text.style.StyleSpan

class StyleData(var style:StyleSpan, var start:Int, var end:Int):Cloneable{

    var content = ""
    public override fun clone():StyleData{
        return super.clone() as StyleData

    }
}
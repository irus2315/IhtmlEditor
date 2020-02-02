package kr.twothumb.ieditor.model

import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import kr.twothumb.ieditor.util.ToHtml
import kr.twothumb.lib.logger.DevLog

class EditorData{
    var hint:String
    var content:String
    var type: ContentType
    var currentFocus:Int
    var fontSize: FontSize
    var align:Align
    var styleList:ArrayList<StyleData> = ArrayList()

    private var ssb = SpannableStringBuilder()
    var start:Int = 0
    var end:Int = 0
    var uri: Uri?

    constructor(type: ContentType, uri: Uri):
            this(type, FontSize.T3, Align.NONE,"", 0, "", uri)
    constructor(type: ContentType, content:String):
            this(type, FontSize.T3,content)
    constructor(type: ContentType, fontSize: FontSize, content:String):
            this(type, fontSize, Align.NONE, content)

    constructor(type: ContentType, fontSize: FontSize, align:Align, content:String):
            this(type, fontSize, align, content, 0)
    constructor(type: ContentType, fontSize: FontSize, align:Align, content:String, currentFocus:Int):
            this(type, fontSize, align, content, currentFocus, "")

    constructor(type: ContentType, fontSize: FontSize, align:Align, content:String, currentFocus:Int, hint:String):
        this(type, fontSize, align, content, currentFocus, hint, null)

    constructor(type: ContentType, fontSize: FontSize, align:Align, content:String, currentFocus:Int, hint:String, uri: Uri?){
        this.hint = hint
        this.type = type
        this.content = content
        this.currentFocus = currentFocus
        this.fontSize = fontSize
        this.align = align
        this.uri = uri
    }

    fun setStyle():SpannableStringBuilder{
//        ssb = SpannableStringBuilder(ToHtml().stripHtml(content))
        ssb = SpannableStringBuilder(ToHtml().toHtml(content))
        with(styleList.iterator()){
            forEach {
                when {
                    it.start == it.end -> {
                        remove()
                    }
                    it.start > content.length -> {
                        remove()
                    }
                    it.end > content.length -> {
                        remove()
                    }
                }
            }
        }

        for(styleSpan in styleList){
            try {
                ssb.setSpan(
                    styleSpan.style,
                    styleSpan.start,
                    styleSpan.end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
            catch (e:IndexOutOfBoundsException){
            }
        }
        return ssb
    }

    fun setBold(){
        var selStart:Int = start
        var selEnd:Int = end
        var isBoldExpand = false

        for(style in styleList){
            if(style.style.style == Typeface.BOLD) {
                if(start < style.start && end > style.end){
                    isBoldExpand = true
                    style.start = start
                    style.end = end
                }
                else if (start in style.start..style.end && end in style.start..style.end) {
                    isBoldExpand = false
                    selStart = end
                    selEnd = style.end
                    style.end = start
                }
                else if (start < style.start && end in style.start..style.end){
                    isBoldExpand = true
                    style.start = start
                    style.end = style.end
                }
                else if (end > style.end && start in style.start..style.end){
                    isBoldExpand = true
                    style.start = start
                    style.end = style.end
                }
            }
        }

        styleList = ToHtml().distinctStyle(styleList)
        with(styleList.iterator()){
            forEach {
                when {
                    it.start == it.end -> remove()
                    it.start > content.length -> remove()
                    it.end > content.length -> remove()
                }
            }
        }

        val sortedList = styleList.distinctBy { Pair(it.start, it.end)}
        styleList = ArrayList(sortedList.reversed())
        currentFocus = end
        if(!isBoldExpand) {
            styleList.add(StyleData(StyleSpan(Typeface.BOLD), selStart, selEnd))
        }
    }

    fun cloneStyle():ArrayList<StyleData>{
        val returnValue:ArrayList<StyleData> = ArrayList()
        for(style in styleList){
            returnValue.add(style.clone())
        }

        return returnValue
    }
}